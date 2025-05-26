package com.android.projet.models

data class User(
    var uuid: String = "",
    var email: String = "",
    var pseudo: String = "",
    var image: String? = "",
    var matieresDifficiles: String? = null,
    var matieresMaitrisees: String? = null,
    var binomesAcceptes: List<String> = emptyList(),
    var demandesRecues: List<String> = emptyList(),
    var lastMessage: String? = null
)