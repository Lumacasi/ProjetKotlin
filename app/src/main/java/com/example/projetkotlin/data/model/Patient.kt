package org.example.consultation.dal.entity

import java.io.Serializable

class Patient(
    var id: Int? = null,
    var lastName: String? = null,
    var firstName: String? = null
) : Serializable {
    companion object {
        private const val serialVersionUID: Long = 1L
    }
}