package com.overklassniy.stankinschedule.journal.predict.ui.paging

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.overklassniy.stankinschedule.journal.predict.ui.model.PredictMark

/**
 * Адаптер для списка предсказаний: заголовки дисциплин и элементы ввода оценок.
 *
 * @param onMarkChange Коллбэк изменения значения оценки.
 * @param onItemCountChanged Коллбэк изменения количества элементов.
 */
class PredictAdapter(
    private val onMarkChange: (mark: PredictMark, value: Int) -> Unit,
    private var onItemCountChanged: (count: Int) -> Unit,
) : RecyclerView.Adapter<ComposeRecyclerHolder>() {

    private var fullData: Map<String, List<PredictMark>> = emptyMap()
    private var data: List<PredictItem> = emptyList()
    private var showExposed: Boolean = true

    /**
     * Передаёт полный набор данных для адаптера.
     *
     * @param data Карта «дисциплина → список оценок».
     */
    fun submitData(data: Map<String, List<PredictMark>>) {
        if (this.fullData != data) {
            this.fullData = data
            updateData()
        }
    }

    /**
     * Переключает режим отображения открытых оценок.
     *
     * @param showExposed Показывать ли оценки с isExposed=false.
     */
    fun showExposedItems(showExposed: Boolean) {
        if (this.showExposed != showExposed) {
            this.showExposed = showExposed
            updateData()
        }
    }

    /**
     * Строит плоский список элементов (заголовки/контент) согласно текущим настройкам.
     * Выполняет DiffUtil и уведомляет слушателя о новом размере.
     */
    private fun updateData() {
        val newData = if (showExposed) {
            fullData.flatMap {
                listOf(HeaderItem(it.key)) + it.value.map { mark -> ContentItem(mark) }
            }
        } else {
            fullData.flatMap {
                val items = it.value
                    .filter { mark -> !mark.isExposed }
                    .map { mark -> ContentItem(mark) }

                if (items.isEmpty()) {
                    emptyList()
                } else {
                    listOf(HeaderItem(it.key)) + items
                }
            }
        }

        val diffCallback = PredictDiffCallback(data, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        data = newData
        onItemCountChanged(data.size)
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * DiffUtil.Callback для сравнения элементов списка предсказаний.
     */
    private class PredictDiffCallback(
        private val oldList: List<PredictItem>,
        private val newList: List<PredictItem>
    ) : DiffUtil.Callback() {

        /**
         * Возвращает размер старого списка для DiffUtil.
         */
        override fun getOldListSize(): Int = oldList.size

        /**
         * Возвращает размер нового списка для DiffUtil.
         */
        override fun getNewListSize(): Int = newList.size

        /**
         * Сравнивает элементы по идентичности (ключи), не по содержимому.
         * Заголовки — по названию дисциплины, контент — по дисциплине и типу оценки.
         */
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return when {
                oldItem is HeaderItem && newItem is HeaderItem ->
                    oldItem.discipline == newItem.discipline

                oldItem is ContentItem && newItem is ContentItem ->
                    oldItem.mark.discipline == newItem.mark.discipline &&
                            oldItem.mark.type == newItem.mark.type

                else -> false
            }
        }

        /**
         * Сравнивает элементы по содержимому для оптимизации обновлений.
         * Заголовки — по названию, контент — по модели [PredictMark].
         */
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return when {
                oldItem is HeaderItem && newItem is HeaderItem ->
                    oldItem.discipline == newItem.discipline

                oldItem is ContentItem && newItem is ContentItem ->
                    oldItem.mark == newItem.mark

                else -> false
            }
        }
    }

    /**
     * Создаёт ViewHolder соответствующего типа.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComposeRecyclerHolder {
        return when (viewType) {
            CONTENT_TYPE -> PredictContentHolder(onMarkChange, ComposeView(parent.context))
            HEADER_TYPE -> PredictHeaderHolder(ComposeView(parent.context))
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    /**
     * Очищает композицию при рециклинге.
     */
    override fun onViewRecycled(holder: ComposeRecyclerHolder) {
        super.onViewRecycled(holder)
        holder.composeView.disposeComposition()
    }

    /**
     * Привязывает элемент к соответствующему ViewHolder.
     */
    override fun onBindViewHolder(holder: ComposeRecyclerHolder, position: Int) {
        val item = data[position]
        when {
            holder is PredictContentHolder && item is ContentItem -> {
                holder.bind(item)
            }

            holder is PredictHeaderHolder && item is HeaderItem -> {
                holder.bind(item)
            }
        }
    }

    /**
     * Возвращает тип элемента по позиции.
     */
    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is ContentItem -> CONTENT_TYPE
            is HeaderItem -> HEADER_TYPE
        }
    }

    /**
     * Возвращает количество элементов.
     */
    override fun getItemCount(): Int = data.size

    /**
     * Маркерный интерфейс элементов адаптера.
     */
    sealed interface PredictItem

    /**
     * Элемент содержимого: оценка по дисциплине и типу.
     */
    class ContentItem(val mark: PredictMark) : PredictItem

    /**
     * Элемент заголовка: название дисциплины.
     */
    class HeaderItem(val discipline: String) : PredictItem

    companion object {
        private const val CONTENT_TYPE = 0
        private const val HEADER_TYPE = 1
    }
}