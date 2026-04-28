package com.example.startconnect

data class LoginResponse(
    val usuarioId: Int,
    val usuarioNome: String,
    val usuarioEmail: String,
    val usuarioCpf: String?,
    val usuarioTipo: String
)
