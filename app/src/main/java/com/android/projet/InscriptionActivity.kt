package com.android.projet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class InscriptionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    lateinit var  emailInscription: TextInputLayout
    lateinit var  passwordInscription: TextInputLayout
    lateinit var  confirmPassword: TextInputLayout
    lateinit var  pseudo: TextInputLayout
    lateinit var buttonCreationCompte: Button
    lateinit var seConnecter: TextView




    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)



        // Initialisation de Firebase Auth
        auth = Firebase.auth

        //Récupération des id
        buttonCreationCompte = findViewById(R.id.button_creation_compte)
        emailInscription = findViewById(R.id.email_inscription)
        passwordInscription = findViewById(R.id.password_inscription)
        confirmPassword = findViewById(R.id.confirm_password)
        pseudo = findViewById(R.id.pseudo)
        seConnecter = findViewById(R.id.textView_se_connecter)

        //Clique sur le bouton créer un compte
        buttonCreationCompte.setOnClickListener{

            emailInscription.isErrorEnabled = false
            passwordInscription.isErrorEnabled = false
            pseudo.isErrorEnabled = false
            confirmPassword.isErrorEnabled = false

            //Récupération des textes des différents champs
            val txtEmail = emailInscription.editText?.text.toString()
            val txtPassword = passwordInscription.editText?.text.toString()
            val txtConfirmPassword= confirmPassword.editText?.text.toString()
            val txtPseudo = pseudo.editText?.text.toString()

            if (txtEmail.trim().isEmpty() || txtPassword.trim().isEmpty() ||
                txtPseudo.trim().isEmpty() || txtConfirmPassword.trim().isEmpty()) {
                if (txtEmail.trim().isEmpty()){
                    emailInscription.error = "L'email est requis"
                    emailInscription.isErrorEnabled = true
                }
                if(txtPassword.trim().isEmpty()){
                    passwordInscription.error = "Le mot de passe est requis"
                    passwordInscription.isErrorEnabled = true
                }
                if(txtConfirmPassword.trim().isEmpty()){
                    confirmPassword.error = "Le mot de passe est requis"
                    confirmPassword.isErrorEnabled = true
                }
                if (txtPseudo.trim().isEmpty()){
                    pseudo.error = "Le pseudo est requis"
                    pseudo.isErrorEnabled = true
                }

            }else{
                if (!txtConfirmPassword.equals(txtPassword)){
                    confirmPassword.error = "Les mots de passe ne correspondent pas"
                    confirmPassword.isErrorEnabled = true
                }else{
                    //Création d'un utilisateur dans le module d'authentification de firebase
                    auth.createUserWithEmailAndPassword(txtEmail, txtPassword).addOnCompleteListener{task->
                        if (task.isSuccessful){

                            val user = hashMapOf(
                                "pseudo" to txtPseudo,
                                "email" to txtEmail
                            )

                            val currentUser = auth.currentUser

                            //Création de l'utilisateur dans le module Firestore
                            val db = Firebase.firestore
                            db.collection("users").document(currentUser!!.uid).set(user).addOnSuccessListener {

                                Intent(this, HomeActivity::class.java).also {intentToHomeActivity: Intent ->
                                    startActivity(intentToHomeActivity)
                                }

                            }.addOnFailureListener{
                                pseudo.error = "Une erreur est survenue. Veuillez réessayer plus tard"
                                pseudo.isErrorEnabled = true
                            }

                        }else{
                            pseudo.error = "Une erreur est survenue. Veuillez réessayer plus tard"
                            pseudo.isErrorEnabled = true
                        }
                    }

                }
            }



        }

        //Clique sur le texte Se connecter
        seConnecter.setOnClickListener {
            Intent(this, ConnexionActivity::class.java).also { intentToConnexionActivity: Intent ->
                startActivity(intentToConnexionActivity)
            }

        }










    }
}
