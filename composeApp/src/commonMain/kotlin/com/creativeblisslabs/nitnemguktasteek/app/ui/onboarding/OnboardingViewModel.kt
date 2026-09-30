package com.creativeblisslabs.nitnemguktasteek.app.ui.onboarding

// 1. Add this exact import
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// 2. Extend the ViewModel class here
class OnboardingViewModel : ViewModel() {

    private val _state =
        MutableStateFlow(
            OnboardingState()
        )

    val state: StateFlow<OnboardingState> =
        _state.asStateFlow()

    fun nextPage() {
        _state.update {
            it.copy(
                page = it.page + 1
            )
        }
    }

    fun previousPage() {
        _state.update {
            it.copy(
                page = (it.page - 1).coerceAtLeast(0)
            )
        }
    }

    fun updateState(
        transform: (OnboardingState) -> OnboardingState
    ) {
        _state.update(transform)
    }
}