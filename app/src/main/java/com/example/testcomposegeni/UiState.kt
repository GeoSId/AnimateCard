package com.example.testcomposegeni

sealed interface UiState {

    data object Initial : UiState

    data object Loading : UiState

    data class Success(val outputText: String) : UiState

    data class Error(val errorMessage: String) : UiState
}