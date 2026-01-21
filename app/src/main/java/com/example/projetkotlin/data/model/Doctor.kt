package org.example.consultation.dal.entity

import java.io.Serializable

class Doctor(
    var id: Int? = null,
    var lastName: String? = null,
    var firstName: String? = null,
    var login: String? = null,
    var password: String? = null,
    var speciality: Any? = null // On met Any? pour l'instant si tu n'as pas l'entité Speciality
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L // Crucial pour la compatibilité avec le serveur
    }
}