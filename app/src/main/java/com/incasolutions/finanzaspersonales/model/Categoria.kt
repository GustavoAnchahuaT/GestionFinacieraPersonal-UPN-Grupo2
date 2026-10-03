package com.incasolutions.finanzaspersonales.model

data class Categoria(
    val id: Int,
    val nombre: String,
    val tipo: String   // "ingreso" | "gasto" | "ambos"
)
