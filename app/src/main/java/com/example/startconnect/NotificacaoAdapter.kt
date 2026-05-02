package com.example.startconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotificacaoAdapter(
    private val notificacoes: List<NotificacaoResponse>,
    private val onItemClick: (NotificacaoResponse) -> Unit
) : RecyclerView.Adapter<NotificacaoAdapter.NotificacaoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacao, parent, false)
        return NotificacaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificacaoViewHolder, position: Int) {
        val notif = notificacoes[position]
        holder.bind(notif)
        holder.itemView.setOnClickListener { onItemClick(notif) }
    }

    override fun getItemCount(): Int = notificacoes.size

    class NotificacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nome: TextView = itemView.findViewById(R.id.txtNotifNome)
        private val mensagem: TextView = itemView.findViewById(R.id.txtNotifMensagem)
        private val data: TextView = itemView.findViewById(R.id.txtNotifData)

        fun bind(notif: NotificacaoResponse) {
            nome.text = notif.startupNome
            mensagem.text = notif.texto
            data.text = formatarData(notif.dataEnvio)
        }

        private fun formatarData(dataStr: String): String {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = sdf.parse(dataStr) ?: return dataStr

                val hoje = Calendar.getInstance()
                val msgDate = Calendar.getInstance().apply { time = date }

                val horaFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val hora = horaFormat.format(date)

                when {
                    hoje.get(Calendar.DAY_OF_YEAR) == msgDate.get(Calendar.DAY_OF_YEAR) &&
                    hoje.get(Calendar.YEAR) == msgDate.get(Calendar.YEAR) -> "Hoje\n$hora"

                    hoje.get(Calendar.DAY_OF_YEAR) - msgDate.get(Calendar.DAY_OF_YEAR) == 1 &&
                    hoje.get(Calendar.YEAR) == msgDate.get(Calendar.YEAR) -> "Ontem\n$hora"

                    else -> {
                        val dataFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                        "${dataFormat.format(date)}\n$hora"
                    }
                }
            } catch (e: Exception) {
                dataStr
            }
        }
    }
}
