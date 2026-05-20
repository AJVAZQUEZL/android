package com.example.descargayt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Esto le dice a Kotlin que use tu diseño XML
        setContentView(R.layout.activity_main)

        // 2. Iniciar Python en la app
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        // 3. Conectar el código con los IDs de tu XML
        val urlInput = findViewById<EditText>(R.id.urlInput)
        val modeGroup = findViewById<RadioGroup>(R.id.modeGroup)
        val downloadButton = findViewById<Button>(R.id.downloadButton)

        // 4. Darle vida al botón
        downloadButton.setOnClickListener {
            // ¡ESTA ES LA LÍNEA QUE SE HABÍA BORRADO! Lee el texto que el usuario pegó
            val url = urlInput.text.toString()

            // Verificamos que no esté vacío
            if (url.isEmpty()) {
                Toast.makeText(this, "Por favor, pega un enlace", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // Detiene el código si no hay link
            }

            try {
                val py = Python.getInstance()
                val module = py.getModule("downloader")

                // 1. Averiguar qué modo eligió el usuario
                val botonSeleccionado = findViewById<android.widget.RadioButton>(modeGroup.checkedRadioButtonId)
                val modo = if (botonSeleccionado?.text.toString().contains("Audio", ignoreCase = true)) "audio" else "video"

                // 2. Conseguir la ruta de Descargas del celular
                // Esto apunta a la carpeta "Download" principal de tu teléfono
                val rutaDescarga = android.os.Environment.getExternalStoragePublicDirectory(
                    android.os.Environment.DIRECTORY_DOWNLOADS
                ).absolutePath
                // 3. Llamar a Python
                val resultado = module.callAttr("descargar", url, modo, rutaDescarga)

                // Mostrar éxito
                Toast.makeText(this, "Descarga terminada: $resultado", Toast.LENGTH_LONG).show()
                urlInput.text.clear()
                // Avisar a la Galería que hay un archivo nuevo para que lo muestre
                android.media.MediaScannerConnection.scanFile(
                    this, arrayOf(java.io.File(rutaDescarga).absolutePath), null
                ) { path, uri ->
                    // Opcional: loguear que el escaneo terminó
                }

            } catch (e: Exception) {
                Toast.makeText(this, "Error de Python: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}