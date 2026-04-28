package com.example.startconnect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StartupAdapter(
    private val startups: List<Startup>
) : RecyclerView.Adapter<StartupAdapter.StartupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StartupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_startup_investidor, parent, false)
        return StartupViewHolder(view)
    }

    override fun onBindViewHolder(holder: StartupViewHolder, position: Int) {
        holder.bind(startups[position])
    }

    override fun getItemCount(): Int = startups.size

    class StartupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.startupTitleText)
        private val subtitleText: TextView = itemView.findViewById(R.id.startupSubtitleText)
        private val tagViews: List<TextView> = listOf(
            itemView.findViewById(R.id.tagOneText),
            itemView.findViewById(R.id.tagTwoText),
            itemView.findViewById(R.id.tagThreeText),
            itemView.findViewById(R.id.tagFourText)
        )

        fun bind(startup: Startup) {
            titleText.text = "${startup.nome} (${startup.segmento})"
            subtitleText.text = startup.subtitulo

            tagViews.forEachIndexed { index, textView ->
                val tag = startup.tags.getOrNull(index)
                if (tag.isNullOrBlank()) {
                    textView.visibility = View.GONE
                } else {
                    textView.visibility = View.VISIBLE
                    textView.text = tag
                }
            }
        }
    }
}