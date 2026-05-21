package com.example.descargayt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    // Scope para manejar tareas en segundo plano
    private val activityScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        val urlInput = findViewById<EditText>(R.id.urlInput)
        val modeGroup = findViewById<RadioGroup>(R.id.modeGroup)
        val downloadButton = findViewById<Button>(R.id.downloadButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val historyButton = findViewById<Button>(R.id.historyButton)

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        historyButton.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        downloadButton.setOnClickListener {
            val url = urlInput.text.toString()

            if (url.isEmpty()) {
                Toast.makeText(this, "Por favor, pega un enlace", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cambiar texto del botón para indicar que está trabajando
            downloadButton.isEnabled = false
            downloadButton.text = getString(R.string.status_downloading)

            // Ejecutar en segundo plano para que sea "rápido" y no trabe la app
            activityScope.launch {
                try {
                    val result = withContext(Dispatchers.IO) {
                        val py = Python.getInstance()
                        val module = py.getModule("downloader")

                        val botonSeleccionado = findViewById<android.widget.RadioButton>(modeGroup.checkedRadioButtonId)
                        val modo = if (botonSeleccionado?.text.toString().contains("Audio", ignoreCase = true)) "audio" else "video"

                        val rutaDescarga = android.os.Environment.getExternalStoragePublicDirectory(
                            android.os.Environment.DIRECTORY_DOWNLOADS
                        ).absolutePath

                        // Llamada a Python (Pesada)
                        val fileName = module.callAttr("descargar", url, modo, rutaDescarga).toString()
                        
                        // Guardar en historial inmediatamente
                        guardarEnHistorial(url, modo, fileName)
                        
                        Triple(fileName, rutaDescarga, modo)
                    }

                    val (fileName, rutaDescarga, _) = result

                    Toast.makeText(this@MainActivity, "¡Descarga Exitosa! $fileName", Toast.LENGTH_LONG).show()
                    urlInput.text.clear()

                    android.media.MediaScannerConnection.scanFile(
                        this@MainActivity, arrayOf(java.io.File(rutaDescarga, fileName).absolutePath), null
                    ) { _, _ -> }

                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                } finally {
                    downloadButton.isEnabled = true
                    downloadButton.text = getString(R.string.btn_download)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.cancel() // Cancelar tareas si se cierra la app
    }

    private fun guardarEnHistorial(url: String, modo: String, fileName: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val database = FirebaseDatabase.getInstance().reference
            val downloadId = database.child("users").child(user.uid).child("downloads").push().key

            val downloadData = mapOf(
                "url" to url,
                "mode" to modo,
                "fileName" to fileName,
                "timestamp" to System.currentTimeMillis()
            )

            if (downloadId != null) {
                database.child("users").child(user.uid).child("downloads").child(downloadId)
                    .setValue(downloadData)
                    .addOnFailureListener {
                        println("Error al guardar en Firebase: ${it.message}")
                    }
            }
        }
    }
}
