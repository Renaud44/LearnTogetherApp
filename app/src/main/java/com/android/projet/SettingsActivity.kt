package com.android.projet

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.android.projet.models.User
import java.io.File
import java.io.FileOutputStream

class SettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private lateinit var profilImage: ShapeableImageView
    private lateinit var pseudoSettings: TextInputLayout
    private lateinit var emailSettings: TextInputLayout
    private lateinit var matieresDifficilesSettings: TextInputLayout
    private lateinit var matieresMaitriseesSettings: TextInputLayout
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button

    private var selectedImageUri: Uri? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        profilImage = findViewById(R.id.profilImage)
        pseudoSettings = findViewById(R.id.pseudo_settings)
        emailSettings = findViewById(R.id.email_settings)
        matieresDifficilesSettings = findViewById(R.id.matieres_difficiles_settings)
        matieresMaitriseesSettings = findViewById(R.id.matieres_maitrisees_settings)
        btnSave = findViewById(R.id.button_save)
        btnLogout = findViewById(R.id.button_logout)

        val currentUser = auth.currentUser

        loadProfileImage()

        currentUser?.let { user ->
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { result ->
                    val firestoreUser = result.toObject(User::class.java)
                    firestoreUser?.let {
                        emailSettings.editText?.setText(it.email)
                        pseudoSettings.editText?.setText(it.pseudo)
                        matieresDifficilesSettings.editText?.setText(it.matieresDifficiles)
                        matieresMaitriseesSettings.editText?.setText(it.matieresMaitrisees)
                    }
                }
        }

        val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                val bitmap = uriToBitmap(it)
                bitmap?.let { bmp ->
                    saveImageToInternalStorage(bmp)
                    loadProfileImage()
                }
            }
        }

        profilImage.setOnClickListener {
            pickImage.launch("image/*")
        }

        btnSave.setOnClickListener {
            val newPseudo = pseudoSettings.editText?.text.toString()
            val newDiff = matieresDifficilesSettings.editText?.text.toString()
            val newMaitrise = matieresMaitriseesSettings.editText?.text.toString()

            currentUser?.let { user ->
                val updates = mapOf(
                    "pseudo" to newPseudo,
                    "matieresDifficiles" to newDiff,
                    "matieresMaitrisees" to newMaitrise
                )
                db.collection("users").document(user.uid).update(updates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Informations mises à jour", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, ConnexionActivity::class.java))
            finish()
        }
    }

    private fun loadProfileImage() {
        val imageFile = File(filesDir, "profile_image.jpg")
        if (imageFile.exists()) {
            Glide.with(this)
                .load(imageFile)
                .placeholder(R.drawable.ic_profil)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profilImage)
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                android.provider.MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap) {
        try {
            val file = File(filesDir, "profile_image.jpg")
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
