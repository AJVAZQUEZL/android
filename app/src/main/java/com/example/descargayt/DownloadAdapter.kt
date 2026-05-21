package com.example.descargayt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

data class DownloadItem(
    val url: String = "",
    val mode: String = "",
    val fileName: String = "",
    val timestamp: Long = 0
)

class DownloadAdapter(private val downloads: List<DownloadItem>) :
    RecyclerView.Adapter<DownloadAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileName: TextView = view.findViewById(R.id.itemFileName)
        val url: TextView = view.findViewById(R.id.itemUrl)
        val mode: TextView = view.findViewById(R.id.itemMode)
        val date: TextView = view.findViewById(R.id.itemDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_download, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = downloads[position]
        holder.fileName.text = item.fileName
        holder.url.text = item.url
        holder.mode.text = item.mode.uppercase()
        
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.date.text = sdf.format(Date(item.timestamp))
    }

    override fun getItemCount() = downloads.size
}