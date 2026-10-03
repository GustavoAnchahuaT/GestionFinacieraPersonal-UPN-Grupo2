package com.incasolutions.finanzaspersonales.repository

// ============================================================
// REEMPLAZA el FinanzasRepository.kt que te mandó tu compañero en:
// PROYECTO/app/src/main/java/com/incasolutions/finanzaspersonales/repository/FinanzasRepository.kt
//
// Es el mismo archivo de tu compañero + 2 métodos nuevos al final
// (getPresupuesto, guardarPresupuesto) para el reporte con alerta.
// ============================================================

import com.incasolutions.finanzaspersonales.data.LoginResponse
import com.incasolutions.finanzaspersonales.model.ApiResponse
import com.incasolutions.finanzaspersonales.model.Categoria
import com.incasolutions.finanzaspersonales.model.Presupuesto
import com.incasolutions.finanzaspersonales.model.Transaccion
import com.incasolutions.finanzaspersonales.network.RetrofitClient
import retrofit2.Response

class FinanzasRepository {

    private val api = RetrofitClient.apiService

    suspend fun login(usuario: String, clave: String): Response<LoginResponse> =
        api.login(usuario, clave)

    suspend fun getCategorias(): Response<ApiResponse<List<Categoria>>> =
        api.getCategorias()

    suspend fun getTransaccionesPorMes(mes: Int, anio: Int): Response<ApiResponse<List<Transaccion>>> =
        api.getTransacciones(mes, anio)

    suspend fun getTransaccionesPorRango(
        fechaInicio: String,
        fechaFin: String
    ): Response<ApiResponse<List<Transaccion>>> =
        api.getTransaccionesPorRango(fechaInicio, fechaFin)

    suspend fun addTransaccion(
        monto: Double,
        tipo: String,
        categoriaId: Int,
        descripcion: String?,
        esRecurrente: Int,
        fecha: String
    ): Response<ApiResponse<Transaccion>> =
        api.addTransaccion(monto, tipo, categoriaId, descripcion, esRecurrente, fecha)

    suspend fun deleteTransaccion(id: Int): Response<ApiResponse<Unit>> =
        api.deleteTransaccion(id)

    // ---------- NUEVO: presupuesto mensual (reporte + alerta) ----------

    suspend fun getPresupuesto(anio: Int, mes: Int): Response<ApiResponse<Presupuesto?>> =
        api.getPresupuesto(anio, mes)

    suspend fun guardarPresupuesto(anio: Int, mes: Int, monto: Double): Response<ApiResponse<Presupuesto>> =
        api.guardarPresupuesto(anio, mes, monto)
}
