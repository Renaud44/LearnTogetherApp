package com.android.projet.models

data class MessageGroupe(
    val senderId: String = "",
    val senderName: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)