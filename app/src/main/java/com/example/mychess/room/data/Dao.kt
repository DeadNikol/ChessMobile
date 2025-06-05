package com.example.mychess.room.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Это интерфейс обращения к любой базе данных
 */
@Dao
interface Dao {

    //Для ходов в рамках одной партии
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMove(moves: Moves)

    @Query("select * from moves_table order by id asc") //Берёт все записи из таблицы с ходами(Класс, обёрнутый в @Entity)
    fun readAllData(): LiveData<List<Moves>>

    @Query("delete from moves_table")
    suspend fun deleteAllMoves()


    //Для запоминания расположения
    @Query("select * from old_plates order by id asc")
    fun readOldPlates(): LiveData<List<OldPlate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addOldPlate(oldPlate: OldPlate)

}