package com.example.mindora.ai.data

import kotlinx.coroutines.flow.Flow

/**
 * Provider-independent repository interface for Mindora's AI assistant.
 */
interface AiRepository {
    /**
     * Sends a user prompt to the AI model and returns a Flow of text chunks.
     */
    fun sendMessageStream(prompt: String): Flow<String>

    /**
     * Resets the active chat session context.
     */
    suspend fun resetConversation()
}
