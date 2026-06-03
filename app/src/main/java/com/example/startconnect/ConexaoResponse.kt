package com.example.startconnect

data class ConexaoResponse(
    val usuarioId: Int,
    val usuarioNome: String,
    val usuarioTipo: String,
    val totalFavoritos: Int
)