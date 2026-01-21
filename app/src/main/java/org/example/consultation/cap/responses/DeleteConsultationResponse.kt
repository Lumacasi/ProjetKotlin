package org.example.consultation.cap.responses

import java.io.Serializable

class DeleteConsultationResponse(
    val success: Boolean = false,
    val message: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}