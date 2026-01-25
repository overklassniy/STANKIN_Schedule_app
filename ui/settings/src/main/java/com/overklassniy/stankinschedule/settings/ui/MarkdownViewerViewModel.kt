package com.overklassniy.stankinschedule.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.overklassniy.stankinschedule.core.ui.components.UIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MarkdownViewerViewModel : ViewModel() {

    private val _markdownContent = MutableStateFlow<UIState<String>>(UIState.loading())
    val markdownContent: StateFlow<UIState<String>> = _markdownContent.asStateFlow()

    private val client = OkHttpClient()

    fun loadMarkdown(url: String, force: Boolean = false) {
        if (!force && _markdownContent.value is UIState.Success) {
            return
        }

        viewModelScope.launch {
            _markdownContent.value = UIState.loading()
            try {
                val markdown = withContext(Dispatchers.IO) {
                    val request = Request.Builder()
                        .url(url)
                        .build()

                    val response = client.newCall(request).execute()
                    
                    try {
                        if (response.isSuccessful) {
                            response.body?.string() ?: ""
                        } else {
                            throw IOException("HTTP ${response.code}: ${response.message}")
                        }
                    } finally {
                        response.close()
                    }
                }
                _markdownContent.value = UIState.Success(markdown)
            } catch (e: Exception) {
                _markdownContent.value = UIState.Failed(e)
            }
        }
    }
}
