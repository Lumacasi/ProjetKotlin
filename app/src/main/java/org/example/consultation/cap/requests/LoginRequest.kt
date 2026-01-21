package org.example.consultation.cap.requests

import java.io.Serializable

data class LoginRequest(
    val login: String,
    val pass: String
) : Serializable {
    // Utilise cette syntaxe précise
    companion object {
        @JvmStatic
        private val serialVersionUID: Long = -5850947417734415419L
    }
}