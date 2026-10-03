package com.incasolutions.finanzaspersonales.network

import com.incasolutions.finanzaspersonales.data.LoginResponse
import com.incasolutions.finanzaspersonales.model.ApiResponse
import com.incasolutions.finanzaspersonales.model.Categoria
import com.incasolutions.finanzaspersonales.model.Presupuesto
import com.incasolutions.finanzaspersonales.model.Transaccion
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Define los endpoints del backend PHP (carpeta backend_php/api).
 * Cada función corresponde a un archivo .php.
 */
interface ApiService {

    @GET("get_categorias.php")
    suspend fun getCategorias(): Response<ApiResponse<List<Categoria>>>

    @GET("get_transacciones.php")
    suspend fun getTransacciones(
        @Query("mes") mes: Int,
        @Query("anio") anio: Int
    ): Response<ApiResponse<List<Transaccion>>>

    // Trae transacciones por rango de fechas (dia especifico,
    // ultimos 3/6 meses, año completo, etc.) en vez de por mes/anio.
    @GET("get_transacciones.php")
    suspend fun getTransaccionesPorRango(
        @Query("fecha_inicio") fechaInicio: String,
        @Query("fecha_fin") fechaFin: String
    ): Response<ApiResponse<List<Transaccion>>>

    @FormUrlEncoded
    @POST("add_transaccion.php")
    suspend fun addTransaccion(
        @Field("monto") monto: Double,
        @Field("tipo") tipo: String,
        @Field("categoria_id") categoriaId: Int,
        @Field("descripcion") descripcion: String?,
        @Field("es_recurrente") esRecurrente: Int,
        @Field("fecha") fecha: String
    ): Response<ApiResponse<Transaccion>>

    @FormUrlEncoded
    @POST("delete_transaccion.php")
    suspend fun deleteTransaccion(
        @Field("id") id: Int
    ): Response<ApiResponse<Unit>>

    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(
        @Field("usuario") usuario: String,
        @Field("clave") clave: String
    ): Response<LoginResponse>

    // ---------- NUEVO: presupuesto mensual (reporte + alerta) ----------

    @GET("get_presupuesto.php")
    suspend fun getPresupuesto(
        @Query("anio") anio: Int,
        @Query("mes") mes: Int
    ): Response<ApiResponse<Presupuesto?>>

    @FormUrlEncoded
    @POST("guardar_presupuesto.php")
    suspend fun guardarPresupuesto(
        @Field("anio") anio: Int,
        @Field("mes") mes: Int,
        @Field("monto") monto: Double
    ): Response<ApiResponse<Presupuesto>>
}
