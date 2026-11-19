package com.example.mindora.ai.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class FirebaseGeminiAiRepository(
    private val context: Context,
    private val modelName: String = DEFAULT_MODEL_NAME,
) : AiRepository {

    private val generativeModel by lazy {
        Firebase.ai.generativeModel(
            modelName = modelName,
            systemInstruction = content {
                text(
                    "You are Mindora, a friendly, encouraging, and highly structured study assistant. " +
                    "Help students plan their studies, break down tasks, summarize concepts, and stay motivated. " +
                    "Keep answers concise, clear, and easy to read on mobile devices, using bullet points when appropriate."
                )
            }
        )
    }

    private var chatSession = generativeModel.startChat()

    override fun sendMessageStream(prompt: String): Flow<String> = flow {
        if (!isNetworkAvailable(context)) {
            throw IOException("No internet connection. Mindora's non-AI features remain available offline, but the AI assistant needs internet access.")
        }
        val responseStream = chatSession.sendMessageStream(prompt)
        responseStream.collect { chunk ->
            chunk.text?.let { text ->
                if (text.isNotEmpty()) {
                    emit(text)
                }
            }
        }
    }

    override suspend fun resetConversation() {
        chatSession = generativeModel.startChat()
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    companion object {
        const val DEFAULT_MODEL_NAME = "gemini-2.5-flash"
    }
}
