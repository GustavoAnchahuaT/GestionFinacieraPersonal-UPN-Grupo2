package com.incasolutions.finanzaspersonales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.incasolutions.finanzaspersonales.databinding.ItemTransaccionBinding
import com.incasolutions.finanzaspersonales.model.Transaccion
import java.text.NumberFormat
import java.util.Locale

class TransaccionAdapter(
    private var items: List<Transaccion>,
    private val onEliminarClick: (Transaccion) -> Unit
) : RecyclerView.Adapter<TransaccionAdapter.TransaccionViewHolder>() {

    private val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "PE"))

    inner class TransaccionViewHolder(val binding: ItemTransaccionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaccionViewHolder {
        val binding = ItemTransaccionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TransaccionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransaccionViewHolder, position: Int) {
        val transaccion = items[position]
        with(holder.binding) {
            tvCategoria.text = transaccion.categoriaNombre ?: "Sin categoría"
            tvDescripcion.text = transaccion.descripcion?.ifBlank { "-" } ?: "-"
            tvFecha.text = transaccion.fecha

            tvRecurrente.visibility = if (transaccion.esRecurrente == 1) View.VISIBLE else View.GONE

            val esIngreso = transaccion.tipo == "ingreso"
            val signo = if (esIngreso) "+ " else "- "
            tvMonto.text = signo + formatoMoneda.format(transaccion.monto)
            tvMonto.setTextColor(
                holder.itemView.context.getColor(
                    if (esIngreso) com.incasolutions.finanzaspersonales.R.color.income_green
                    else com.incasolutions.finanzaspersonales.R.color.expense_red
                )
            )

            // Un solo toque en el ícono de basurero -> pide confirmación y elimina
            btnEliminar.setOnClickListener {
                onEliminarClick(transaccion)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun actualizarLista(nuevaLista: List<Transaccion>) {
        items = nuevaLista
        notifyDataSetChanged()
    }
}
