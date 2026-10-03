package com.incasolutions.finanzaspersonales.viewmodel

// ============================================================
// ARCHIVO NUEVO — copiar a:
// PROYECTO/app/src/main/java/com/incasolutions/finanzaspersonales/viewmodel/ReporteViewModel.kt
//
// Calcula el reporte gráfico (gasto por categoría, en % sobre el
// total gastado del mes) y compara el total gastado contra el
// presupuesto guardado, para que la Activity solo tenga que pintar
// lo que este ViewModel ya calculó.
// ============================================================

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incasolutions.finanzaspersonales.repository.FinanzasRepository
import kotlinx.coroutines.launch

data class CategoriaResumen(
    val nombre: String,
    val monto: Double,
    val porcentaje: Int
)

data class ReporteData(
    val categorias: List<CategoriaResumen>,
    val totalGastado: Double,
    val presupuesto: Double?,
    val porcentajeUsado: Int?
)

private const val MSG_ERROR_CONEXION =
    "No se pudo conectar con el servidor. Verifica que XAMPP esté activo"

class ReporteViewModel : ViewModel() {

    private val repository = FinanzasRepository()

    private val _reporte = MutableLiveData<UiState<ReporteData>>()
    val reporte: LiveData<UiState<ReporteData>> = _reporte

    private val _guardarPresupuestoState = MutableLiveData<UiState<Unit>>()
    val guardarPresupuestoState: LiveData<UiState<Unit>> = _guardarPresupuestoState

    fun cargarReporte(anio: Int, mes: Int) {
        _reporte.value = UiState.Loading
        viewModelScope.launch {
            try {
                val respuestaTransacciones = repository.getTransaccionesPorMes(mes, anio)
                val respuestaPresupuesto = repository.getPresupuesto(anio, mes)

                if (!respuestaTransacciones.isSuccessful || respuestaTransacciones.body()?.success != true) {
                    _reporte.value = UiState.Error(MSG_ERROR_CONEXION)
                    return@launch
                }

                val transacciones = respuestaTransacciones.body()?.data ?: emptyList()
                val gastos = transacciones.filter { it.tipo == "gasto" }
                val totalGastado = gastos.sumOf { it.monto }

                val categorias = gastos
                    .groupBy { it.categoriaNombre ?: "Sin categoría" }
                    .map { (nombre, lista) ->
                        val monto = lista.sumOf { it.monto }
                        val porcentaje = if (totalGastado > 0) ((monto / totalGastado) * 100).toInt() else 0
                        CategoriaResumen(nombre, monto, porcentaje)
                    }
                    .sortedByDescending { it.monto }

                val presupuesto: Double? =
                    if (respuestaPresupuesto.isSuccessful && respuestaPresupuesto.body()?.success == true)
                        respuestaPresupuesto.body()?.data?.monto
                    else null

                val porcentajeUsado = presupuesto?.let {
                    if (it > 0) ((totalGastado / it) * 100).toInt() else null
                }

                _reporte.value = UiState.Success(
                    ReporteData(categorias, totalGastado, presupuesto, porcentajeUsado)
                )
            } catch (e: Exception) {
                _reporte.value = UiState.Error(MSG_ERROR_CONEXION)
            }
        }
    }

    fun guardarPresupuesto(anio: Int, mes: Int, monto: Double) {
        _guardarPresupuestoState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val respuesta = repository.guardarPresupuesto(anio, mes, monto)
                if (respuesta.isSuccessful && respuesta.body()?.success == true) {
                    _guardarPresupuestoState.value = UiState.Success(Unit)
                    cargarReporte(anio, mes) // recarga para que la alerta se recalcule con el nuevo presupuesto
                } else {
                    _guardarPresupuestoState.value = UiState.Error(MSG_ERROR_CONEXION)
                }
            } catch (e: Exception) {
                _guardarPresupuestoState.value = UiState.Error(MSG_ERROR_CONEXION)
            }
        }
    }
}
