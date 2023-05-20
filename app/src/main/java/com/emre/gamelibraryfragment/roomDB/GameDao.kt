package com.emre.gamelibraryfragment.roomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.emre.gamelibraryfragment.model.Games
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface GameDao {

    @Query("select * from Games")
    fun getAll(): Flowable<List<Games>>

    @Query("select * from Games where id = :id")
    fun getDataWithID(id: Int): Flowable<Games>

    @Insert
    fun insert(games: Games): Completable

    @Delete
    fun delete(games: Games): Completable

    @Update
    fun update(games: Games): Completable
}