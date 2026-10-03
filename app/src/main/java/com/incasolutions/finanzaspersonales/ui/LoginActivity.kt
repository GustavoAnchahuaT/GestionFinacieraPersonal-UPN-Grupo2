package com.incasolutions.finanzaspersonales.ui

// ============================================================
// REEMPLAZA tu LoginActivity.kt actual en:
// PROYECTO/app/src/main/java/com/incasolutions/finanzaspersonales/ui/LoginActivity.kt
//
// CAMBIO: ya no llama a RetrofitClient.apiService directamente.
// Ahora usa LoginViewModel (capa ViewModel del patrón MVVM), que
// a su vez usa FinanzasRepository (capa Repository).
// ============================================================

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.incasolutions.finanzaspersonales.databinding.ActivityLoginBinding
import com.incasolutions.finanzaspersonales.viewmodel.LoginViewModel
import com.incasolutions.finanzaspersonales.viewmodel.UiState

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnIngresar.setOnClickListener {
            val usuario = binding.etUsuario.text.toString().trim()
            val clave = binding.etClave.text.toString().trim()
            viewModel.login(usuario, clave)
        }

        viewModel.loginState.observe(this) { estado ->
            when (estado) {
                is UiState.Loading -> {
                    binding.btnIngresar.isEnabled = false
                }
                is UiState.Success -> {
                    binding.btnIngresar.isEnabled = true
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
                is UiState.Error -> {
                    binding.btnIngresar.isEnabled = true
                    mostrarAlerta(estado.message)
                }
            }
        }
    }

    private fun mostrarAlerta(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Error de acceso")
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
