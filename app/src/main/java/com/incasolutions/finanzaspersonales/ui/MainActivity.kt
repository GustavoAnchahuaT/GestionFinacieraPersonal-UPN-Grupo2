package com.incasolutions.finanzaspersonales.ui

// ============================================================
// REEMPLAZA el MainActivity.kt que te mandó tu compañero (MVVM) en:
// PROYECTO/app/src/main/java/com/incasolutions/finanzaspersonales/ui/MainActivity.kt
//
// Es el mismo archivo de tu compañero + un botón nuevo (btnReporte)
// que abre ReporteActivity. Todo lo demás queda exactamente igual.
// Necesitas agregar el botón "btnReporte" en activity_main.xml
// (ver SNIPPETS_PARA_PEGAR.txt).
// ============================================================

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.incasolutions.finanzaspersonales.R
import com.incasolutions.finanzaspersonales.adapter.TransaccionAdapter
import com.incasolutions.finanzaspersonales.databinding.ActivityMainBinding
import com.incasolutions.finanzaspersonales.model.Transaccion
import com.incasolutions.finanzaspersonales.viewmodel.MainViewModel
import com.incasolutions.finanzaspersonales.viewmodel.UiState
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TransaccionAdapter
    private val viewModel: MainViewModel by viewModels()

    private val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
    private val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val formatoFechaVisible = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // Debe coincidir en orden con el array "opciones_periodo" de strings.xml
    // 0 = Este mes, 1 = Hoy, 2 = Este año, 3 = Últimos 3 meses,
    // 4 = Últimos 6 meses, 5 = Rango de fechas personalizado
    private var periodoSeleccionado = 0

    private var fechaDesdeElegida: Calendar? = null
    private var fechaHastaElegida: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TransaccionAdapter(emptyList()) { transaccion ->
            mostrarDialogoEliminar(transaccion)
        }
        binding.rvTransacciones.layoutManager = LinearLayoutManager(this)
        binding.rvTransacciones.adapter = adapter

        binding.fabAgregar.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }

        binding.btnCerrarSesion.setOnClickListener {
            confirmarCerrarSesion()
        }

        // NUEVO: abre la pantalla de reporte gráfico + alerta de presupuesto
        binding.btnReporte.setOnClickListener {
            startActivity(Intent(this, ReporteActivity::class.java))
        }

        binding.spinnerPeriodo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                periodoSeleccionado = position

                if (periodoSeleccionado == 5) {
                    binding.llRangoPersonalizado.visibility = View.VISIBLE
                } else {
                    binding.llRangoPersonalizado.visibility = View.GONE
                    cargarTransacciones()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.btnFechaDesde.setOnClickListener { elegirFecha(esDesde = true) }
        binding.btnFechaHasta.setOnClickListener { elegirFecha(esDesde = false) }

        binding.btnAplicarRango.setOnClickListener {
            val desde = fechaDesdeElegida
            val hasta = fechaHastaElegida

            if (desde == null || hasta == null) {
                Toast.makeText(this, R.string.error_rango_incompleto, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (desde.after(hasta)) {
                Toast.makeText(this, R.string.error_rango_invalido, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            cargarTransacciones()
        }

        observarViewModel()
    }

    override fun onResume() {
        super.onResume()
        if (periodoSeleccionado != 5) {
            cargarTransacciones()
        }
    }

    private fun observarViewModel() {
        viewModel.transacciones.observe(this) { estado ->
            when (estado) {
                is UiState.Loading -> { /* opcional: mostrar un ProgressBar */ }
                is UiState.Success -> actualizarUI(estado.data)
                is UiState.Error -> mostrarError()
            }
        }

        viewModel.deleteState.observe(this) { estado ->
            when (estado) {
                is UiState.Success -> cargarTransacciones()
                is UiState.Error -> mostrarError()
                else -> Unit
            }
        }
    }

    private fun elegirFecha(esDesde: Boolean) {
        val calendarioBase = (if (esDesde) fechaDesdeElegida else fechaHastaElegida)
            ?: Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, anio, mes, dia ->
                val seleccionada = Calendar.getInstance()
                seleccionada.set(anio, mes, dia, 0, 0, 0)

                if (esDesde) {
                    fechaDesdeElegida = seleccionada
                    binding.btnFechaDesde.text = formatoFechaVisible.format(seleccionada.time)
                } else {
                    fechaHastaElegida = seleccionada
                    binding.btnFechaHasta.text = formatoFechaVisible.format(seleccionada.time)
                }
            },
            calendarioBase.get(Calendar.YEAR),
            calendarioBase.get(Calendar.MONTH),
            calendarioBase.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun cargarTransacciones() {
        when (periodoSeleccionado) {
            1 -> { // Hoy
                val hoy = formatoFecha.format(Calendar.getInstance().time)
                viewModel.cargarPorRango(hoy, hoy)
            }
            2 -> { // Este año completo
                val anio = Calendar.getInstance().get(Calendar.YEAR)
                viewModel.cargarPorRango("$anio-01-01", "$anio-12-31")
            }
            3 -> viewModel.cargarPorRango(fechaInicioHaceNMeses(3), fechaHoy()) // Últimos 3 meses
            4 -> viewModel.cargarPorRango(fechaInicioHaceNMeses(6), fechaHoy()) // Últimos 6 meses
            5 -> { // Rango personalizado
                val desde = fechaDesdeElegida ?: return
                val hasta = fechaHastaElegida ?: return
                viewModel.cargarPorRango(formatoFecha.format(desde.time), formatoFecha.format(hasta.time))
            }
            else -> { // Este mes (por defecto)
                val calendario = Calendar.getInstance()
                val mes = calendario.get(Calendar.MONTH) + 1
                val anio = calendario.get(Calendar.YEAR)
                viewModel.cargarPorMes(mes, anio)
            }
        }
    }

    private fun fechaHoy(): String = formatoFecha.format(Calendar.getInstance().time)

    private fun fechaInicioHaceNMeses(n: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -(n - 1))
        cal.set(Calendar.DAY_OF_MONTH, 1)
        return formatoFecha.format(cal.time)
    }

    private fun actualizarUI(lista: List<Transaccion>) {
        adapter.actualizarLista(lista)
        binding.tvListaVacia.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE

        val ingresos = lista.filter { it.tipo == "ingreso" }.sumOf { it.monto }
        val gastos = lista.filter { it.tipo == "gasto" }.sumOf { it.monto }
        val saldo = ingresos - gastos

        binding.tvIngresosMes.text = formatoMoneda.format(ingresos)
        binding.tvGastosMes.text = formatoMoneda.format(gastos)
        binding.tvSaldoMes.text = formatoMoneda.format(saldo)
    }

    private fun mostrarError() {
        Toast.makeText(this, R.string.error_conexion, Toast.LENGTH_LONG).show()
    }

    private fun confirmarCerrarSesion() {
        AlertDialog.Builder(this)
            .setTitle(R.string.btn_cerrar_sesion)
            .setMessage(R.string.msg_confirmar_cerrar_sesion)
            .setPositiveButton(R.string.btn_cerrar_sesion) { _, _ -> cerrarSesion() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cerrarSesion() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun mostrarDialogoEliminar(transaccion: Transaccion) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar movimiento")
            .setMessage("¿Seguro que deseas eliminar este registro?")
            .setPositiveButton("Eliminar") { _, _ ->
                transaccion.id?.let { viewModel.eliminar(it) }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
