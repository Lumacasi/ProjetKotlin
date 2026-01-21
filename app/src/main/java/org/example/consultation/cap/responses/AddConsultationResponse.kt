package org.example.consultation.cap.responses

import java.io.Serializable

class AddConsultationResponse(
    val success: Boolean,
    val message: String,
    val createdIds: List<Int> = emptyList()
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}