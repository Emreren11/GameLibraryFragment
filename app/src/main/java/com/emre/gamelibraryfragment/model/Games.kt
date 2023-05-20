package com.emre.gamelibraryfragment.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Games(
    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "image")
    var image: ByteArray
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}