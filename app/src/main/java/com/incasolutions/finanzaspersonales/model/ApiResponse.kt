package com.incasolutions.finanzaspersonales.model

/**
 * Envoltorio genérico de respuesta que usa la API en PHP.
 * Todos los endpoints devuelven JSON con esta forma:
 * { "success": true, "message": "...", "data": ... }
 */
data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)
