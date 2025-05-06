package com.unibo.cyberopoli.data.services

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class HFService(private val token: String) {
    private val json = Json { ignoreUnknownKeys = true }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            connectTimeoutMillis = 20_000
            socketTimeoutMillis = 120_000
        }
    }

    suspend fun generateChat(
        model: String, userPrompt: String
    ): String {
        val url = "https://router.huggingface.co/novita/v3/openai/chat/completions"

        val request = ChatRequest(
            model = model, messages = listOf(ChatMessage(role = "user", content = userPrompt))
        )

        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $token")
            setBody(request)
        }

        val raw = response.bodyAsText()
        Log.d("HfService", "$url -> $raw")

        if (!response.status.isSuccess()) {
            throw RuntimeException("HuggingFace Chat API error ${response.status}: $raw")
        }

        val chatResponse = json.decodeFromString(ChatResponse.serializer(), raw)
        return chatResponse.choices.firstOrNull()?.message?.content ?: "💥 Errore: nessuna risposta"
    }
}

@Serializable
data class ChatMessage(
    val role: String, val content: String
)

@Serializable
data class ChatRequest(
    val model: String, val messages: List<ChatMessage>, val stream: Boolean = false
)

@Serializable
data class ChatChoice(
    val index: Int, val message: ChatMessage, val finish_reason: String? = null
)

@Serializable
data class ChatResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<ChatChoice>
)
