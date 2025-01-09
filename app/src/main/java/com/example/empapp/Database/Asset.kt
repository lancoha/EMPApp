package com.example.empapp.Database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asset")
data class Asset(
    @PrimaryKey val id: String,
    val isFavourite: Boolean
)
