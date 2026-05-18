package com.example.startconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ConexaoAdapter(
    private val conexoes: List<ConexaoResponse>
) : RecyclerView.Adapter<ConexaoAdapter.ConexaoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConexaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conexao, parent, false)
        return ConexaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConexaoViewHolder, position: Int) {
        holder.bind(conexoes[position])
    }

    override fun getItemCount(): Int = conexoes.size

    class ConexaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nomeText: TextView = itemView.findViewById(R.id.txtConexaoNome)
        private val tipoText: TextView = itemView.findViewById(R.id.txtConexaoTipo)
        private val resumoText: TextView = itemView.findViewById(R.id.txtConexaoResumo)

        fun bind(conexao: ConexaoResponse) {
            nomeText.text = conexao.usuarioNome
            tipoText.text = "@${conexao.usuarioTipo.lowercase()}"
            resumoText.text = "Favoritou ${conexao.totalFavoritos} startup${if (conexao.totalFavoritos > 1) "s" else ""}"
        }
    }
}