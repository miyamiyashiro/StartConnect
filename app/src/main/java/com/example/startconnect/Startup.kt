package com.example.startconnect

data class Startup(
    val startupId: Int,
    val nome: String,
    val segmento: String,
    val subtitulo: String,
    val tags: List<String>
)