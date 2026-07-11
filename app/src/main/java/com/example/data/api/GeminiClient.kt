package com.example.data.api

import com.example.BuildConfig
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>?
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun getChatResponse(prompt: String, chatHistory: List<Content> = emptyList()): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Please configure your GEMINI_API_KEY in the Secrets panel to activate Kashi AI Guide!"
        }

        val systemPrompt = """
            You are "Kashi AI", a friendly, knowledgeable, and culturally rich AI travel guide specializing in Banaras (Varanasi/Kashi). 
            Your goal is to assist travelers in planning their trips, learning about the ancient city's history, temples, Ganga Aarti, ghats, and recommending local activities and street food.
            Keep your responses helpful, highly informative, and warm, occasionally using terms like 'Namaste' or 'Har Har Mahadev' to capture the spiritual essence of Varanasi.
            Limit your length to be easy to read on mobile screens (1-2 concise paragraphs, or clean bullet points).
        """.trimIndent()

        // Combine history and current prompt
        val contentsList = chatHistory.toMutableList().apply {
            add(Content(parts = listOf(Part(text = prompt))))
        }

        val request = GenerateContentRequest(
            contents = contentsList,
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
        )

        return try {
            val response = service.generateContent(apiKey, request)
            val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            replyText ?: "No response from Kashi AI. Please try again."
        } catch (e: Exception) {
            "Error: Could not connect to Kashi AI. Please check your internet connection. (Details: ${e.localizedMessage})"
        }
    }
}
