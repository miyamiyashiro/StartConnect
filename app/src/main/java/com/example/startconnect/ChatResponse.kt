package com.example.startconnect

data class ChatResponse(
    val startupId: Int,
    val startupNome: String,
    val ultimaMensagem: String,
    val dataUltimaMensagem: String,
    val outroUsuarioId: Int,
    val outroUsuarioNome: String
)
