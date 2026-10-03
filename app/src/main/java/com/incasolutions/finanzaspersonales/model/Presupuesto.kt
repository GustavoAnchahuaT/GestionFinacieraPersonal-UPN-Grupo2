package com.incasolutions.finanzaspersonales.model

// ============================================================
// ARCHIVO NUEVO — copiar a:
// PROYECTO/app/src/main/java/com/incasolutions/finanzaspersonales/model/Presupuesto.kt
//
// Representa el presupuesto mensual guardado en la tabla
// "presupuestos" de MySQL (anio, mes, monto).
// ============================================================

data class Presupuesto(
    val id: Int? = null,
    val anio: Int,
    val mes: Int,
    val monto: Double
)
