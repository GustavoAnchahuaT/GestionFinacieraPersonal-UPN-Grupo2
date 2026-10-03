package com.incasolutions.finanzaspersonales.viewmodel

// ============================================================
// REEMPLAZA tu LoginViewModel.kt actual en:
// PROYECTO/app/src/main/java/com/incasolutions/finanzaspersonales/viewmodel/LoginViewModel.kt
//
// CAMBIO: se agregó la validación de que la contraseña tenga al
// menos 8 caracteres (igual a lo que pide el informe en HU-01),
// antes de intentar conectarse al servidor.
// ============================================================

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.incasolutions.finanzaspersonales.repository.FinanzasRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = FinanzasRepository()

    private val _loginState = MutableLiveData<UiState<Unit>>()
    val loginState: LiveData<UiState<Unit>> = _loginState

    fun login(usuario: String, clave: String) {
        if (usuario.isEmpty() || clave.isEmpty()) {
            _loginState.value = UiState.Error("Completa usuario y clave")
            return
        }

        if (clave.length < 8) {
            _loginState.value = UiState.Error("La contraseña debe tener al menos 8 caracteres")
            return
        }

        _loginState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val respuesta = repository.login(usuario, clave)
                val body = respuesta.body()
                if (respuesta.isSuccessful && body?.success == true) {
                    _loginState.value = UiState.Success(Unit)
                } else {
                    _loginState.value = UiState.Error(body?.message ?: "Usuario o clave incorrectos")
                }
            } catch (e: Exception) {
                _loginState.value = UiState.Error(
                    "No se pudo conectar al servidor. Verifica que XAMPP esté encendido y el celular/emulador tenga conexión."
                )
            }
        }
    }
}
