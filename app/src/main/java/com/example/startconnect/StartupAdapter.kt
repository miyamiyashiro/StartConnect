package com.example.startconnect

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StartupAdapter(
    private val startups: List<Startup>,
    private val onItemClick: ((Startup) -> Unit)? = null,
    private val showEditAction: Boolean = false,
    private val onEditClick: ((Startup) -> Unit)? = null,
    private val actionIconRes: Int? = null,
    private val onActionClick: ((Startup) -> Unit)? = null
) : RecyclerView.Adapter<StartupAdapter.StartupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StartupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_startup_investidor, parent, false)
        return StartupViewHolder(view)
    }

    override fun onBindViewHolder(holder: StartupViewHolder, position: Int) {
        val startup = startups[position]
        holder.bind(startup, showEditAction, onEditClick, actionIconRes, onActionClick)
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(startup)
        }
    }

    override fun getItemCount(): Int = startups.size

    class StartupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.startupTitleText)
        private val subtitleText: TextView = itemView.findViewById(R.id.startupSubtitleText)
        private val editIcon: ImageView = itemView.findViewById(R.id.startupEditIcon)
        private val tagViews: List<TextView> = listOf(
            itemView.findViewById(R.id.tagOneText),
            itemView.findViewById(R.id.tagTwoText),
            itemView.findViewById(R.id.tagThreeText),
            itemView.findViewById(R.id.tagFourText)
        )

        fun bind(
            startup: Startup,
            showEditAction: Boolean,
            onEditClick: ((Startup) -> Unit)?,
            actionIconRes: Int?,
            onActionClick: ((Startup) -> Unit)?
        ) {
            val iconPadding = (itemView.resources.displayMetrics.density * 3).toInt()

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

            if (actionIconRes != null && onActionClick != null) {
                editIcon.visibility = View.VISIBLE
                editIcon.setImageResource(actionIconRes)
                editIcon.setBackgroundResource(R.drawable.bg_small_action_white)
                editIcon.setPadding(0, 0, 0, 0)
                editIcon.setColorFilter(Color.parseColor("#6F5A86"))
                editIcon.contentDescription = "Remover startup dos favoritos"
                editIcon.setOnClickListener { onActionClick.invoke(startup) }
            } else if (showEditAction) {
                editIcon.visibility = View.VISIBLE
                editIcon.setImageResource(R.drawable.editar_simbolo)
                editIcon.setBackgroundResource(R.drawable.bg_circular_editar)
                editIcon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding)
                editIcon.clearColorFilter()
                editIcon.contentDescription = "Editar startup"
                editIcon.setOnClickListener { onEditClick?.invoke(startup) }
            } else {
                editIcon.visibility = View.GONE
                editIcon.setOnClickListener(null)
            }
        }
    }
}

