package com.android.projet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class ConnexionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    lateinit var  emailConnexion: TextInputLayout
    lateinit var  passwordConnexion: TextInputLayout
    lateinit var buttonConnect: Button
    lateinit var createCompte: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connexion)



        // Initialize Firebase Auth
        auth = Firebase.auth

        //Récupération des id
        buttonConnect = findViewById(R.id.button_connexion)
        emailConnexion = findViewById(R.id.email_connexion)
        passwordConnexion = findViewById(R.id.password_connexion)
        createCompte = findViewById(R.id.textView_creer_compte)


    }

    override fun onStart() {
        super.onStart()

        //Clique sur le bouton Se connecter
        buttonConnect.setOnClickListener{
            emailConnexion.isErrorEnabled = false
            passwordConnexion.isErrorEnabled = false

            //Récupération des textes des champs
            val txtEmail = emailConnexion.editText?.text.toString()
            val txtPassword = passwordConnexion.editText?.text.toString()
            if (txtEmail.trim().isEmpty() || txtPassword.trim().isEmpty()){
                if (txtEmail.trim().isEmpty()) {
                    emailConnexion.error = "L'email est requis"
                    emailConnexion.isErrorEnabled = true
                }
                if (txtPassword.trim().isEmpty()){
                    passwordConnexion.error = "Le mot de passe est requis"
                    passwordConnexion.isErrorEnabled = true
                }

            }else{
                signIn(txtEmail, txtPassword)
            }

        }



        //Clique sur le texte Créer un compte
        createCompte.setOnClickListener{
            Intent(this, InscriptionActivity::class.java).also { intentToInscriptionActivity: Intent ->
                startActivity(intentToInscriptionActivity)
            }
        }
    }

    fun signIn(email: String, password: String){

        //Authentification avec email et password
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{ task ->

            if (task.isSuccessful){
                Intent(this, HomeActivity::class.java).also {intentToHomeActivity: Intent ->
                    startActivity(intentToHomeActivity)
                }

                finish()
            }else{
                passwordConnexion.error = "Erreur d'authentification"
                passwordConnexion.isErrorEnabled = true
                emailConnexion.isErrorEnabled = true
            }
        }


    }
}