package com.android.projet.revision_groupe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.projet.R
import com.android.projet.models.User

class MembresAdapter(
    private val membres: List<User>
) : RecyclerView.Adapter<MembresAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pseudo: TextView = view.findViewById(R.id.textPseudo)
        val role: TextView = view.findViewById(R.id.textRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_membre_groupe, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int = membres.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = membres[position]
        holder.pseudo.text = user.pseudo
        holder.role.text = if (position == 0) "(Admin)" else "(Membre)"
    }
}
