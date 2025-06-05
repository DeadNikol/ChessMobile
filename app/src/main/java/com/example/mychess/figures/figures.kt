package com.example.mychess.figures

import android.content.Context
import android.content.SharedPreferences
import android.os.Parcelable
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.example.mychess.R
import kotlinx.parcelize.Parcelize

/**
 * Этот класс содержит в себе константы для обозначения фигуры на поле
 */
enum class Figure(val value: Int) {
    NONE(0), PAWN(1), ROOK(2), KNIGHT(3), BISHOP(4), QUEEN(5), KING(6);
}

/**
 * Этот класс содержит в себе константы для обозначения стороны
 */
enum class Sight(val value: Int) {
    WHITE(1), BLACK(-1), NONE(0);
}

/**
 * Этот класс хранит в себе информацию о каждом поле, включая объект кнопки, FrameLayout'а, положения и фигуры
 */
data class Plate(
    val button: ImageButton,
    val frame: FrameLayout,
    var row: Int,
    var column: Int,
    var figure: Figure,
    var sight: Sight,
    var enableToMove: Boolean = false,
    var enableToAttack: Boolean = false,
    var enPassant: Sight = Sight.NONE,
    var enPassantTurn: Int? = null,
    var canBeAttackedByWhite: Boolean = false,
    var canBeAttackedByBlack: Boolean = false,)
{
    /**
     * Метод выставляет на плитке фигуру, соответсвующую значениям figure и sight
     */
    fun update(sharedPreferences: SharedPreferences? = null) {
        when (figure) {
            Figure.NONE -> button.foreground = null
            Figure.PAWN -> if (sight == Sight.BLACK) button.foreground =
                ContextCompat.getDrawable(button.context, R.drawable.black_pawn)
            else button.foreground =
                ContextCompat.getDrawable(button.context, R.drawable.white_pawn)

            Figure.ROOK -> if (sight == Sight.BLACK) button.foreground =
                ContextCompat.getDrawable(button.context, R.drawable.black_rook)
            else button.foreground =
                ContextCompat.getDrawable(button.context, R.drawable.white_rook)

            Figure.KNIGHT -> if (sight == Sight.BLACK) button.foreground =
                ContextCompat.getDrawable(button.context, R.drawable.black_knight)
            else button.foreground =
                ContextCompat.getDrawable(button.context, R.drawable.white_knight)

            Figure.BISHOP -> if (sight == Sight.BLACK) button.foreground =
                ContextCompat.getDrawable(button.context, R.drawable.black_bishop)
            else button.foreground =
                ContextCompat.getDrawable(button.context, R.drawable.white_bishop)

            Figure.QUEEN -> if (sight == Sight.BLACK) button.foreground =
                ContextCompat.getDrawable(button.context, R.drawable.black_queen)
            else button.foreground =
                ContextCompat.getDrawable(button.context, R.drawable.white_queen)

            Figure.KING -> if (sight == Sight.BLACK) button.foreground =
                ContextCompat.getDrawable(button.context, R.drawable.black_king)
            else button.foreground =
                ContextCompat.getDrawable(button.context, R.drawable.white_king)
        }
        if(sharedPreferences?.getBoolean("reverseBlackPawn", false) == true && sight == Sight.BLACK){
            button.rotationX = 180f
        } else{button.rotationX = 0f}

    }

    /**
     * Метод убирает с клетки значения фигуры и стороны и саму фигуру
     */
    fun removeFigure() {
        figure = Figure.NONE
        sight = Sight.NONE
        update()
    }

    /**
     * Метод добавляет/убирает возможность походить выбранной фигурой на это поле
     */
    fun enableToMove(enabled: Boolean) {
        enableToMove = enabled
        if (enableToMove)
            frame.foreground = ContextCompat.getDrawable(frame.context, R.drawable.point_to_move)
        else
            frame.foreground = null
    }

    /**
     * Метод добавляет/убирает возможность атаковать выбранной фигурой это поле
     */
    fun enableToAttack(enabled: Boolean) {
        enableToAttack = enabled
        if (enableToAttack)
            frame.foreground = ContextCompat.getDrawable(frame.context, R.drawable.attacked_plate)
        else
            frame.foreground = null

    }

    fun getPlateId(): Int {
        return 8 * (8 - row) + (column - 1)
    }

    /**
     * Отмечает, находится ли клетка под боем. Принимает в себя sight(Сторона, под боем которой клетка находится) и attackable(Находится ли под боем или нет)
     */
    fun enableToAttackedInTheory(sight: Sight, attackable: Boolean) {
        when (sight) {
            Sight.WHITE -> {
                canBeAttackedByWhite = attackable
            }

            Sight.BLACK -> {
                canBeAttackedByBlack = attackable
            }

            Sight.NONE -> {}
        }
    }

    fun copy(): Plate {
        return Plate(
            button,
            frame,
            row,
            column,
            figure,
            sight,
            enableToMove,
            enableToAttack,
            enPassant,
            enPassantTurn,
            canBeAttackedByWhite,
            canBeAttackedByBlack
        )
    }
}