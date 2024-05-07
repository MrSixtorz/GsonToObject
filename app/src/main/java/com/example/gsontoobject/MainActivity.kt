package com.example.gsontoobject

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.gsontoobject.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var direccion: String = "https://swapi.dev/api/people"
    private var direccionPelicula: String = "https://swapi.dev/api/films"
    private val miClase = MiCLase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buscar.setOnClickListener {
            val personaje = binding.personaje.text.toString()
            val url = "$direccion/$personaje"

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val personajeObject: Personaje = miClase.getPersonajeFromApi(url)
                    println("PERSONAJE: " + personajeObject)

                    withContext(Dispatchers.Main) {
                        binding.campo1.text = ""
                        binding.campo2.text = ""
                        binding.campo3.text = ""
                        binding.campo1.text = personajeObject.name
                        binding.campo2.text = personajeObject.gender

                        var movieNames = mutableListOf<String>()
                        for (filmUrl in personajeObject.films) {
                            var movieId = Uri.parse(filmUrl).lastPathSegment?.toIntOrNull()
                            println("MovieIdString: " + movieId)

                            if (movieId != null) {

                                var movieUrl = "$direccionPelicula/$movieId"
                                println("Movie URL: " + movieUrl)
                                var movieData = miClase.getMovieFromApi(movieUrl)
                                println("SSSSS: " + movieData)
                                movieNames.add(movieData.title)
                            } else {
                                println("ERROR")
                            }
                        }
                        binding.campo3.text = movieNames.joinToString("\n")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}

