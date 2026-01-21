package com.example.projetkotlin.data.network

import android.util.Log
import com.example.projetkotlin.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.consultation.cap.requests.LoginRequest
import org.example.consultation.cap.requests.SearchConsultationsRequest
import org.example.consultation.cap.responses.LoginResponse
import org.example.consultation.cap.responses.SearchConsultationsResponse
import org.example.consultation.dal.entity.Consultation
import org.example.consultation.dal.entity.Patient
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import org.example.consultation.cap.requests.AddConsultationRequest
import org.example.consultation.cap.requests.GetPatientsRequest
import org.example.consultation.cap.responses.GetPatientsResponse


class NetworkManager {

    suspend fun sendLogin(login: String, pass: String): LoginResponse? {
        return withContext(Dispatchers.IO) {
            var socket: Socket? = null
            try {
                socket = Socket(Constants.SERVER_IP, Constants.SERVER_PORT)

                val oos = ObjectOutputStream(socket.getOutputStream())
                oos.flush()
                val ois = ObjectInputStream(socket.getInputStream())

                val requete = LoginRequest(login, pass)
                oos.writeObject(requete)
                oos.flush()

                val response = ois.readObject() as? LoginResponse
                return@withContext response

            } catch (e: Exception) {
                Log.e("NETWORK", "Erreur Login: ${e.message}")
                null
            } finally {
                socket?.close()
            }
        }
    }

    // MODIFICATION ICI : On accepte l'objet SearchConsultationsRequest complet
    suspend fun getConsultations(request: SearchConsultationsRequest): List<Consultation> {
        return withContext(Dispatchers.IO) {
            var socket: Socket? = null
            try {
                socket = Socket(Constants.SERVER_IP, Constants.SERVER_PORT)
                val oos = ObjectOutputStream(socket.getOutputStream())
                oos.flush() // Important de flush après création

                // Envoi de l'objet de requête tel quel
                oos.writeObject(request)
                oos.flush()

                val ois = ObjectInputStream(socket.getInputStream())
                val response = ois.readObject() as? SearchConsultationsResponse

                response?.consultations ?: emptyList()
            } catch (e: Exception) {
                Log.e("CAP_PROT", "Erreur lors de SEARCH_CONSULTATIONS: ${e.message}")
                emptyList()
            } finally {
                socket?.close()
            }
        }
    }

    // AJOUT : Pour la suite "ADD_CONSULTATION"
    suspend fun addConsultations(request: AddConsultationRequest): Boolean {        return withContext(Dispatchers.IO) {
            var socket: Socket? = null
            try {
                socket = Socket(Constants.SERVER_IP, Constants.SERVER_PORT)
                val oos = ObjectOutputStream(socket.getOutputStream())
                oos.flush()

                oos.writeObject(request)
                oos.flush()

                val ois = ObjectInputStream(socket.getInputStream())
                // Le serveur répond par un Boolean selon ton protocole
                ois.readObject() as Boolean
            } catch (e: Exception) {
                Log.e("CAP_PROT", "Erreur lors de ADD_CONSULTATION: ${e.message}")
                false
            } finally {
                socket?.close()
            }
        }
    }

    suspend fun getAllPatients(): List<Patient> {
        return withContext(Dispatchers.IO) {
            var socket: Socket? = null
            try {
                socket = Socket(Constants.SERVER_IP, Constants.SERVER_PORT)
                val oos = ObjectOutputStream(socket.getOutputStream())
                oos.flush()

                // Envoi de la requête GetPatientsRequest (pense à l'importer)
                oos.writeObject(org.example.consultation.cap.requests.GetPatientsRequest())
                oos.flush()

                val ois = ObjectInputStream(socket.getInputStream())
                val response = ois.readObject() as? GetPatientsResponse
                return@withContext response?.patients ?: emptyList()
            } catch (e: Exception) {
                Log.e("NETWORK", "Erreur GetPatients: ${e.message}")
                emptyList()
            } finally {
                socket?.close()
            }
        }
    }
}