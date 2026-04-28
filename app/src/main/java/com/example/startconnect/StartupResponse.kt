package com.example.startconnect

data class StartupResponse(
    val startupId: Int,
    val nome: String,
    val segmento: String,
    val subtitulo: String,
    val tag1: String?,
    val tag2: String?,
    val tag3: String?,
    val tag4: String?
)
