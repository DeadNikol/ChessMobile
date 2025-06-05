package com.example.mychess.room.data

import androidx.lifecycle.LiveData
/**
 * Здесь хранятся все данные и отсюда вызываются все методы
 */
class Repository(private val dao: Dao) {

    val readAllData: LiveData<List<Moves>> = dao.readAllData()
    val readOldPlates: LiveData<List<OldPlate>> = dao.readOldPlates()

    suspend fun addMove(move: Moves){
        dao.addMove(move)
    }

    suspend fun deleteAll(){
        dao.deleteAllMoves()
    }

    suspend fun addOldPlate(oldPlate: OldPlate){
        dao.addOldPlate(oldPlate)
    }
}