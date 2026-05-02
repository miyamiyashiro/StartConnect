package com.example.startconnect

data class NotificacaoResponse(
    val mensagemId: Int,
    val remetenteId: Int,
    val startupId: Int,
    val texto: String,
    val dataEnvio: String,
    val startupNome: String,
    val remetenteNome: String
)
