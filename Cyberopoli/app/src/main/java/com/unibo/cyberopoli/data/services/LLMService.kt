package com.unibo.cyberopoli.data.services

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class LLMService(
    private val baseUrl: String = "http://213.165.71.13:8080",
) {
    private val json = Json { ignoreUnknownKeys = true }

    private val client = HttpClient(OkHttp) {
        install(Logging) {
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    Log.d("TEST Ktor-Logging", message)
                }
            }
            level = io.ktor.client.plugins.logging.LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            connectTimeoutMillis = 20_000
            socketTimeoutMillis = 120_000
        }
    }

    @Serializable
    private data class LLMGenerateRequest(
        val model: String,
        val prompt: String,
        val stream: Boolean = false,
    )

    suspend fun generate(
        model: String,
        prompt: String,
        stream: Boolean = false,
    ): String {
        val url = "$baseUrl/api/generate"
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(LLMGenerateRequest(model, prompt, stream))
        }

        if (!response.status.isSuccess()) {
            val err = response.bodyAsText()
            throw RuntimeException("Ollama API error ${response.status}: $err")
        }

        val channel: ByteReadChannel = response.bodyAsChannel()
        val sb = StringBuilder()

        while (!channel.isClosedForRead) {
            val line = channel.readUTF8Line() ?: break
            if (line.isBlank()) continue

            try {
                val jsonObj = Json.parseToJsonElement(line).jsonObject
                val fragment = jsonObj["response"]?.jsonPrimitive?.content ?: ""
                sb.append(fragment)
            } catch (_: Exception) {
            }
        }

        val raw = sb.toString()

        val start = raw.indexOf('[').takeIf { it >= 0 } ?: 0
        val end = raw.lastIndexOf(']').takeIf { it >= 0 } ?: (raw.length - 1)

        val jsonArray = if (start < end) {
            raw.substring(start, end + 1)
        } else {
            raw
        }

        return jsonArray.trim()
    }
}
