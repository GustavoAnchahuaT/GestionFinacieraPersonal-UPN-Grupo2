package com.incasolutions.finanzaspersonales.viewmodel

// ============================================================
// ARCHIVO NUEVO — copiar a:
// PROYECTO/app/src/main/java/com/incasolutions/finanzaspersonales/viewmodel/MainViewModel.kt
// ============================================================

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incasolutions.finanzaspersonales.model.ApiResponse
import com.incasolutions.finanzaspersonales.model.Transaccion
import com.incasolutions.finanzaspersonales.repository.FinanzasRepository
import kotlinx.coroutines.launch
import retrofit2.Response

private const val MSG_ERROR_CONEXION =
    "No se pudo conectar con el servidor. Verifica que XAMPP esté activo"

class MainViewModel : ViewModel() {

    private val repository = FinanzasRepository()

    private val _transacciones = MutableLiveData<UiState<List<Transaccion>>>()
    val transacciones: LiveData<UiState<List<Transaccion>>> = _transacciones

    private val _deleteState = MutableLiveData<UiState<Unit>>()
    val deleteState: LiveData<UiState<Unit>> = _deleteState

    fun cargarPorMes(mes: Int, anio: Int) {
        cargar { repository.getTransaccionesPorMes(mes, anio) }
    }

    fun cargarPorRango(fechaInicio: String, fechaFin: String) {
        cargar { repository.getTransaccionesPorRango(fechaInicio, fechaFin) }
    }

    private fun cargar(llamada: suspend () -> Response<ApiResponse<List<Transaccion>>>) {
        _transacciones.value = UiState.Loading
        viewModelScope.launch {
            try {
                val respuesta = llamada()
                if (respuesta.isSuccessful && respuesta.body()?.success == true) {
                    _transacciones.value = UiState.Success(respuesta.body()?.data ?: emptyList())
                } else {
                    _transacciones.value = UiState.Error(MSG_ERROR_CONEXION)
                }
            } catch (e: Exception) {
                _transacciones.value = UiState.Error(MSG_ERROR_CONEXION)
            }
        }
    }

    fun eliminar(id: Int) {
        viewModelScope.launch {
            try {
                val respuesta = repository.deleteTransaccion(id)
                if (respuesta.isSuccessful && respuesta.body()?.success == true) {
                    _deleteState.value = UiState.Success(Unit)
                } else {
                    _deleteState.value = UiState.Error(MSG_ERROR_CONEXION)
                }
            } catch (e: Exception) {
                _deleteState.value = UiState.Error(MSG_ERROR_CONEXION)
            }
        }
    }
}
