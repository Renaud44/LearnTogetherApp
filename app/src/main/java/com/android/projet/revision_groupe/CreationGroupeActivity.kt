package com.android.projet.revision_groupe

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.projet.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreationGroupeActivity : AppCompatActivity() {

    private lateinit var nomGroupe: TextInputLayout
    private lateinit var matiere: TextInputLayout
    private lateinit var description: TextInputLayout
    private lateinit var btnCreer: Button
    private lateinit var btnVoirGroupes: Button

    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation_groupe)

        nomGroupe = findViewById(R.id.nom_groupe)
        matiere = findViewById(R.id.matiere)
        description = findViewById(R.id.description_groupe)
        btnCreer = findViewById(R.id.button_creation_compte)
        btnVoirGroupes = findViewById(R.id.button_voir_groupes)

        btnCreer.setOnClickListener {
            creerGroupe()
        }

        btnVoirGroupes.setOnClickListener {
            startActivity(Intent(this, ListeGroupesActivity::class.java))
        }
    }

    private fun creerGroupe() {
        val nom = nomGroupe.editText?.text.toString().trim()
        val matiereText = matiere.editText?.text.toString().trim()
        val desc = description.editText?.text.toString().trim()

        if (nom.isEmpty() || matiereText.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = currentUser?.uid ?: return

        val groupeData = hashMapOf(
            "nom" to nom,
            "matiere" to matiereText,
            "description" to desc,
            "createur" to uid,
            "membres" to listOf(uid),
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("groupes")
            .add(groupeData)
            .addOnSuccessListener {
                Toast.makeText(this, "Groupe créé avec succès", Toast.LENGTH_SHORT).show()
                nomGroupe.editText?.setText("")
                matiere.editText?.setText("")
                description.editText?.setText("")
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erreur lors de la création du groupe", Toast.LENGTH_SHORT).show()
            }
    }
}