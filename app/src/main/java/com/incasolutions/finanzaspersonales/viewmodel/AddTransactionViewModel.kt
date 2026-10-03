package com.incasolutions.finanzaspersonales.viewmodel

// ============================================================
// ARCHIVO NUEVO — copiar a:
// PROYECTO/app/src/main/java/com/incasolutions/finanzaspersonales/viewmodel/AddTransactionViewModel.kt
// ============================================================

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incasolutions.finanzaspersonales.model.Categoria
import com.incasolutions.finanzaspersonales.repository.FinanzasRepository
import kotlinx.coroutines.launch

private const val MSG_ERROR_CONEXION =
    "No se pudo conectar con el servidor. Verifica que XAMPP esté activo"

class AddTransactionViewModel : ViewModel() {

    private val repository = FinanzasRepository()

    private val _categorias = MutableLiveData<UiState<List<Categoria>>>()
    val categorias: LiveData<UiState<List<Categoria>>> = _categorias

    private val _guardarState = MutableLiveData<UiState<Unit>>()
    val guardarState: LiveData<UiState<Unit>> = _guardarState

    fun cargarCategorias() {
        _categorias.value = UiState.Loading
        viewModelScope.launch {
            try {
                val respuesta = repository.getCategorias()
                if (respuesta.isSuccessful && respuesta.body()?.success == true) {
                    _categorias.value = UiState.Success(respuesta.body()?.data ?: emptyList())
                } else {
                    _categorias.value = UiState.Error(MSG_ERROR_CONEXION)
                }
            } catch (e: Exception) {
                _categorias.value = UiState.Error(MSG_ERROR_CONEXION)
            }
        }
    }

    fun guardarTransaccion(
        monto: Double,
        tipo: String,
        categoriaId: Int,
        descripcion: String?,
        esRecurrente: Int,
        fecha: String
    ) {
        _guardarState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val respuesta = repository.addTransaccion(
                    monto, tipo, categoriaId, descripcion, esRecurrente, fecha
                )
                if (respuesta.isSuccessful && respuesta.body()?.success == true) {
                    _guardarState.value = UiState.Success(Unit)
                } else {
                    _guardarState.value = UiState.Error(MSG_ERROR_CONEXION)
                }
            } catch (e: Exception) {
                _guardarState.value = UiState.Error(MSG_ERROR_CONEXION)
            }
        }
    }
}
