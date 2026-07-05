package com.phishguard.ai

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BrowserViewModel : ViewModel() {
    
    private val phishingDetector = BedrockPhishingDetector()
    
    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState
    
    private val _detectionResult = MutableLiveData<DetectionResult>()
    val detectionResult: LiveData<DetectionResult> = _detectionResult
    
    sealed class LoadingState {
        object Idle : LoadingState()
        object Loading : LoadingState()
        object Analyzing : LoadingState()
        object Complete : LoadingState()
    }
    
    sealed class DetectionResult {
        object Safe : DetectionResult()
        data class Phishing(val explanation: String, val confidence: Float) : DetectionResult()
        data class Error(val message: String) : DetectionResult()
    }
    
    fun analyzeUrl(url: String, pageContent: String = "") {
        _loadingState.value = LoadingState.Analyzing
        
        viewModelScope.launch {
            try {
                val result = phishingDetector.analyzeUrl(url, pageContent)
                
                _detectionResult.value = if (result.isSafe) {
                    DetectionResult.Safe
                } else {
                    DetectionResult.Phishing(result.explanation, result.confidence)
                }
                
                _loadingState.value = LoadingState.Complete
            } catch (e: Exception) {
                _detectionResult.value = DetectionResult.Error(e.message ?: "Analysis failed")
                _loadingState.value = LoadingState.Complete
            }
        }
    }
    
    fun setLoadingState(state: LoadingState) {
        _loadingState.value = state
    }
}