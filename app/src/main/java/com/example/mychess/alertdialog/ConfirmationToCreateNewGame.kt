package com.example.mychess.alertdialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.mychess.figures.Figure
import com.example.mychess.figures.Plate
import com.example.mychess.figures.Sight
import com.example.mychess.room.data.OldPlate
import com.example.mychess.room.data.ViewModelForRoom
import com.example.mychess.viewmodel.MainActivityViewModel

class ConfirmationToCreateNewGame(
    val viewModel: MainActivityViewModel,
    val viewModelForRoom: ViewModelForRoom,
    val allPlates: List<Plate>,
    val sharedPreferences: SharedPreferences
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it)
                .setMessage("Создать новую игру")
                .setTitle("Вы уверены?")
                .setPositiveButton("Да") { dialog, which ->
                    viewModelForRoom.deleteAll()

                    //Статистика фигур каждой стороны
                    viewModel.getWhiteFigures().value = mutableMapOf(
                        Figure.PAWN to 8, Figure.ROOK to 2,
                        Figure.KNIGHT to 2, Figure.BISHOP to 2, Figure.QUEEN to 1, Figure.KING to 1
                    )
                    viewModel.getBlackFigures().value = mutableMapOf(
                        Figure.PAWN to 8, Figure.ROOK to 2,
                        Figure.KNIGHT to 2, Figure.BISHOP to 2, Figure.QUEEN to 1, Figure.KING to 1
                    )

                    //Возможность участия в рокировке для каждой из шести фигур
                    viewModel.getMovedBefore().value =
                        mutableListOf(false, false, false, false, false, false)
                    //Расстановка фигур
                    allPlates.forEachIndexed { index, it ->
                        when (it.row) {
                            1 -> {
                                it.sight = Sight.WHITE
                                when (it.column) {
                                    1, 8 -> it.figure = Figure.ROOK
                                    2, 7 -> it.figure = Figure.KNIGHT
                                    3, 6 -> it.figure = Figure.BISHOP
                                    4 -> it.figure = Figure.QUEEN
                                    5 -> it.figure = Figure.KING
                                }
                            }

                            2 -> {
                                it.sight = Sight.WHITE
                                it.figure = Figure.PAWN
                            }

                            8 -> {
                                it.sight = Sight.BLACK
                                when (it.column) {
                                    1, 8 -> it.figure = Figure.ROOK
                                    2, 7 -> it.figure = Figure.KNIGHT
                                    3, 6 -> it.figure = Figure.BISHOP
                                    4 -> it.figure = Figure.QUEEN
                                    5 -> it.figure = Figure.KING
                                }
                            }

                            7 -> {
                                it.sight = Sight.BLACK
                                it.figure = Figure.PAWN
                            }

                            else -> {
                                it.sight = Sight.NONE
                                it.figure = Figure.NONE
                            }
                        }
                        it.update(sharedPreferences)
                        it.enableToMove(false)
                        it.enableToAttack(false)
                        viewModelForRoom.addOldPlate(
                            OldPlate(
                                index,
                                it.figure.value,
                                it.sight.value
                            )
                        ) //Если я создал игру заново, то записываю все фигуры заново
                    }

                    //Право хода
                    viewModel.getPlayersTurn().value = Sight.WHITE


                    viewModel.getTurns().value = 1
                }
                .setNegativeButton("Нет") { dialog, which -> null }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}