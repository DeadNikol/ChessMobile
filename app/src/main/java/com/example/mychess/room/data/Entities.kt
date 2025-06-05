package com.example.mychess.room.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Это просто табличка в базе данных
 */
@Entity(tableName = "moves_table")
data class Moves(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val sight: Int,
    val fromPlateId: Int,
    val figure: Int,
    val toPlateId: Int,
    val check: Boolean,
)

/**
 * Эта табличка хранит расположения фигур и их цвет для каждой плитки с индексом [id]
 */
@Entity(tableName = "old_plates")
data class OldPlate(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val figure: Int,
    val sight: Int)