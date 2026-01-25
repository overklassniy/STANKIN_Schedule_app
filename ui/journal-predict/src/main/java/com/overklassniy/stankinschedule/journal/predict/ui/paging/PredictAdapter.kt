package com.overklassniy.stankinschedule.journal.predict.ui.paging

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.overklassniy.stankinschedule.journal.predict.ui.model.PredictMark

class PredictAdapter(
    private val onMarkChange: (mark: PredictMark, value: Int) -> Unit,
    private var onItemCountChanged: (count: Int) -> Unit,
) : RecyclerView.Adapter<ComposeRecyclerHolder>() {

    private var fullData: Map<String, List<PredictMark>> = emptyMap()
    private var data: List<PredictItem> = emptyList()
    private var showExposed: Boolean = true

    fun submitData(data: Map<String, List<PredictMark>>) {
        if (this.fullData != data) {
            this.fullData = data
            updateData()
        }
    }

    fun showExposedItems(showExposed: Boolean) {
        if (this.showExposed != showExposed) {
            this.showExposed = showExposed
            updateData()
        }
    }

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

    private class PredictDiffCallback(
        private val oldList: List<PredictItem>,
        private val newList: List<PredictItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComposeRecyclerHolder {
        return when (viewType) {
            CONTENT_TYPE -> PredictContentHolder(onMarkChange, ComposeView(parent.context))
            HEADER_TYPE -> PredictHeaderHolder(ComposeView(parent.context))
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onViewRecycled(holder: ComposeRecyclerHolder) {
        super.onViewRecycled(holder)
        holder.composeView.disposeComposition()
    }

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

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is ContentItem -> CONTENT_TYPE
            is HeaderItem -> HEADER_TYPE
        }
    }

    override fun getItemCount(): Int = data.size


    sealed interface PredictItem

    class ContentItem(val mark: PredictMark) : PredictItem

    class HeaderItem(val discipline: String) : PredictItem

    companion object {
        private const val CONTENT_TYPE = 0
        private const val HEADER_TYPE = 1
    }
}