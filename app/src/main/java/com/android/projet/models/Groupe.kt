package com.android.projet.models

data class Groupe(
    var id: String = "",
    var nom: String = "",
    var matiere: String = "",
    var description: String = "",
    var adminId: String = "",
    var createur: String = "",
    var membres: List<String> = emptyList()
)