package com.incasolutions.finanzaspersonales.ui

// ============================================================
// ARCHIVO NUEVO — copiar a:
// PROYECTO/app/src/main/java/com/incasolutions/finanzaspersonales/ui/ReporteActivity.kt
//
// Pantalla "Reporte del mes": permite definir un presupuesto,
// muestra el % gastado del presupuesto (con aviso NO restrictivo
// al 80% y al 100%) y la lista de gastos por categoría con barra
// de porcentaje — sin ninguna librería externa.
// ============================================================

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.incasolutions.finanzaspersonales.databinding.ActivityReporteBinding
import com.incasolutions.finanzaspersonales.databinding.ItemCategoriaReporteBinding
import com.incasolutions.finanzaspersonales.viewmodel.ReporteData
import com.incasolutions.finanzaspersonales.viewmodel.ReporteViewModel
import com.incasolutions.finanzaspersonales.viewmodel.UiState
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

class ReporteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReporteBinding
    private val viewModel: ReporteViewModel by viewModels()
    private val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "PE"))

    private val anioActual = Calendar.getInstance().get(Calendar.YEAR)
    private val mesActual = Calendar.getInstance().get(Calendar.MONTH) + 1

    // Para que la alerta aparezca una sola vez por visita a la pantalla,
    // no cada vez que el LiveData se vuelve a emitir.
    private var yaMostroAlerta80 = false
    private var yaMostroAlerta100 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReporteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Reporte del mes"

        binding.btnVolver.setOnClickListener { finish() }

        binding.btnGuardarPresupuesto.setOnClickListener {
            val texto = binding.etPresupuesto.text.toString().trim()
            val monto = texto.toDoubleOrNull()
            if (monto == null || monto <= 0) {
                AlertDialog.Builder(this)
                    .setTitle("Dato inválido")
                    .setMessage("Ingresa un presupuesto mayor a 0")
                    .setPositiveButton("Aceptar", null)
                    .show()
                return@setOnClickListener
            }
            viewModel.guardarPresupuesto(anioActual, mesActual, monto)
        }

        observarViewModel()
        viewModel.cargarReporte(anioActual, mesActual)
    }

    private fun observarViewModel() {
        viewModel.reporte.observe(this) { estado ->
            when (estado) {
                is UiState.Success -> mostrarReporte(estado.data)
                is UiState.Error -> { /* el usuario ya ve el reporte vacío; no se bloquea nada */ }
                else -> Unit
            }
        }
    }

    private fun mostrarReporte(data: ReporteData) {
        binding.tvTotalGastado.text = "Gastado este mes: ${formatoMoneda.format(data.totalGastado)}"

        if (data.presupuesto != null) {
            binding.etPresupuesto.setText(data.presupuesto.toString())
        }

        if (data.porcentajeUsado != null) {
            val porcentajeBarra = data.porcentajeUsado.coerceIn(0, 100)
            binding.pbPresupuesto.progress = porcentajeBarra
            binding.tvPorcentajeUsado.text = "${data.porcentajeUsado}% del presupuesto usado"

            // Aviso NO restrictivo: solo informa, no bloquea seguir registrando movimientos
            if (data.porcentajeUsado >= 100 && !yaMostroAlerta100) {
                yaMostroAlerta100 = true
                mostrarAlertaPresupuesto(
                    "Presupuesto superado",
                    "Ya superaste el 100% de tu presupuesto de este mes " +
                        "(${formatoMoneda.format(data.totalGastado)} de ${formatoMoneda.format(data.presupuesto)}). " +
                        "Esto es solo un aviso, puedes seguir registrando movimientos con normalidad."
                )
            } else if (data.porcentajeUsado in 80..99 && !yaMostroAlerta80) {
                yaMostroAlerta80 = true
                mostrarAlertaPresupuesto(
                    "Atención con tu presupuesto",
                    "Ya usaste el ${data.porcentajeUsado}% de tu presupuesto de este mes. " +
                        "Te quedan ${formatoMoneda.format(data.presupuesto!! - data.totalGastado)}."
                )
            }
        } else {
            binding.pbPresupuesto.progress = 0
            binding.tvPorcentajeUsado.text = "Define un presupuesto para ver tu avance"
        }

        binding.llCategorias.removeAllViews()
        data.categorias.forEach { categoria ->
            val itemBinding = ItemCategoriaReporteBinding.inflate(
                LayoutInflater.from(this), binding.llCategorias, false
            )
            itemBinding.tvNombreCategoria.text = categoria.nombre
            itemBinding.tvPorcentajeCategoria.text = "${categoria.porcentaje}%"
            itemBinding.tvMontoCategoria.text = formatoMoneda.format(categoria.monto)
            itemBinding.pbCategoria.progress = categoria.porcentaje
            binding.llCategorias.addView(itemBinding.root)
        }
    }

    private fun mostrarAlertaPresupuesto(titulo: String, mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("Entendido", null)
            .show()
    }
}
