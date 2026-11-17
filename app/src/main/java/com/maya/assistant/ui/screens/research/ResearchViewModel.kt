package com.maya.assistant.ui.screens.research

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import com.maya.assistant.ai.research.ResearchReport
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResearchViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val researchEngine = mayaApp.browserAndResearchEngine

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _report = MutableStateFlow<ResearchReport?>(null)
    val report: StateFlow<ResearchReport?> = _report.asStateFlow()

    fun updateQuery(text: String) {
        _query.value = text
    }

    fun performResearch() {
        val q = _query.value.trim()
        if (q.isBlank() || _isSearching.value) return

        _isSearching.value = true
        viewModelScope.launch {
            delay(1500)
            val result = researchEngine.performResearch(q)
            _report.value = result
            _isSearching.value = false
        }
    }
}
