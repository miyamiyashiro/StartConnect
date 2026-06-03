package com.example.startconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class ConexaoAdapter(
    private val conexoes: List<ConexaoResponse>,
    private val onActionClick: ((ConexaoResponse) -> Unit)? = null
) : RecyclerView.Adapter<ConexaoAdapter.ConexaoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConexaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conexao, parent, false)
        return ConexaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConexaoViewHolder, position: Int) {
        holder.bind(conexoes[position], onActionClick)
    }

    override fun getItemCount(): Int = conexoes.size

    class ConexaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fotoView: ShapeableImageView = itemView.findViewById(R.id.imgConexaoProfile)
        private val nomeText: TextView = itemView.findViewById(R.id.txtConexaoNome)
        private val tipoText: TextView = itemView.findViewById(R.id.txtConexaoTipo)
        private val resumoText: TextView = itemView.findViewById(R.id.txtConexaoResumo)
        private val actionView: ImageView = itemView.findViewById(R.id.btnDesfazerConexao)

        fun bind(conexao: ConexaoResponse, onActionClick: ((ConexaoResponse) -> Unit)?) {
            nomeText.text = conexao.usuarioNome
            tipoText.text = "@${conexao.usuarioNome}"
            resumoText.text = "Favoritou ${conexao.totalFavoritos} startup${if (conexao.totalFavoritos > 1) "s" else ""}"

            val bitmap = ProfilePhotoHelper.getPhotoBitmap(itemView.context, conexao.usuarioId)
            if (bitmap != null) {
                fotoView.setImageBitmap(bitmap)
            } else {
                fotoView.setImageResource(R.drawable.foto_vazia)
            }

            actionView.setOnClickListener {
                onActionClick?.invoke(conexao)
            }
        }
    }
}
