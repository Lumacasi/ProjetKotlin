package org.example.consultation.dal.entity

import java.io.Serializable
import java.time.LocalDate
import java.time.LocalTime

class Consultation(
    var id: Int? = null,
    var doctor: Any? = null,
    var patient: Patient? = null,
    var date: LocalDate? = null,
    var hour: LocalTime? = null,
    var reason: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}