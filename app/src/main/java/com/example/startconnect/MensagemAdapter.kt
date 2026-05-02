package com.example.startconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MensagemAdapter(
    private val mensagens: List<MensagemResponse>,
    private val usuarioId: Int
) : RecyclerView.Adapter<MensagemAdapter.MensagemViewHolder>() {

    companion object {
        private const val TIPO_ENVIADA = 0
        private const val TIPO_RECEBIDA = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (mensagens[position].remetenteId == usuarioId) TIPO_ENVIADA else TIPO_RECEBIDA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensagemViewHolder {
        val layout = if (viewType == TIPO_ENVIADA) R.layout.item_mensagem_enviada else R.layout.item_mensagem_recebida
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MensagemViewHolder(view)
    }

    override fun onBindViewHolder(holder: MensagemViewHolder, position: Int) {
        holder.bind(mensagens[position])
    }

    override fun getItemCount(): Int = mensagens.size

    class MensagemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtMensagem: TextView = itemView.findViewById(R.id.txtMensagem)

        fun bind(mensagem: MensagemResponse) {
            txtMensagem.text = mensagem.texto
        }
    }
}
