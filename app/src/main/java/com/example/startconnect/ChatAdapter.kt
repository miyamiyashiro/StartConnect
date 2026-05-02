package com.example.startconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(
    private val chats: List<ChatResponse>,
    private val onItemClick: (ChatResponse) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chats[position]
        holder.bind(chat)
        holder.itemView.setOnClickListener { onItemClick(chat) }
    }

    override fun getItemCount(): Int = chats.size

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nome: TextView = itemView.findViewById(R.id.txtChatNome)
        private val ultimaMsg: TextView = itemView.findViewById(R.id.txtChatUltimaMensagem)

        fun bind(chat: ChatResponse) {
            nome.text = chat.startupNome
            ultimaMsg.text = chat.ultimaMensagem
        }
    }
}
