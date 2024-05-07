package org.hyperskill.phrases

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

const val CHANNEL_ID = "org.hyperskill.phrases"
const val ID = 393939

class MainActivity : AppCompatActivity() {
    lateinit var pendingIntent: PendingIntent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appDatabase = (application as MyApplication).database

        val data = appDatabase.getPhraseDao().getAll() //Book().generatePhrases()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = RecyclerAdapter(data)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
                setDrawable(AppCompatResources.getDrawable(this@MainActivity, R.drawable.divider)!!)
            }
        )

        createNotificationChannel()

        val reminderTextView = findViewById<TextView>(R.id.reminderTextView)
        reminderTextView.setOnClickListener {
            val size = appDatabase.getPhraseDao().getAll().size
            if (size != 0) {
                val dialog = TimePickerDialog(this, { _, hour, minute ->
                    reminderTextView.text = String.format("Reminder set for %02d:%02d", hour, minute)
                    val intent = Intent(applicationContext, Receiver::class.java)
                    pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = System.currentTimeMillis()
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    val currentTime = System.currentTimeMillis()
                    if (currentTime >= calendar.timeInMillis) calendar.timeInMillis += 24*60*60*1000
                    val chosenTime = calendar.timeInMillis
                    val am: AlarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                    am.setRepeating(AlarmManager.RTC_WAKEUP, chosenTime, AlarmManager.INTERVAL_DAY, pendingIntent)
                }, 9, 0, true)
                dialog.show()
            } else {
                Toast.makeText(this, "Error: Database is empty.", Toast.LENGTH_SHORT).show()
                reminderTextView.text = "No reminder set"
            }
        }
        val addButton = findViewById<FloatingActionButton>(R.id.addButton)
        addButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            // Get the layout inflater.
            //val inflater = layoutInflater;

            // Inflate and set the layout for the dialog.
            // Pass null as the parent view because it's going in the dialog
            // layout.
            val inputEditTextField = EditText(this)
            inputEditTextField.id = R.id.editText
            inputEditTextField.hint = "Phrase"
            builder.setView(inputEditTextField)//inflater.inflate(R.layout.dialog_add, null))
                .setTitle("Add phrase")
                // Add action buttons.
                .setPositiveButton("Add") { dialog, id ->
                    val string = inputEditTextField.text.toString() //findViewById<EditText>(R.id.editText).text.toString()
                    appDatabase.getPhraseDao().insert(Phrase(string))
                    val adapter = recyclerView.adapter as RecyclerAdapter
                    adapter.data = appDatabase.getPhraseDao().getAll()
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.cancel()
                }
            val alertDialog = builder.create()
            alertDialog?.show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Phrases channel"
            val descriptionText = "------------------"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}