package org.hyperskill.phrases

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phrases")
data class Phrase (
    @ColumnInfo var phrase: String,
    @PrimaryKey(autoGenerate = true) var id: Int = 0
)