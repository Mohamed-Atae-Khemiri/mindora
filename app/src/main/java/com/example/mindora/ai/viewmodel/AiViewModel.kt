package com.example.mindora.ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindora.ai.data.AiRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

enum class AiMessageRole { USER, ASSISTANT }

data class AiMessage(
    val id: Long,
    val role: AiMessageRole,
    val text: String
)

data class AiUiState(
    val isGenerating: Boolean = false,
    val messages: List<AiMessage> = emptyList(),
    val error: String? = null,
    val isOffline: Boolean = false,
    val lastFailedInput: String? = null
)

class AiViewModel(
    application: Application,
    private val repository: AiRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AiUiState())
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    fun sendMessage(input: String) {
        val prompt = input.trim()
        val current = _uiState.value
        if (prompt.isEmpty() || current.isGenerating) return

        val userMessage = AiMessage(
            id = System.currentTimeMillis(),
            role = AiMessageRole.USER,
            text = prompt
        )
        val assistantMessage = AiMessage(
            id = System.currentTimeMillis() + 1L,
            role = AiMessageRole.ASSISTANT,
            text = ""
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage + assistantMessage,
                isGenerating = true,
                error = null,
                isOffline = false,
                lastFailedInput = null
            )
        }

        viewModelScope.launch {
            try {
                repository.sendMessageStream(prompt)
                    .catch { throwable ->
                        val errorMessage = mapErrorToUserMessage(throwable)
                        val isOfflineError = throwable is IOException || errorMessage.contains("internet", ignoreCase = true)
                        _uiState.update { state ->
                            val lastIndex = state.messages.indexOfLast { it.role == AiMessageRole.ASSISTANT }
                            val updatedMessages = if (lastIndex >= 0 && state.messages[lastIndex].text.isEmpty()) {
                                state.messages.subList(0, lastIndex)
                            } else {
                                state.messages
                            }
                            state.copy(
                                messages = updatedMessages,
                                error = errorMessage,
                                isOffline = isOfflineError,
                                lastFailedInput = prompt
                            )
                        }
                    }
                    .collect { chunk ->
                        _uiState.update { state ->
                            val lastAssistantIndex = state.messages.indexOfLast {
                                it.role == AiMessageRole.ASSISTANT
                            }
                            if (lastAssistantIndex < 0) state
                            else state.copy(
                                messages = state.messages.toMutableList().also { list ->
                                    val last = list[lastAssistantIndex]
                                    list[lastAssistantIndex] = last.copy(text = last.text + chunk)
                                }
                            )
                        }
                    }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (throwable: Throwable) {
                val errorMessage = mapErrorToUserMessage(throwable)
                _uiState.update { state ->
                    state.copy(
                        error = errorMessage,
                        lastFailedInput = prompt
                    )
                }
            } finally {
                _uiState.update { it.copy(isGenerating = false) }
            }
        }
    }

    fun retryLastMessage() {
        val failed = _uiState.value.lastFailedInput
        if (!failed.isNullOrBlank()) {
            sendMessage(failed)
        }
    }

    fun startNewConversation() {
        if (_uiState.value.isGenerating) return
        viewModelScope.launch {
            try {
                repository.resetConversation()
                _uiState.update { AiUiState() }
            } catch (error: Exception) {
                _uiState.update { it.copy(error = error.localizedMessage ?: "Could not reset conversation.") }
            }
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun mapErrorToUserMessage(throwable: Throwable): String {
        val message = generateSequence(throwable) { it.cause }
            .mapNotNull { it.message }
            .joinToString(" ")

        return when {
            throwable is IOException || message.contains("No internet", ignoreCase = true) ||
                message.contains("Unable to resolve host", ignoreCase = true) ->
                "No internet connection. Tasks, subjects, and progress remain available offline."
            message.contains("API key not valid", ignoreCase = true) ||
                message.contains("API_KEY_INVALID", ignoreCase = true) ||
                message.contains("API key", ignoreCase = true) && message.contains("valid", ignoreCase = true) ->
                "Mindora AI is not connected to a valid Firebase project. Replace app/google-services.json with the Android config for com.example.mindora and enable Firebase AI Logic."
            message.contains("App Check", ignoreCase = true) ||
                message.contains("debug token", ignoreCase = true) ->
                "Firebase App Check rejected this app. Register its debug token in Firebase Console, or verify the Play Integrity App Check setup."
            message.contains("429", ignoreCase = true) ||
                message.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ||
                message.contains("Quota", ignoreCase = true) ->
                "Gemini quota reached. Please wait a moment before sending another message."
            message.contains("PERMISSION_DENIED", ignoreCase = true) ->
                "Firebase denied this AI request. Check that Firebase AI Logic is enabled for this project and that its API restrictions allow this Android app."
            else ->
                throwable.localizedMessage ?: "Mindora could not complete that AI request. Please try again."
        }
    }
}
