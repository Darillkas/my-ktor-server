package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: Int? = null,
    val userId: Int,
    val username: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)