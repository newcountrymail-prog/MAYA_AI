package com.maya.assistant.ui.screens.coding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maya.assistant.MayaApplication
import com.maya.assistant.ai.opencode.CodeGenerationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CodingStudioViewModel(application: Application) : AndroidViewModel(application) {

    private val mayaApp = application as MayaApplication
    private val openCodeEngine = mayaApp.openCodeEngine

    private val _codingPrompt = MutableStateFlow("")
    val codingPrompt: StateFlow<String> = _codingPrompt.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("Kotlin")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generatedCode = MutableStateFlow<CodeGenerationResult?>(null)
    val generatedCode: StateFlow<CodeGenerationResult?> = _generatedCode.asStateFlow()

    private val _testRunOutput = MutableStateFlow<String?>(null)
    val testRunOutput: StateFlow<String?> = _testRunOutput.asStateFlow()

    fun updatePrompt(text: String) {
        _codingPrompt.value = text
    }

    fun setLanguage(lang: String) {
        _selectedLanguage.value = lang
    }

    fun generateCode() {
        val prompt = _codingPrompt.value.trim()
        if (prompt.isBlank() || _isGenerating.value) return

        _isGenerating.value = true
        viewModelScope.launch {
            delay(1200)
            val result = openCodeEngine.generateCode(prompt, _selectedLanguage.value)
            _generatedCode.value = result
            _isGenerating.value = false
        }
    }

    fun runTests() {
        viewModelScope.launch {
            val (passed, report) = openCodeEngine.runUnitTests()
            _testRunOutput.value = report
        }
    }
}
