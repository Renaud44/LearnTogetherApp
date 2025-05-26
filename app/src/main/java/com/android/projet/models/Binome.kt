package com.android.projet.models

data class Binome(
    var uuid: String = "",
    val pseudo: String = "",
    val matieresDifficiles: String = "",
    val matieresMaitrisees: String = "",
    val demandeEnvoyeeA: List<String> = listOf(),      // UID des utilisateurs à qui j’ai envoyé une demande
    val demandesRecues: List<String> = listOf(),        // UID des utilisateurs qui m'ont envoyé une demande
    val binomesAcceptes: List<String> = listOf()        // Binômes confirmés
)

