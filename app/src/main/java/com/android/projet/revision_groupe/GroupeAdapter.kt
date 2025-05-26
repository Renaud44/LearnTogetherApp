package com.android.projet.revision_groupe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.projet.R
import com.android.projet.models.Groupe
import com.google.firebase.auth.FirebaseAuth

class GroupeAdapter(
    private val groupes: List<Groupe>,
    private val onDelete: (Groupe) -> Unit,
    private val onView: (Groupe) -> Unit,
    private val onMembers: (Groupe) -> Unit
) : RecyclerView.Adapter<GroupeAdapter.ViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nomGroupe: TextView = view.findViewById(R.id.nom_groupe)
        val matiere: TextView = view.findViewById(R.id.matiere_groupe)
        val description: TextView = view.findViewById(R.id.description_groupe)
        val btnSupprimer: Button = view.findViewById(R.id.btn_supprimer)
        val btnVoir: Button = view.findViewById(R.id.btn_voir)
        val btnMembres: Button = view.findViewById(R.id.btn_membres)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_groupe, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = groupes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val groupe = groupes[position]
        holder.nomGroupe.text = groupe.nom
        holder.matiere.text = "Matière : ${groupe.matiere}"
        holder.description.text = "Description : ${groupe.description}"


        holder.btnSupprimer.text = if (groupe.adminId == currentUserId) "Supprimer" else "Quitter"

        holder.btnSupprimer.setOnClickListener { onDelete(groupe) }
        holder.btnVoir.setOnClickListener { onView(groupe) }
        holder.btnMembres.setOnClickListener { onMembers(groupe) }
    }
}
