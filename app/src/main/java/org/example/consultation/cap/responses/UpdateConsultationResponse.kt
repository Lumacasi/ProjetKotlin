package org.example.consultation.cap.responses

import java.io.Serializable

class UpdateConsultationResponse(
    val success: Boolean,
    val message: String
) : Serializable {
    companion object { private const val serialVersionUID = 1L }
}