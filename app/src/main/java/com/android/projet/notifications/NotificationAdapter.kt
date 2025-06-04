package com.android.projet.notifications

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.projet.R
import com.android.projet.defis.DefiInteractifActivity
import com.android.projet.models.Binome
import com.android.projet.models.Notification
import com.android.projet.models.User
import com.google.firebase.firestore.FirebaseFirestore

class NotificationAdapter(
    private val notifications: List<Notification>,
    private val onAcceptBinome: (Binome) -> Unit,
    private val onRefuseBinome: (Binome) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private val db = FirebaseFirestore.getInstance()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pseudo: TextView = view.findViewById(R.id.textPseudo)
        val details: TextView = view.findViewById(R.id.textDetails)
        val btnAccept: Button = view.findViewById(R.id.btnAccepter)
        val btnRefuse: Button = view.findViewById(R.id.btnRefuser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = notifications.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notif = notifications[position]

        // Si type inconnu, on ne montre pas la vue
        if (notif.type !in listOf("binome", "defi", "defi_invite")) {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            return
        }

        db.collection("users").document(notif.from).get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(User::class.java)
                val pseudo = user?.pseudo ?: "Utilisateur inconnu"

                when (notif.type) {
                    "binome" -> {
                        holder.pseudo.text = pseudo
                        holder.details.text = "Souhaite vous ajouter comme binôme"
                        holder.btnAccept.text = "Accepter"
                        holder.btnRefuse.visibility = View.VISIBLE

                        val binome = Binome(
                            uuid = notif.from,
                            pseudo = pseudo,
                            email = user?.email ?: "",
                            image = user?.image,
                            matieresDifficiles = user?.matieresDifficiles,
                            matieresMaitrisees = user?.matieresMaitrisees
                        )

                        holder.btnAccept.setOnClickListener {
                            onAcceptBinome(binome)
                            marquerCommeLue(notif.id)
                        }

                        holder.btnRefuse.setOnClickListener {
                            onRefuseBinome(binome)
                            marquerCommeLue(notif.id)
                        }
                    }

                    "defi", "defi_invite" -> {
                        holder.pseudo.text = pseudo
                        holder.details.text = "Vous a défié ! Cliquez pour commencer"
                        holder.btnAccept.text = "Commencer"
                        holder.btnRefuse.visibility = View.GONE

                        holder.btnAccept.setOnClickListener {
                            val context = holder.itemView.context
                            val intent = Intent(context, DefiInteractifActivity::class.java)
                            intent.putExtra("defiId", notif.defiId)
                            context.startActivity(intent)
                            marquerCommeLue(notif.id)
                        }
                    }
                }
            }
            .addOnFailureListener {
                holder.pseudo.text = "Erreur de chargement"
                holder.details.text = ""
                holder.btnAccept.visibility = View.GONE
                holder.btnRefuse.visibility = View.GONE
            }
    }

    private fun marquerCommeLue(notificationId: String) {
        db.collection("notifications").document(notificationId)
            .update("read", true)
    }
}