package com.example.gsontoobject

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.gsontoobject.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

data class Resultado<T>(val data: T)
class MiClase {
    suspend inline fun <reified T> getDataFromApi(url: String): Resultado<T> {
        return withContext(Dispatchers.IO) {
            val response = URL(url).readText()
            val gson = Gson()
            val data = when (T::class) {
                String::class -> {
                    response as T
                }

                else -> {
                    gson.fromJson(response, T::class.java)
                }
            }
            Resultado(data)
        }
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var direccion: String = "https://swapi.dev/api/people"
    private var direccionPelicula: String = "https://swapi.dev/api/films"
    private val miClase = MiClase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buscar.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val personaje = binding.personaje.text.toString()
            val url = "$direccion/$personaje"

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val personajeObject: Resultado<String> = miClase.getDataFromApi(url)
                    val jsonObject = JSONObject(personajeObject.data)

                    withContext(Dispatchers.Main) {

                        binding.campo1.text = ""
                        binding.campo2.text = ""
                        binding.campo3.text = ""
                        binding.campo1.text = jsonObject.getString("name")
                        binding.campo2.text = jsonObject.getString("gender")

                        val filmsArray = jsonObject.getJSONArray("films")
                        val movieNames = mutableListOf<String>()
                        for (i in 0 until filmsArray.length()) {
                            val filmUrl = filmsArray.getString(i)
                            val movieId = Uri.parse(filmUrl).lastPathSegment?.toIntOrNull()

                            if (movieId != null) {
                                val movieUrl = "$direccionPelicula/$movieId"
                                val movieData: Resultado<String> =
                                    miClase.getDataFromApi(movieUrl)
                                val movieJson = JSONObject(movieData.data)
                                movieNames.add(movieJson.getString("title"))
                            } else {
                                println("ERROR")
                            }
                        }
                        binding.campo3.text = movieNames.joinToString("\n")
                        binding.progressBar.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}


