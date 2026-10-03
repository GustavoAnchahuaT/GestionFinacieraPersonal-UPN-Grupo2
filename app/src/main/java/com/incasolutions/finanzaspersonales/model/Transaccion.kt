package com.incasolutions.finanzaspersonales.model

import com.google.gson.annotations.SerializedName

/**
 * Representa un movimiento financiero (ingreso o gasto).
 * Coincide con las columnas de la tabla `transacciones` en MySQL.
 */
data class Transaccion(
    val id: Int? = null,
    val monto: Double,
    val tipo: String,          // "ingreso" | "gasto"
    @SerializedName("categoria_id")
    val categoriaId: Int,
    @SerializedName("categoria_nombre")
    val categoriaNombre: String? = null,
    val descripcion: String? = null,
    @SerializedName("es_recurrente")
    val esRecurrente: Int = 0,  // 0 = no, 1 = sí (viene de un CheckBox)
    val fecha: String           // formato "yyyy-MM-dd"
)
