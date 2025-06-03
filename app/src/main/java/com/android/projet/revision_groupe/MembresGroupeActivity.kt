package com.android.projet.revision_groupe

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.projet.R
import com.android.projet.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MembresGroupeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAjouterMembre: Button
    private val membresList = mutableListOf<User>()
    private lateinit var adapter: MembresAdapter
    private val db = FirebaseFirestore.getInstance()
    private lateinit var currentUid: String
    private lateinit var groupeId: String
    private lateinit var membresIds: MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_membres_groupe)

        currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        groupeId = intent.getStringExtra("groupId") ?: return

        recyclerView = findViewById(R.id.recyclerMembres)
        btnAjouterMembre = findViewById(R.id.button_ajouter_membre)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = MembresAdapter(membresList)
        recyclerView.adapter = adapter

        btnAjouterMembre.setOnClickListener {
            afficherDialogAjoutMembre()
        }

        chargerMembres()
    }

    private fun chargerMembres() {
        db.collection("groupes").document(groupeId).get()
            .addOnSuccessListener { doc ->
                membresIds = (doc.get("membres") as? List<String>)?.toMutableList() ?: mutableListOf()
                membresList.clear()

                for (uid in membresIds) {
                    db.collection("users").document(uid).get()
                        .addOnSuccessListener { userDoc ->
                            val user = userDoc.toObject(User::class.java)
                            user?.uuid = uid
                            user?.let {
                                membresList.add(it)
                                adapter.notifyDataSetChanged()
                            }
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erreur chargement membres", Toast.LENGTH_SHORT).show()
            }
    }

    private fun afficherDialogAjoutMembre() {
        db.collection("users").document(currentUid).get()
            .addOnSuccessListener { userDoc ->
                val binomes = userDoc.get("binomesAcceptes") as? List<String> ?: listOf()
                val binomesRestants = binomes.filterNot { membresIds.contains(it) }

                if (binomesRestants.isEmpty()) {
                    Toast.makeText(this, "Aucun binôme disponible à ajouter", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val noms = mutableListOf<String>()
                val idMap = mutableMapOf<String, String>()

                binomesRestants.forEach { uid ->
                    db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                        val user = doc.toObject(User::class.java)
                        if (user != null) {
                            noms.add(user.pseudo)
                            idMap[user.pseudo] = uid

                            if (noms.size == binomesRestants.size) {
                                val builder = AlertDialog.Builder(this)
                                builder.setTitle("Choisir un binôme à ajouter")
                                builder.setItems(noms.toTypedArray()) { _, which ->
                                    val selectedPseudo = noms[which]
                                    val selectedUid = idMap[selectedPseudo] ?: return@setItems

                                    db.collection("groupes").document(groupeId)
                                        .update("membres", FieldValue.arrayUnion(selectedUid))
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "$selectedPseudo ajouté", Toast.LENGTH_SHORT).show()

                                            // Ajout fluide dans la liste
                                            db.collection("users").document(selectedUid).get()
                                                .addOnSuccessListener { newDoc ->
                                                    val newUser = newDoc.toObject(User::class.java)
                                                    newUser?.uuid = selectedUid
                                                    newUser?.let {
                                                        membresList.add(it)
                                                        membresIds.add(selectedUid)
                                                        adapter.notifyItemInserted(membresList.size - 1)
                                                    }
                                                }
                                        }
                                }
                                builder.show()
                            }
                        }
                    }
                }
            }
    }
}
