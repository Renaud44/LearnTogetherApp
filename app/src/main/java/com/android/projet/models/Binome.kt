package com.android.projet.models

data class Binome(
    var uuid: String = "",
    var email: String = "",
    var pseudo: String = "",
    var image: String? = null,
    var matieresDifficiles: String? = null,
    var matieresMaitrisees: String? = null
)
