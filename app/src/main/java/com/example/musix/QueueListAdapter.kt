package com.example.musix

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musix.databinding.ActivityQueueListBinding
import com.example.musix.databinding.QueueRecyclerItemBinding

class QueueListAdapter( val context: Context, var queueList : ArrayList<AudioModel>)
    : RecyclerView.Adapter<QueueListAdapter.ViewHolder>() {
    class ViewHolder(binding: QueueRecyclerItemBinding) :RecyclerView.ViewHolder(binding.root) {
        val queueTitle = binding.musicTitleText
        val queueButton = binding.ClearBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return QueueListAdapter.ViewHolder(QueueRecyclerItemBinding
            .inflate(LayoutInflater.from(context),parent,false))
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val dbHandler = QueueListDatabase(context)
        val queueList = dbHandler.getQueueSongsList()
        if (queueList.isNotEmpty()){
        holder.queueTitle.text = queueList[position].title

        }
        holder.queueButton.setOnClickListener{
            holder.queueButton.isActivated = true
            val db = QueueListDatabase(context)
            val currentSong : AudioModel = queueList[position]
            db.removeQueueSong(currentSong)
            refreshPlayList()
        }

    }

    override fun getItemCount(): Int {
       return queueList.size
    }

fun refreshPlayList(){
    queueList = ArrayList()
    queueList.addAll(QueueListDatabase(context).getQueueSongsList())
    notifyDataSetChanged()
}


}
