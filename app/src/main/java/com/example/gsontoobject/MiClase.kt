package com.example.gsontoobject

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL


class MiCLase {
    suspend fun getPersonajeFromApi(url: String): Personaje {
        return withContext(Dispatchers.IO) {
            val response = URL(url).readText()
            Gson().fromJson(response, Personaje::class.java)
        }
    }

    suspend fun getMovieFromApi(url: String): Peliculas {
        return withContext(Dispatchers.IO) {
            val response = URL(url).readText()
            Gson().fromJson(response, Peliculas::class.java)
        }
    }

}
