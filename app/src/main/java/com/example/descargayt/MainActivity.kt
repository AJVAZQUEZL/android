package com.example.descargayt

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar el video de fondo
        setupBackgroundVideo()

        // Iniciar Python en la app
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        // Conectar el código con los IDs de tu XML
        val urlInput = findViewById<EditText>(R.id.urlInput)
        val modeGroup = findViewById<RadioGroup>(R.id.modeGroup)
        val downloadButton = findViewById<Button>(R.id.downloadButton)

        // Darle vida al botón
        downloadButton.setOnClickListener {
            val url = urlInput.text.toString()

            if (url.isEmpty()) {
                Toast.makeText(this, "Por favor, pega un enlace", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val py = Python.getInstance()
                val module = py.getModule("downloader")

                val botonSeleccionado = findViewById<android.widget.RadioButton>(modeGroup.checkedRadioButtonId)
                val modo = if (botonSeleccionado?.text.toString().contains("Audio", ignoreCase = true)) "audio" else "video"

                val rutaDescarga = android.os.Environment.getExternalStoragePublicDirectory(
                    android.os.Environment.DIRECTORY_DOWNLOADS
                ).absolutePath
                
                val resultado = module.callAttr("descargar", url, modo, rutaDescarga)

                Toast.makeText(this, "Descarga terminada: $resultado", Toast.LENGTH_LONG).show()
                
                android.media.MediaScannerConnection.scanFile(
                    this, arrayOf(java.io.File(rutaDescarga).absolutePath), null
                ) { _, _ -> }

            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupBackgroundVideo() {
        val videoView = findViewById<VideoView>(R.id.backgroundVideoView) ?: return
        val videoResId = resources.getIdentifier("background_video", "raw", packageName)
        
        if (videoResId != 0) {
            val uri = Uri.parse("android.resource://$packageName/$videoResId")
            videoView.setVideoURI(uri)
            videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                mp.setVolume(0f, 0f)
                
                val videoWidth = mp.videoWidth.toFloat()
                val videoHeight = mp.videoHeight.toFloat()
                val viewWidth = videoView.width.toFloat()
                val viewHeight = videoView.height.toFloat()

                val scaleX = viewWidth / videoWidth
                val scaleY = viewHeight / videoHeight
                val scale = Math.max(scaleX, scaleY)

                videoView.scaleX = scale / scaleX
                videoView.scaleY = scale / scaleY
                
                videoView.start()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        findViewById<VideoView>(R.id.backgroundVideoView)?.start()
    }
}