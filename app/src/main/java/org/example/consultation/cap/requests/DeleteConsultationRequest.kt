package org.example.consultation.cap.requests
import java.io.Serializable

class DeleteConsultationRequest(val consultationId: Int) : Serializable { // <-- consultationId
    companion object { private const val serialVersionUID = 1L }
}