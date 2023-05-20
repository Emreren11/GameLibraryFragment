package com.emre.gamelibraryfragment.roomDB

import androidx.room.Database
import androidx.room.RoomDatabase
import com.emre.gamelibraryfragment.model.Games

@Database(entities = [Games::class], version = 1)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}