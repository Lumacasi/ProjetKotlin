package org.example.consultation.cap.responses

import java.io.Serializable

data class LoginResponse(
    val success: Boolean,  // C'est ce "success" que l'UI doit lire
    val token: String?,
    val doctorId: Int?
) : Serializable