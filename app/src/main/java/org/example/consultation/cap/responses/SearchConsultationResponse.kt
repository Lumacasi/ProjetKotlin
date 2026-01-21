package org.example.consultation.cap.responses

import org.example.consultation.dal.entity.Consultation
import java.io.Serializable

class SearchConsultationsResponse(
    val success: Boolean = false,
    val message: String? = null,
    val consultations: List<Consultation> = emptyList()
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}