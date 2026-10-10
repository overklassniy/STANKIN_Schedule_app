package com.overklassniy.stankinschedule.schedule.table.domain.model

import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel

/**
 * Модель дня для таблицы расписания.
 *
 * Группирует пары по временным слотам и формирует ячейки с объединением строк/колонок.
 */
class ScheduleTableDay {

    private var lines: MutableList<MutableList<MutableList<Int>>> = mutableListOf()

    private var pairs: List<PairModel> = emptyList()

    /**
     * Устанавливает список пар и перераспределяет ячейки.
     *
     * @param pairs Список пар.
     */
    fun setPairs(pairs: List<PairModel>) {
        this.pairs = pairs.sortedWith(PAIR_COMPARATOR)
        reallocate()
    }

    /**
     * Строит список ячеек таблицы для отрисовки.
     *
     * @param pairsToText Преобразователь списка пар в текст ячейки.
     * @return Список ячеек [ScheduleTableCell].
     */
    fun cells(
        pairsToText: (pairs: List<PairModel>) -> String = { it.joinToString("\n") }
    ): List<ScheduleTableCell> {
        val rawCells = mutableListOf<RawTableCell>()
        val rows = lines.map { line -> line.map { ids -> ids.toMutableSet() } }

        for (rowIndex in rows.indices) {
            for (columnIndex in rows[rowIndex].indices) {
                val ids = rows[rowIndex][columnIndex]
                if (ids.isNotEmpty() && rawCells.all { it.ids != ids }) {
                    val pairsInCell = pairsFromIds(ids)
                    val duration = pairsInCell.first().time.duration
                    val cell = RawTableCell(ids, rowIndex, 1, columnIndex, duration)
                    rawCells.add(cell)
                    expandCell(rows, cell, duration)
                }
            }
        }

        var overIndex = pairs.size
        for (rowIndex in rows.indices) {
            for (columnIndex in rows[rowIndex].indices) {
                if (rows[rowIndex][columnIndex].isEmpty()) {
                    rows[rowIndex][columnIndex].add(overIndex++)
                    val cell = RawTableCell(
                        ids = rows[rowIndex][columnIndex],
                        row = rowIndex,
                        rowSpan = 1,
                        column = columnIndex,
                        columnSpan = 1
                    )
                    rawCells.add(cell)
                    expandCell(rows, cell, 1)
                }
            }
        }

        return rawCells.map { rawCell ->
            ScheduleTableCell(
                row = rawCell.row,
                column = rawCell.column,
                text = pairsToText(pairsFromIds(rawCell.ids)),
                rowSpan = rawCell.rowSpan,
                columnSpan = rawCell.columnSpan
            )
        }
    }

    /**
     * Заполняет ячейку таблицы и растягивает её по строкам вниз и вверх,
     * пока соседние строки свободны в колонках длительности [duration].
     *
     * @param rows Матрица идентификаторов пар (строки × колонки).
     * @param cell Ячейка, чьи `row`/`rowSpan` обновляются в процессе.
     * @param duration Ширина ячейки в колонках.
     */
    private fun expandCell(
        rows: List<List<MutableSet<Int>>>,
        cell: RawTableCell,
        duration: Int,
    ) {
        for (k in 0 until duration) {
            rows[cell.row][cell.column + k].addAll(cell.ids)
        }

        var m = cell.row + 1
        while (m < rows.size && (0 until duration).all { rows[m][cell.column + it].isEmpty() }) {
            for (k in 0 until duration) {
                rows[m][cell.column + k].addAll(cell.ids)
            }
            ++m
            ++cell.rowSpan
        }

        var n = cell.row - 1
        while (n >= 0 && (0 until duration).all { rows[n][cell.column + it].isEmpty() }) {
            for (k in 0 until duration) {
                rows[n][cell.column + k].addAll(cell.ids)
            }
            --n
            ++cell.rowSpan
            --cell.row
        }
    }

    /**
     * Преобразует набор индексов пар в список моделей пар.
     *
     * Индексы, выходящие за пределы списка, игнорируются.
     *
     * @param ids Набор индексов пар.
     * @return Список [PairModel], соответствующих индексам.
     */
    private fun pairsFromIds(ids: Iterable<Int>): List<PairModel> {
        return ids.mapNotNull { index -> if (index >= pairs.size) null else pairs[index] }
    }

    /**
     * Перераспределяет пары по строкам и колонкам дня.
     *
     * Формирует матрицу [lines] из идентификаторов пар с учётом длительности,
     * пересечений по времени и возможности объединения ячеек.
     */
    private fun reallocate() {
        lines.clear()
        lines.add(MutableList(COLUMNS) { mutableListOf() })

        pairs.forEachIndexed { id, pair ->
            var isInsert = false

            for (line in lines) {
                val idsInCell = line[pair.time.number()]
                val pairsInTargetCell = pairsFromIds(idsInCell)

                val canMerge = pairsInTargetCell.isNotEmpty()
                        && pairsInTargetCell.first().time.duration == pair.time.duration
                        && isMerge(pairsInTargetCell, pair)
                val isFree = line.none { cell ->
                    val pairsInCell = pairsFromIds(cell)
                    pairsInCell.isNotEmpty() && pairsInCell.first().time.isIntersect(pair.time)
                }

                if (canMerge || isFree) {
                    idsInCell.add(id)
                    isInsert = true
                    break
                }
            }

            if (!isInsert) {
                lines.add(MutableList(COLUMNS) { mutableListOf() })
                lines.last()[pair.time.number()].add(id)
            }
        }
    }

    /**
     * Проверяет возможность объединения пар в одной ячейке.
     *
     * @param pairs Уже находящиеся в ячейке пары.
     * @param pair Пара-кандидат.
     * @return true, если подгруппа совпадает и пары можно объединить.
     */
    private fun isMerge(pairs: List<PairModel>, pair: PairModel): Boolean {
        return pairs.any { p -> p.subgroup == pair.subgroup }
    }

    /**
     * Возвращает количество строк (слоёв) для текущего дня.
     *
     * @return Количество строк.
     */
    fun lines(): Int {
        return lines.size
    }

    companion object {

        private const val COLUMNS = 8

        private val PAIR_COMPARATOR
            get() = Comparator<PairModel> { o1, o2 -> o2.time.duration - o1.time.duration }
    }

    /**
     * Внутреннее представление «сырой» ячейки таблицы до преобразования.
     *
     * @property ids Индексы пар, отображаемых в ячейке.
     * @property row Стартовая строка.
     * @property rowSpan Растяжение по строкам.
     * @property column Стартовая колонка.
     * @property columnSpan Растяжение по колонкам.
     */
    private class RawTableCell(
        var ids: Iterable<Int>,
        var row: Int,
        var rowSpan: Int,
        var column: Int,
        var columnSpan: Int
    )
}
