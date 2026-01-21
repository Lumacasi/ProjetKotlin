package org.example.consultation.cap.responses

import java.io.Serializable

// Assure-toi que la classe implémente bien Serializable
class LoginResponse(
    val success: Boolean,
    val message: String
) : Serializable {

    companion object {
        // On force la valeur 1 pour correspondre à ce que le serveur envoie
        private const val serialVersionUID: Long = 1L
    }
}