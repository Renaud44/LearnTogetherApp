package com.android.projet.revision_groupe

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.projet.R
import com.android.projet.models.Groupe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ListeGroupesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroupeAdapter
    private val groupes = mutableListOf<Groupe>()
    private val db = FirebaseFirestore.getInstance()
    private val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_groupes)

        recyclerView = findViewById(R.id.recyclerGroupes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = GroupeAdapter(groupes,
            onDelete = { groupe -> confirmerSuppressionOuQuitter(groupe) },
            onView = { groupe -> ouvrirChatDeGroupe(groupe) },
            onMembers = { groupe -> voirMembresDuGroupe(groupe) }
        )

        recyclerView.adapter = adapter
        chargerGroupes()
    }

    private fun chargerGroupes() {
        db.collection("groupes").get()
            .addOnSuccessListener { result ->
                groupes.clear()
                for (doc in result) {
                    val groupe = doc.toObject(Groupe::class.java)
                    groupe.id = doc.id
                    if (groupe.membres.contains(currentUid)) {
                        groupes.add(groupe)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erreur lors du chargement", Toast.LENGTH_SHORT).show()
            }
    }

    private fun confirmerSuppressionOuQuitter(groupe: Groupe) {
        val isAdmin = groupe.adminId == currentUid
        val message = if (isAdmin) {
            "Voulez-vous supprimer ce groupe ?"
        } else {
            "Voulez-vous quitter ce groupe ?"
        }

        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage(message)
            .setPositiveButton("Oui") { _, _ ->
                if (isAdmin) supprimerGroupe(groupe)
                else quitterGroupe(groupe)
            }
            .setNegativeButton("Non", null)
            .show()
    }

    private fun supprimerGroupe(groupe: Groupe) {
        db.collection("groupes").document(groupe.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Groupe supprimé", Toast.LENGTH_SHORT).show()
                groupes.remove(groupe)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erreur suppression", Toast.LENGTH_SHORT).show()
            }
    }

    private fun quitterGroupe(groupe: Groupe) {
        val updatedMembres = groupe.membres.toMutableList()
        updatedMembres.remove(currentUid)

        db.collection("groupes").document(groupe.id)
            .update("membres", updatedMembres)
            .addOnSuccessListener {
                Toast.makeText(this, "Vous avez quitté le groupe", Toast.LENGTH_SHORT).show()
                groupes.remove(groupe)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erreur", Toast.LENGTH_SHORT).show()
            }
    }

    private fun ouvrirChatDeGroupe(groupe: Groupe) {
        val intent = Intent(this, GroupChatActivity::class.java)
        intent.putExtra("groupId", groupe.id)
        startActivity(intent)
    }

    private fun voirMembresDuGroupe(groupe: Groupe) {
        val intent = Intent(this, MembresGroupeActivity::class.java)
        intent.putExtra("groupId", groupe.id)
        startActivity(intent)
    }
}