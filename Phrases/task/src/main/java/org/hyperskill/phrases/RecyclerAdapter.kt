package org.hyperskill.phrases

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(data: List<Phrase>) : RecyclerView.Adapter<RecyclerAdapter.PhraseViewHolder>() {

    var data: List<Phrase> = data
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhraseViewHolder {
        return PhraseViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.phrase_item, parent, false))
    }

    override fun onBindViewHolder(holder: PhraseViewHolder, position: Int) {
        val phrase = data[position]
        holder.phrasetv.text = phrase.phrase
        holder.deletetv.setOnClickListener {
            //phrases.removeAt(position)
            val ctx = holder.deletetv.context
            val appDatabase = (ctx.applicationContext as MyApplication).database
            appDatabase.getPhraseDao().delete(phrase)
            data = appDatabase.getPhraseDao().getAll()
            //notifyItemRemoved(position)
            //notifyItemRangeChanged(position, data.size)
            if (data.isEmpty()) {
                val am: AlarmManager = ctx.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
                val activity = ctx as MainActivity
                am.cancel(activity.pendingIntent)
                activity.findViewById<TextView>(R.id.reminderTextView).text = "No reminder set"
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class PhraseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val phrasetv = view.findViewById<TextView>(R.id.phraseTextView)
        val deletetv = view.findViewById<TextView>(R.id.deleteTextView)
    }
}