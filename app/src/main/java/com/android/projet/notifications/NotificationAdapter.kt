package com.android.projet.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.projet.R
import com.android.projet.models.Binome

class NotificationAdapter(
    private val demandes: List<Binome>,
    private val onAccept: (Binome) -> Unit,
    private val onRefuse: (Binome) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pseudo: TextView = view.findViewById(R.id.textPseudo)
        val difficultes: TextView = view.findViewById(R.id.textDifficile)
        val matrisés: TextView = view.findViewById(R.id.textMaitrise)
        val btnAccept: Button = view.findViewById(R.id.btnAccepter)
        val btnRefuse: Button = view.findViewById(R.id.btnRefuser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = demandes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val binome = demandes[position]
        holder.pseudo.text = binome.pseudo
        holder.difficultes.text = "Difficultés : ${binome.matieresDifficiles}"
        holder.matrisés.text = "Maîtrisées : ${binome.matieresMaitrisees}"
        holder.btnAccept.setOnClickListener { onAccept(binome) }
        holder.btnRefuse.setOnClickListener { onRefuse(binome) }
    }
}