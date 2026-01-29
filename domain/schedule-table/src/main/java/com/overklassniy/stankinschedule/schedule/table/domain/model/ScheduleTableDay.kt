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
                    rawCells.add(RawTableCell(ids, rowIndex, 1, columnIndex, duration))

                    var prevRow = rowIndex - 1 >= 0
                    var nextRow = rowIndex + 1 < rows.size

                    for (k in 0 until duration) {
                        rows[rowIndex][columnIndex + k].addAll(ids)
                        if (nextRow && rows[rowIndex + 1][columnIndex + k].isNotEmpty()) {
                            nextRow = false
                        }
                        if (prevRow && rows[rowIndex - 1][columnIndex + k].isNotEmpty()) {
                            prevRow = false
                        }
                    }

                    var m = rowIndex + 1
                    while (nextRow) {
                        nextRow = m + 1 < rows.size
                        for (k in 0 until duration) {
                            rows[m][columnIndex + k].addAll(ids)
                            if (nextRow && rows[m + 1][columnIndex + k].isNotEmpty()) {
                                nextRow = false
                            }
                        }
                        ++m
                        ++rawCells.last().rowSpan
                    }

                    var n = rowIndex - 1
                    while (prevRow) {
                        prevRow = n - 1 >= 0
                        for (k in 0 until duration) {
                            rows[n][columnIndex + k].addAll(ids)
                            if (prevRow && rows[n - 1][columnIndex + k].isNotEmpty()) {
                                prevRow = false
                            }
                        }
                        --n
                        rawCells.last().apply {
                            ++rowSpan
                            --row
                        }
                    }
                }
            }
        }

        var overIndex = pairs.size
        for (rowIndex in rows.indices) {
            for (columnIndex in rows[rowIndex].indices) {
                if (rows[rowIndex][columnIndex].isEmpty()) {
                    var prevRow = rowIndex - 1 >= 0
                    var nextRow = rowIndex + 1 < rows.size

                    rows[rowIndex][columnIndex].add(overIndex++)
                    rawCells.add(
                        RawTableCell(
                            ids = rows[rowIndex][columnIndex],
                            row = rowIndex,
                            rowSpan = 1,
                            column = columnIndex,
                            columnSpan = 1
                        ),
                    )

                    if (nextRow && rows[rowIndex + 1][columnIndex].isNotEmpty()) {
                        nextRow = false
                    }

                    if (prevRow && rows[rowIndex - 1][columnIndex].isNotEmpty()) {
                        prevRow = false
                    }

                    var m = rowIndex + 1
                    while (nextRow) {
                        nextRow = m + 1 < rows.size
                        rows[m][columnIndex].addAll(rows[rowIndex][columnIndex])
                        ++rawCells.last().rowSpan
                        ++m
                    }

                    var n = rowIndex - 1
                    while (prevRow) {
                        prevRow = n - 1 >= 0
                        rows[n][columnIndex].addAll(rows[rowIndex][columnIndex])
                        ++rawCells.last().rowSpan
                        --rawCells.last().row
                        --n
                    }
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

                if (pairsInTargetCell.isNotEmpty()
                    && pairsInTargetCell.first().time.duration == pair.time.duration
                    && isMerge(pairsInTargetCell, pair)
                ) {
                    idsInCell.add(id)
                    isInsert = true
                    break
                } else {
                    var isFree = true
                    for (cell in line) {
                        val pairsInCell = pairsFromIds(cell)
                        if (pairsInCell.isNotEmpty()
                            && pairsInCell.first().time.isIntersect(pair.time)
                        ) {
                            isFree = false
                            break
                        }
                    }

                    if (isFree) {
                        idsInCell.add(id)
                        isInsert = true
                        break
                    }
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