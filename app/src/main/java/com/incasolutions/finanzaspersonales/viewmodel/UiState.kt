package com.incasolutions.finanzaspersonales.viewmodel

// ============================================================
// ARCHIVO NUEVO — copiar a:
// PROYECTO/app/src/main/java/com/incasolutions/finanzaspersonales/viewmodel/UiState.kt
//
// Representa el estado de una operación (carga de datos, login,
// guardado, eliminación) para que la Activity solo tenga que
// reaccionar a 3 casos: cargando, éxito o error. Evita repetir
// try/catch e if/else de conexión en cada pantalla.
// ============================================================

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
