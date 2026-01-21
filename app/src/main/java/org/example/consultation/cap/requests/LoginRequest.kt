package org.example.consultation.cap.requests

import java.io.Serializable

data class LoginRequest(
    val login: String,
    val pass: String
) : Serializable {
    companion object {
        // 'const' en Kotlin place la valeur directement dans la classe parente
        // comme un 'public static final long' en Java.
        const val serialVersionUID: Long = -5850947417734415419L
    }
}