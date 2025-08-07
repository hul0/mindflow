package com.hul0.mindflow.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey val id: String,
    val name: String,
    val source: String,
    val quotedBy: String,
    val type: String, // Category
    var isFavorite: Boolean = false
)
