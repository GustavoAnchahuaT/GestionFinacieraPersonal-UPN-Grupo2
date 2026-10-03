package com.incasolutions.finanzaspersonales.ui

// ============================================================
// REEMPLAZA tu AddTransactionActivity.kt actual en:
// PROYECTO/app/src/main/java/com/incasolutions/finanzaspersonales/ui/AddTransactionActivity.kt
//
// CAMBIO: ya no llama a RetrofitClient.apiService directamente.
// Usa AddTransactionViewModel para cargar categorías y guardar.
// Las validaciones de formulario (monto, categoría) se quedan
// aquí porque son responsabilidad de la Vista, no del ViewModel.
// ============================================================

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.incasolutions.finanzaspersonales.R
import com.incasolutions.finanzaspersonales.databinding.ActivityAddTransactionBinding
import com.incasolutions.finanzaspersonales.model.Categoria
import com.incasolutions.finanzaspersonales.viewmodel.AddTransactionViewModel
import com.incasolutions.finanzaspersonales.viewmodel.UiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private val viewModel: AddTransactionViewModel by viewModels()

    private var todasLasCategorias: List<Categoria> = emptyList()
    private var categoriasFiltradas: List<Categoria> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = getString(R.string.title_add_transaction)

        binding.btnVolver.setOnClickListener { finish() }

        observarViewModel()
        viewModel.cargarCategorias()

        binding.rgTipo.setOnCheckedChangeListener { _, _ ->
            filtrarCategoriasPorTipoSeleccionado()
        }

        binding.btnGuardar.setOnClickListener {
            guardarTransaccion()
        }
    }

    private fun observarViewModel() {
        viewModel.categorias.observe(this) { estado ->
            when (estado) {
                is UiState.Success -> {
                    todasLasCategorias = estado.data
                    filtrarCategoriasPorTipoSeleccionado()
                }
                is UiState.Error -> {
                    Toast.makeText(this, R.string.error_conexion, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }

        viewModel.guardarState.observe(this) { estado ->
            when (estado) {
                is UiState.Loading -> binding.btnGuardar.isEnabled = false
                is UiState.Success -> {
                    binding.btnGuardar.isEnabled = true
                    Toast.makeText(this, R.string.msg_guardado, Toast.LENGTH_SHORT).show()
                    finish()
                }
                is UiState.Error -> {
                    binding.btnGuardar.isEnabled = true
                    Toast.makeText(this, R.string.error_conexion, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun tipoSeleccionado(): String =
        if (binding.rbIngreso.isChecked) "ingreso" else "gasto"

    private fun filtrarCategoriasPorTipoSeleccionado() {
        val tipo = tipoSeleccionado()
        categoriasFiltradas = todasLasCategorias.filter { it.tipo == tipo || it.tipo == "ambos" }

        val nombres = categoriasFiltradas.map { it.nombre }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombres)
        binding.spinnerCategoria.adapter = adapter
    }

    private fun guardarTransaccion() {
        val montoTexto = binding.etMonto.text.toString().trim()
        val monto = montoTexto.toDoubleOrNull()

        if (monto == null) {
            mostrarAlerta("Solo se permiten números en el campo Monto")
            return
        }
        if (monto < 0) {
            mostrarAlerta("El monto no puede ser negativo")
            return
        }
        if (monto == 0.0) {
            mostrarAlerta(getString(R.string.error_monto))
            return
        }

        val posicionCategoria = binding.spinnerCategoria.selectedItemPosition
        if (posicionCategoria < 0 || categoriasFiltradas.isEmpty()) {
            Toast.makeText(this, R.string.error_categoria, Toast.LENGTH_SHORT).show()
            return
        }

        val categoriaSeleccionada = categoriasFiltradas[posicionCategoria]
        val tipo = tipoSeleccionado()
        val descripcion = binding.etDescripcion.text.toString()
        val esRecurrente = if (binding.cbRecurrente.isChecked) 1 else 0
        val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        viewModel.guardarTransaccion(
            monto = monto,
            tipo = tipo,
            categoriaId = categoriaSeleccionada.id,
            descripcion = descripcion,
            esRecurrente = esRecurrente,
            fecha = fechaHoy
        )
    }

    private fun mostrarAlerta(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Dato inválido")
            .setMessage(mensaje)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
