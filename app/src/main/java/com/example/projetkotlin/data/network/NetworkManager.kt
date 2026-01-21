package com.example.projetkotlin.data.network

import android.util.Log
import com.example.projetkotlin.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.example.consultation.cap.requests.LoginRequest
import org.example.consultation.cap.responses.LoginResponse
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class NetworkManager {

    suspend fun sendLogin(login: String, pass: String): LoginResponse? {
        return withContext(Dispatchers.IO) {
            var socket: Socket? = null
            try {
                // Utilise l'IP de ton PC (10.0.2.2 pour l'émulateur) et le port du serveur
                socket = Socket(Constants.SERVER_IP, Constants.SERVER_PORT)

                val oos = ObjectOutputStream(socket.getOutputStream())
                oos.flush()
                val ois = ObjectInputStream(socket.getInputStream())

                // Création de la requête selon le nouveau modèle du pote
                val requete = LoginRequest(login, pass)
                oos.writeObject(requete)
                oos.flush()

                // Lecture de la réponse (LoginResponse)
                val response = ois.readObject() as? LoginResponse
                return@withContext response

            } catch (e: Exception) {
                Log.e("NETWORK", "Erreur: ${e.message}")
                null
            } finally {
                socket?.close()
            }
        }
    }
}