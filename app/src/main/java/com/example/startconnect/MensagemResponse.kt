package com.example.startconnect

data class MensagemResponse(
    val mensagemId: Int,
    val remetenteId: Int,
    val destinatarioId: Int,
    val startupId: Int,
    val texto: String,
    val dataEnvio: String,
    val remetenteNome: String
)
