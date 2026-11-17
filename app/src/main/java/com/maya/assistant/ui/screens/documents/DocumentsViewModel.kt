package com.maya.assistant.ui.screens.documents

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import com.maya.assistant.ai.document.DocumentFormat
import com.maya.assistant.ai.document.DocumentSummaryResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DocumentsViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val docEngine = mayaApp.documentsEngine

    private val _sampleDocumentName = MutableStateFlow("Maya_Architecture_Spec.md")
    val sampleDocumentName: StateFlow<String> = _sampleDocumentName.asStateFlow()

    private val _documentContent = MutableStateFlow(
        """
        # MAYA AI System Specification
        MAYA AI is an autonomous, female AI Assistant built with Jetpack Compose, Material 3, and Clean Architecture.
        
        ## Features
        1. Universal AI Provider Manager with Free-Tier protection.
        2. Multi-model orchestrator with quota-exhaustion fallback.
        3. Dedicated OpenCode engine for engineering tasks.
        4. Real-time neural Text-to-Speech and speech recognition.
        5. Deep web research and document synthesis.
        """.trimIndent()
    )
    val documentContent: StateFlow<String> = _documentContent.asStateFlow()

    private val _summaryResult = MutableStateFlow<DocumentSummaryResult?>(null)
    val summaryResult: StateFlow<DocumentSummaryResult?> = _summaryResult.asStateFlow()

    private val _exportMessage = MutableStateFlow<String?>(null)
    val exportMessage: StateFlow<String?> = _exportMessage.asStateFlow()

    fun summarize() {
        val summary = docEngine.summarizeDocument(_sampleDocumentName.value, _documentContent.value)
        _summaryResult.value = summary
    }

    fun exportDocument(format: DocumentFormat) {
        val (fileName, content) = docEngine.exportDocument("MAYA_Summary", _documentContent.value, format)
        _exportMessage.value = "Exported as $fileName successfully!"
    }
}
