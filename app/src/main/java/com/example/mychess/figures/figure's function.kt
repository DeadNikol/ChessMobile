package com.example.mychess.figures

import android.content.SharedPreferences
import androidx.fragment.app.FragmentManager
import com.example.mychess.alertdialog.ChangePawnToAnotherFigureDialog
import com.example.mychess.figures.Figure.BISHOP
import com.example.mychess.figures.Figure.KING
import com.example.mychess.figures.Figure.KNIGHT
import com.example.mychess.figures.Figure.NONE
import com.example.mychess.figures.Figure.PAWN
import com.example.mychess.figures.Figure.QUEEN
import com.example.mychess.figures.Figure.ROOK
import com.example.mychess.room.data.Moves
import com.example.mychess.room.data.OldPlate
import com.example.mychess.room.data.ViewModelForRoom
import com.example.mychess.viewmodel.MainActivityViewModel
import kotlin.math.abs


/**
 * Функция в зависимости от фигуры определяет возможные поля для хода
 */
fun checkMoves(
    plate: Plate,
    allPlates: List<Plate>,
    playersTurn: Sight,
    viewModel: MainActivityViewModel,
    supportFragmentManager: FragmentManager,
    actualTurn: Int,
    viewModelForRoom: ViewModelForRoom,
    sharedPreferences: SharedPreferences,
) {
    val row = plate.row
    val column = plate.column

    val moveToPlatesId = mutableListOf<Int>()
    val attackToPlatesId = mutableListOf<Int>()

    /**
     * Метод обозначает поля, доступные для хода и/или атаки. Возвращает список, содержащий в себе `true` и `false`, значащих наличие шахов на каждом ходу
     */
    fun _moveOrAttack(
        moveToPlatesId: MutableList<Int>,
        attackToPlatesId: MutableList<Int>
    ): List<Boolean> {
        val checks = mutableListOf<Boolean>()

        //Проверка хода на наличие шахов
        fun accessToMove(allPlates: List<Plate>, plate: Plate): Boolean {
            val oldPlate = viewModel.getChosenFigure().value!!.copy()
            val newAllPlates = allPlates.copy()

            newAllPlates[plate.getPlateId()].apply {
                sight = oldPlate.sight
                figure = oldPlate.figure
            }
            newAllPlates[oldPlate.getPlateId()].apply {
                sight = Sight.NONE
                figure = NONE
            }
            return !(attackableInTheory(
                newAllPlates,
                viewModel
            ) && oldPlate.sight == viewModel.getCheck().value) //Если я ставлю шах своему королю, то ходить нельзя
        }
        //Разметка доступных для хода и/или атаки клеток
        allPlates.forEach { it ->
            if (it.getPlateId() in moveToPlatesId) {
                val check = accessToMove(allPlates, it)
                it.enableToMove(check)
                checks.add(!check)
            }
            if (it.getPlateId() in attackToPlatesId) {
                val check = accessToMove(allPlates, it)
                it.enableToAttack(check)
                checks.add(!check)
            }
        }
        return checks
    }

    fun knightMoves() {
        for (i in intArrayOf(-1, 1)) {
            for (j in intArrayOf(-1, 1)) {
                fun check(row: Int, column: Int) {
                    if (row in 1..8 && column in 1..8) {
                        val newPlate = allPlates[getPlateId(row, column)]
                        when (newPlate.sight) {
                            Sight.WHITE -> if (plate.sight == Sight.BLACK) {
                                attackToPlatesId.add(newPlate.getPlateId())
                            }

                            Sight.BLACK -> if (plate.sight == Sight.WHITE) {
                                attackToPlatesId.add(newPlate.getPlateId())
                            }

                            Sight.NONE -> moveToPlatesId.add(newPlate.getPlateId())
                        }
                    }
                }

                //Вертикальные поля
                val verticalRow = row + 2 * i
                val verticalColumn = column + j
                check(verticalRow, verticalColumn)

                //Горизонтальные поля
                val horizontalRow = row + i
                val horizontalColumn = column + 2 * j
                check(horizontalRow, horizontalColumn)
            }
        }
        _moveOrAttack(moveToPlatesId, attackToPlatesId)
    }

    fun rookMoves() {
        val direction = listOf(
            (row + 1..8),
            (1..row - 1).reversed(),
            (1..column - 1).reversed(),
            (column + 1..8)
        )
        direction.forEachIndexed { index, it ->
            for (i in it) {
                val plateId = when (index) {
                    0, 1 -> getPlateId(i, column)
                    2, 3 -> getPlateId(row, i)
                    else -> -1 //выстрелит ошибку
                }

                val newPlate = allPlates[plateId]

                when (newPlate.sight) {
                    Sight.WHITE -> {
                        if (plate.sight == Sight.BLACK) {
                            attackToPlatesId.add(newPlate.getPlateId())
                        }
                        break
                    }

                    Sight.BLACK -> {
                        if (plate.sight == Sight.WHITE) {
                            attackToPlatesId.add(newPlate.getPlateId())
                        }
                        break
                    }

                    Sight.NONE -> {
                        moveToPlatesId.add(newPlate.getPlateId())
                    }
                }
            }
        }
        _moveOrAttack(moveToPlatesId, attackToPlatesId)
    }

    fun bishopMoves() {
        val borders = 1..8

        for (flag in 1..4) {
            for (i in 1..7) {
                val newPlate = when {
                    flag == 1 && row + i in borders && column + i in borders -> allPlates[getPlateId(
                        row + i,
                        column + i
                    )]

                    flag == 2 && row + i in borders && column - i in borders -> allPlates[getPlateId(
                        row + i,
                        column - i
                    )]

                    flag == 3 && row - i in borders && column + i in borders -> allPlates[getPlateId(
                        row - i,
                        column + i
                    )]

                    flag == 4 && row - i in borders && column - i in borders -> allPlates[getPlateId(
                        row - i,
                        column - i
                    )]

                    else -> break //Выстрелит ошибку
                }

                when (newPlate.sight) {
                    Sight.WHITE -> {
                        if (plate.sight == Sight.BLACK) {
                            attackToPlatesId.add(newPlate.getPlateId())
                        }
                        break
                    }

                    Sight.BLACK -> {
                        if (plate.sight == Sight.WHITE) {
                            attackToPlatesId.add(newPlate.getPlateId())
                        }
                        break
                    }

                    Sight.NONE -> {
                        moveToPlatesId.add(newPlate.getPlateId())
                    }
                }


            }
            _moveOrAttack(moveToPlatesId, attackToPlatesId)
        }
    }

    fun queenMoves() {
        rookMoves()
        bishopMoves()
        _moveOrAttack(moveToPlatesId, attackToPlatesId)
    }

    fun kingMoves() {
        //Стандартные ходы
        for (i in intArrayOf(-1, 0, 1)) {
            for (j in intArrayOf(-1, 0, 1)) {
                if (!(i == 0 && j == 0)) {
                    if (row + i in 1..8 && column + j in 1..8) {
                        val newPlate = allPlates[getPlateId(row + i, column + j)]
                        when (newPlate.sight) {
                            Sight.WHITE -> {
                                if (plate.sight == Sight.BLACK && newPlate.canBeAttackedByWhite == false) {
                                    attackToPlatesId.add(newPlate.getPlateId())
                                }
                            }

                            Sight.BLACK -> {
                                if (plate.sight == Sight.WHITE && newPlate.canBeAttackedByBlack == false) {
                                    attackToPlatesId.add(newPlate.getPlateId())
                                }
                            }

                            Sight.NONE -> {
                                if ((plate.sight == Sight.WHITE && newPlate.canBeAttackedByBlack == false) || (plate.sight == Sight.BLACK && newPlate.canBeAttackedByWhite == false))
                                    moveToPlatesId.add(newPlate.getPlateId())
                            }
                        }
                    }
                }
            }
        }
        val movedBefore = viewModel.getMovedBefore().value!!
        val kingsId = if (plate.sight == Sight.WHITE) 0 else 3

        /**
         * Метод принимает в себя ряд и колонку, для между которой и королём нужно посчитать фигуры. Вернёт `true` если всё чисто,`false` иначе
         */
        fun emptyBetweenRookAndKing(row: Int, column: Int): Boolean {
            var output = false
            when (row) {
                1 -> when (column) {
                    1 -> {
                        val newPlate = allPlates[getPlateId(row, column + 1)]
                        val newPlate2 = allPlates[getPlateId(row, column + 2)]
                        val newPlate3 = allPlates[getPlateId(row, column + 2)]
                        output = (newPlate.sight == Sight.NONE
                                && newPlate2.sight == Sight.NONE && !newPlate2.canBeAttackedByBlack
                                && newPlate3.sight == Sight.NONE && !newPlate3.canBeAttackedByBlack)
                    }

                    8 -> {
                        val newPlate = allPlates[getPlateId(row, column - 1)]
                        val newPlate2 = allPlates[getPlateId(row, column - 2)]
                        output = (newPlate.sight == Sight.NONE && !newPlate.canBeAttackedByBlack
                                && newPlate2.sight == Sight.NONE && !newPlate2.canBeAttackedByBlack)
                    }
                }

                8 -> when (column) {
                    1 -> {
                        val newPlate = allPlates[getPlateId(row, column + 1)]
                        val newPlate2 = allPlates[getPlateId(row, column + 2)]
                        val newPlate3 = allPlates[getPlateId(row, column + 2)]
                        output = (newPlate.sight == Sight.NONE
                                && newPlate2.sight == Sight.NONE && !newPlate2.canBeAttackedByWhite
                                && newPlate3.sight == Sight.NONE && !newPlate3.canBeAttackedByWhite)
                    }

                    8 -> {
                        val newPlate = allPlates[getPlateId(row, column - 1)]
                        val newPlate2 = allPlates[getPlateId(row, column - 2)]
                        output = (newPlate.sight == Sight.NONE && !newPlate.canBeAttackedByWhite
                                && newPlate2.sight == Sight.NONE && !newPlate2.canBeAttackedByWhite)
                    }
                }
            }
            return output
        }
        if (!movedBefore[kingsId] && plate.sight != viewModel.getCheck().value) {
            if (!movedBefore[kingsId + 1] && emptyBetweenRookAndKing(
                    plate.row,
                    1
                )
            ) moveToPlatesId.add(getPlateId(plate.row, column - 2))
            if (!movedBefore[kingsId + 2] && emptyBetweenRookAndKing(
                    plate.row,
                    8
                )
            ) moveToPlatesId.add(getPlateId(plate.row, column + 2))
        }
        _moveOrAttack(moveToPlatesId, attackToPlatesId)
    }

    fun pawnMoves() {
        //Проверка доступных ходов для перемещения
        when (plate.row) {
            2, 7 -> {
                //Проверка на ходибельность
                val nextPlateRow = row + 1 * plate.sight.value
                if (nextPlateRow in 1..8) {
                    if (allPlates[getPlateId(nextPlateRow, column)].sight == Sight.NONE) {
                        moveToPlatesId.add(getPlateId(nextPlateRow, column))
                        val next2PlateRow = row + 2 * plate.sight.value
                        if (next2PlateRow in 1..8) {
                            if (allPlates[getPlateId(next2PlateRow, column)].sight == Sight.NONE) {
                                moveToPlatesId.add(getPlateId(next2PlateRow, column))
                            }
                        }
                    }
                }
                //Проверка на атаки
                for (i in intArrayOf(-1, 1)) {
                    val nextPlateColumn = column + i
                    if (nextPlateColumn in 1..8) {
                        if (allPlates[getPlateId(nextPlateRow, nextPlateColumn)].sight !in listOf(
                                Sight.NONE, plate.sight
                            )
                        ) {
                            attackToPlatesId.add(getPlateId(nextPlateRow, nextPlateColumn))
                        }
                    }
                }
            }

            1, 8 -> null


            else -> {
                //Проверка на ходибельность
                if (allPlates[getPlateId(
                        row + 1 * plate.sight.value,
                        column
                    )].sight == Sight.NONE
                ) {
                    moveToPlatesId.add(getPlateId(row + 1 * plate.sight.value, column))
                }
                //Проверка на атаку
                for (i in intArrayOf(-1, 1)) {
                    if (column + i in 1..8) {
                        if (allPlates[getPlateId( //Стандартные атаки
                                row + 1 * plate.sight.value,
                                column + i
                            )].sight !in listOf(
                                Sight.NONE,
                                plate.sight
                            )
                            || allPlates[getPlateId( // Взятие на проходе
                                row + 1 * plate.sight.value,
                                column + i
                            )].enPassant !in listOf(Sight.NONE, plate.sight)
                        ) {
                            attackToPlatesId.add(
                                getPlateId(
                                    row + 1 * plate.sight.value,
                                    column + i
                                )
                            )
                        }
                    }
                }
            }
        }
        _moveOrAttack(moveToPlatesId, attackToPlatesId)
    }

    /**
     * класс чисто для двух функций снизу. [answer] - походили мы или нет, [check] - привёл ли ход к шаху или нет
     */
    data class MoveOrAttack(val answer: Boolean, val check: Boolean)

    //Перемещение фигуры на доступное поле
    fun moveToPlate(allPlates: List<Plate>, plate: Plate): MoveOrAttack {
        if (plate.enableToMove) {
            val chosenPlate =
                viewModel.getChosenFigure().value!! //Вспоминаем фигуру, которой ходили

            //Данные для сохранения(Они дальше по коду изменятся, а нужны именно такими)
            val cpF = chosenPlate.figure
            val cpS = chosenPlate.sight


            //Если походили ладьёй или короолём - лишаем их возможности к рокировке
            if (chosenPlate.figure == KING || chosenPlate.figure == ROOK) {
                when (chosenPlate.getPlateId()) {
                    0 -> viewModel.getMovedBefore().value!![4] = true
                    4 -> viewModel.getMovedBefore().value!![3] = true
                    7 -> viewModel.getMovedBefore().value!![5] = true
                    56 -> viewModel.getMovedBefore().value!![1] = true
                    60 -> viewModel.getMovedBefore().value!![0] = true
                    63 -> viewModel.getMovedBefore().value!![2] = true
                }
            }


            //Проверяем, не можем ли мы превратить пешку
            if ((row == 8 || row == 1) && chosenPlate.figure == PAWN) {
                //Такой хитрый способ нужен затем, что бы клик за пределами всплывающего окна не вызывал его закрытие
                val q = ChangePawnToAnotherFigureDialog(viewModel, sharedPreferences.getBoolean("reverseBlackPawn", false))
                q.isCancelable = false
                q.show(supportFragmentManager, "RadioButtons")
            }
            //При хождении на 2 поля пешкой, делаем возможным взять её на проходе
            if (abs(row - chosenPlate.row) == 2 && chosenPlate.figure == PAWN) {
                allPlates[plate.getPlateId() + 8 * chosenPlate.sight.value].apply {
                    enPassant = chosenPlate.sight
                    enPassantTurn = actualTurn

                }
            }
            //При хождении на 2 поля королём, передвигаем ладью
            if (abs(column - chosenPlate.column) == 2 && chosenPlate.figure == KING) {
                val oldRookPlate = mutableListOf<Plate>()
                val newRookPlate = mutableListOf<Plate>()
                if (column - chosenPlate.column < 0) {
                    oldRookPlate.add(allPlates[getPlateId(row, chosenPlate.column - 4)])
                    newRookPlate.add(allPlates[getPlateId(row, chosenPlate.column - 1)])
                } else {
                    oldRookPlate.add(allPlates[getPlateId(row, chosenPlate.column + 3)])
                    newRookPlate.add(allPlates[getPlateId(row, chosenPlate.column + 1)])
                }

                newRookPlate[0].apply {
                    figure = oldRookPlate[0].figure
                    sight = oldRookPlate[0].sight
                    update(sharedPreferences)
                }

                oldRookPlate[0].apply {
                    removeFigure()
                }
                //Сохраняем передедвижени ладьи
                saveOldPlate(viewModelForRoom, oldRookPlate[0], newRookPlate[0])
            }

            plate.apply { //передвигаем фигуру
                figure = chosenPlate.figure
                sight = chosenPlate.sight
                update(sharedPreferences)
            }

            chosenPlate.removeFigure() //Убираем фигуру

            allPlates.forEach { //Стираем все иконки ходьбы и атаки
                it.enableToMove(false)
                it.enableToAttack(false)
            }

            val check = attackableInTheory(allPlates, viewModel)

            //Сохраняем запись в бд
            saveMove(
                cpS,
                chosenPlate.getPlateId(),
                cpF,
                plate.getPlateId(),
                viewModelForRoom,
                check,
            )
            //Сохраняем клетку
            saveOldPlate(viewModelForRoom, chosenPlate, plate)
            changePlayersTurn(plate.sight, viewModel) // Отдаём право хода другой стороне
            //Проверяем поля под атакой
            return MoveOrAttack(
                true,
                check
            ) //Выходим из метода checkMoves, нам дальше ничего не нужно
        }
        return MoveOrAttack(false, false)
    }

    //Атака фигурой на доступное поле
    fun attackToPlate(allPlates: List<Plate>, plate: Plate): MoveOrAttack {
        if (plate.enableToAttack) {
            val chosenPlate =
                viewModel.getChosenFigure().value!! //Вспоминаем фигуру, которой ходили

            //Данные для сохранения(Они дальше по коду изменятся, а нужны именно такими)
            val cpF = chosenPlate.figure
            val cpS = chosenPlate.sight

            //Если походили ладьёй или короолём - лишаем их возможности к рокировке
            if (chosenPlate.figure == KING || chosenPlate.figure == ROOK)
                when (chosenPlate.getPlateId()) {
                    0 -> viewModel.getMovedBefore().value!![1] = true
                    4 -> viewModel.getMovedBefore().value!![0] = true
                    7 -> viewModel.getMovedBefore().value!![2] = true
                    56 -> viewModel.getMovedBefore().value!![4] = true
                    60 -> viewModel.getMovedBefore().value!![3] = true
                    63 -> viewModel.getMovedBefore().value!![5] = true
                }
            //Если мы съели ладью - лишаем её возможности к рокировке
            if (plate.figure == ROOK){
                val id = when(plate.getPlateId()){
                    0 -> 4
                    7 -> 5
                    63 -> 2
                    56 -> 1
                    else -> 0
                }
                viewModel.getMovedBefore().value!![id] = true
            }

            //Проверяем, не можем ли мы превратить пешку
            if ((row == 8 || row == 1) && chosenPlate.figure == PAWN) {
                //Такой хитрый способ нужен затем, что бы клик за пределами всплывающего окна не вызывал его закрытие
                val q = ChangePawnToAnotherFigureDialog(viewModel, sharedPreferences.getBoolean("reverseBlackPawn", false))
                q.isCancelable = false
                q.show(supportFragmentManager, "RadioButtons")
            }

            plate.apply {//передвигаем фигуру
                //Удаляем фигуру из множества
                when (plate.sight) {
                    Sight.WHITE -> {
                        with(viewModel.getWhiteFigures()) {
                            value!![plate.figure] = value!![plate.figure]!! - 1
                        }
                    }

                    Sight.BLACK -> {
                        with(viewModel.getBlackFigures()) {
                            value!![plate.figure] = value!![plate.figure]!! - 1
                        }
                    }

                    Sight.NONE -> { //Взятие на проходе, только для пешек
                        if (plate.enPassant != Sight.NONE) {
                            enPassant = Sight.NONE
                            with(allPlates[plate.getPlateId() + 8 * chosenPlate.sight.value]) {
                                removeFigure()
                                with(viewModel.getBlackFigures()) {
                                    value!![PAWN] = value!![PAWN]!! - 1
                                }
                                //Убираем эту пешку из памяти
                                viewModelForRoom.addOldPlate(OldPlate(getPlateId(), figure.value, sight.value))
                            }
                        }
                    }
                }

                //Перемещаем на новое поле
                figure = chosenPlate.figure
                sight = chosenPlate.sight
                update(sharedPreferences)
            }

            chosenPlate.removeFigure() //Убираем фигуру

            allPlates.forEach { //Стираем все иконки атаки
                it.enableToAttack(false)
                it.enableToAttack(false)
            }
            val check = attackableInTheory(allPlates, viewModel)

            //Сохраняем запись в бд
            saveMove(
                cpS,
                chosenPlate.getPlateId(),
                cpF,
                plate.getPlateId(),
                viewModelForRoom,
                check,
            )
            //Сохраняем клетку
            saveOldPlate(viewModelForRoom, chosenPlate, plate)

            changePlayersTurn(plate.sight, viewModel) // Отдаём право хода другой стороне

            //Проверяем условия окончания игры
            if (viewModel.getWhiteFigures().value!![KING] == 0) {
                viewModel.getEndGame().value = Sight.BLACK
            }
            if (viewModel.getBlackFigures().value!![KING] == 0) {
                viewModel.getEndGame().value = Sight.WHITE
            }
            //Проверяем поля под атакой
            return MoveOrAttack(
                true,
                check
            )//Выходим из метода checkMoves, нам дальше ничего не нужно
        }
        return MoveOrAttack(false, false)
    }

    if (moveToPlate(allPlates, plate).answer) {
        _moveOrAttack(moveToPlatesId, attackToPlatesId)
        return
    }
    if (attackToPlate(allPlates, plate).answer) {
        return
    }

    viewModel.getChosenFigure().value = plate //Если хода не произошло, то запоминаем новую фигуру

    allPlates.forEach {
        it.enableToMove(false)
        it.enableToAttack(false)
        if (it.enPassantTurn != null) { //Убираю возможность взятия на проходе
            if (actualTurn - it.enPassantTurn!! > 1) {
                it.enPassant = Sight.NONE
                it.enPassantTurn = null
            }
        }

    }
    //Сами ходы
    if (plate.sight == playersTurn) {
        when (plate.figure) {
            PAWN -> pawnMoves()
            ROOK -> rookMoves()
            KNIGHT -> knightMoves()
            BISHOP -> bishopMoves()
            QUEEN -> queenMoves()
            KING -> kingMoves()
            NONE -> null
        }
    }

}

fun checkmate(
    allPlates: List<Plate>,
    viewModel: MainActivityViewModel,
    playersTurn: Sight,
) {
    val flag = mutableListOf<Boolean>()

    fun pseudoMove(oldPlateId: Int, newPlateId: Int) {
        val newAllPlates = allPlates.copy()
        //Нельзя съесть своего короля
        if (allPlates[newPlateId].figure == KING && allPlates[newPlateId].sight == allPlates[oldPlateId].sight) {
            flag.add(true)
            return
        }

        newAllPlates[newPlateId].apply {
            sight = newAllPlates[oldPlateId].sight
            figure = newAllPlates[oldPlateId].figure
        }

        newAllPlates[oldPlateId].apply {
            sight = Sight.NONE
            figure = NONE
        }
        flag.add(attackableInTheory(newAllPlates, viewModel))
    }

    allPlates.forEachIndexed { ind, plate ->
        val row = plate.row
        val column = plate.column
        if (plate.sight == playersTurn) {
            when (plate.figure) {
                NONE -> null
                PAWN -> {
                    //Проверка доступных ходов для перемещения
                    when (plate.row) {
                        2, 7 -> {
                            //Проверка на ходибельность
                            val nextPlateRow = row + 1 * plate.sight.value
                            if (nextPlateRow in 1..8) {
                                if (allPlates[getPlateId(
                                        nextPlateRow,
                                        column
                                    )].sight == Sight.NONE
                                ) {
                                    pseudoMove(plate.getPlateId(), getPlateId(nextPlateRow, column))

                                    val next2PlateRow = row + 2 * plate.sight.value
                                    if (next2PlateRow in 1..8) {
                                        if (allPlates[getPlateId(
                                                next2PlateRow,
                                                column
                                            )].sight == Sight.NONE
                                        ) {
                                            pseudoMove(
                                                plate.getPlateId(),
                                                getPlateId(next2PlateRow, column)
                                            )
                                        }
                                    }
                                }
                            }
                            //Проверка на атаки
                            for (i in intArrayOf(-1, 1)) {
                                val nextPlateColumn = column + i
                                if (nextPlateColumn in 1..8) {
                                    if (allPlates[getPlateId(
                                            nextPlateRow,
                                            nextPlateColumn
                                        )].sight !in listOf(
                                            Sight.NONE, plate.sight
                                        )
                                    ) {
                                        pseudoMove(
                                            plate.getPlateId(),
                                            getPlateId(nextPlateRow, nextPlateColumn)
                                        )
                                    }
                                }
                            }
                        }

                        1, 8 -> null


                        else -> {
                            //Проверка на ходибельность
                            if (allPlates[getPlateId(
                                    row + 1 * plate.sight.value,
                                    column
                                )].sight == Sight.NONE
                            ) {
                                pseudoMove(
                                    plate.getPlateId(),
                                    getPlateId(row + 1 * plate.sight.value, column)
                                )
                            }
                            //Проверка на атаку
                            for (i in intArrayOf(-1, 1)) {
                                if (column + i in 1..8) {
                                    if (allPlates[getPlateId( //Стандартные атаки
                                            row + 1 * plate.sight.value,
                                            column + i
                                        )].sight !in listOf(
                                            Sight.NONE,
                                            plate.sight
                                        )
                                        || allPlates[getPlateId( // Взятие на проходе
                                            row + 1 * plate.sight.value,
                                            column + i
                                        )].enPassant !in listOf(Sight.NONE, plate.sight)
                                    ) {
                                        pseudoMove(
                                            plate.getPlateId(),
                                            getPlateId(row + 1 * plate.sight.value, column + i)
                                        )
                                    }
                                }
                            }
                        }
                    }

                }

                ROOK -> {
                    val direction = listOf(
                        (row + 1..8),
                        (1..row - 1).reversed(),
                        (1..column - 1).reversed(),
                        (column + 1..8)
                    )
                    direction.forEachIndexed { index, it ->
                        for (i in it) {
                            val plateId = when (index) {
                                0, 1 -> getPlateId(i, column)
                                2, 3 -> getPlateId(row, i)
                                else -> -1 //выстрелит ошибку
                            }

                            val newPlate = allPlates[plateId]

                            when (newPlate.sight) {
                                Sight.WHITE -> {
                                    if (plate.sight == Sight.BLACK) {
                                        pseudoMove(plate.getPlateId(), newPlate.getPlateId())
                                    }
                                    break
                                }

                                Sight.BLACK -> {
                                    if (plate.sight == Sight.WHITE) {
                                        pseudoMove(plate.getPlateId(), newPlate.getPlateId())
                                    }
                                    break
                                }

                                Sight.NONE -> {
                                    pseudoMove(plate.getPlateId(), newPlate.getPlateId())
                                }
                            }
                        }
                    }
                }

                KNIGHT -> {

                    for (i in intArrayOf(-1, 1)) {
                        for (j in intArrayOf(-1, 1)) {
                            fun check(row: Int, column: Int) {
                                if (row in 1..8 && column in 1..8) {
                                    val newPlate = allPlates[getPlateId(row, column)]
                                    when (newPlate.sight) {
                                        Sight.WHITE -> if (plate.sight == Sight.BLACK) {
                                            pseudoMove(plate.getPlateId(), newPlate.getPlateId())
                                        }

                                        Sight.BLACK -> if (plate.sight == Sight.WHITE) {
                                            pseudoMove(plate.getPlateId(), newPlate.getPlateId())
                                        }

                                        Sight.NONE -> pseudoMove(
                                            plate.getPlateId(),
                                            newPlate.getPlateId()
                                        )
                                    }
                                }
                            }

                            //Вертикальные поля
                            val verticalRow = row + 2 * i
                            val verticalColumn = column + j
                            check(verticalRow, verticalColumn)

                            //Горизонтальные поля
                            val horizontalRow = row + i
                            val horizontalColumn = column + 2 * j
                            check(horizontalRow, horizontalColumn)
                        }
                    }
                }

                BISHOP -> {
                    val borders = 1..8

                    for (flag in 1..4) {
                        for (i in 1..7) {
                            val newPlate = when {
                                flag == 1 && row + i in borders && column + i in borders -> allPlates[getPlateId(
                                    row + i,
                                    column + i
                                )]

                                flag == 2 && row + i in borders && column - i in borders -> allPlates[getPlateId(
                                    row + i,
                                    column - i
                                )]

                                flag == 3 && row - i in borders && column + i in borders -> allPlates[getPlateId(
                                    row - i,
                                    column + i
                                )]

                                flag == 4 && row - i in borders && column - i in borders -> allPlates[getPlateId(
                                    row - i,
                                    column - i
                                )]

                                else -> break //Выстрелит ошибку
                            }

                            when (newPlate.sight) {
                                Sight.WHITE -> {
                                    if (plate.sight == Sight.BLACK) {
                                        pseudoMove(plate.getPlateId(), newPlate.getPlateId())
                                    }
                                    break
                                }

                                Sight.BLACK -> {
                                    if (plate.sight == Sight.WHITE) {
                                        pseudoMove(plate.getPlateId(), newPlate.getPlateId())
                                    }
                                    break
                                }

                                Sight.NONE -> {
                                    pseudoMove(plate.getPlateId(), newPlate.getPlateId())
                                }
                            }


                        }
                    }
                }

                QUEEN -> {
                    //Проверка по горизонтали
                    val direction = listOf(
                        (plate.row + 1..8),
                        (1..plate.row - 1).reversed(),
                        (1..plate.column - 1).reversed(),
                        (plate.column + 1..8)
                    )
                    direction.forEachIndexed { index, it ->
                        for (i in it) {
                            val plateId = when (index) {
                                0, 1 -> getPlateId(i, plate.column)
                                2, 3 -> getPlateId(plate.row, i)
                                else -> error("Ладья сломалась") //выстрелит ошибку
                            }

                            val newPlate = allPlates[plateId]

                            when (newPlate.sight) {
                                Sight.WHITE -> {
                                    pseudoMove(plate.getPlateId(), newPlate.getPlateId())
                                    break
                                }

                                Sight.BLACK -> {
                                    pseudoMove(plate.getPlateId(), newPlate.getPlateId())
                                    break
                                }

                                Sight.NONE -> {
                                    pseudoMove(plate.getPlateId(), newPlate.getPlateId())
                                }
                            }
                        }
                    }
                    //Проверка по диагонали
                    val borders = 1..8
                    val row = plate.row
                    val column = plate.column

                    for (flag in 1..4) {
                        for (i in 1..7) {
                            val newPlate = when {
                                flag == 1 && row + i in borders && column + i in borders -> allPlates[getPlateId(
                                    row + i,
                                    column + i
                                )]

                                flag == 2 && row + i in borders && column - i in borders -> allPlates[getPlateId(
                                    row + i,
                                    column - i
                                )]

                                flag == 3 && row - i in borders && column + i in borders -> allPlates[getPlateId(
                                    row - i,
                                    column + i
                                )]

                                flag == 4 && row - i in borders && column - i in borders -> allPlates[getPlateId(
                                    row - i,
                                    column - i
                                )]

                                else -> break //Выстрелит ошибку
                            }

                            when (newPlate.sight) {
                                Sight.WHITE -> {
                                    pseudoMove(plate.getPlateId(), newPlate.getPlateId())
                                    break
                                }

                                Sight.BLACK -> {
                                    pseudoMove(plate.getPlateId(), newPlate.getPlateId())
                                    break
                                }

                                Sight.NONE -> {
                                    pseudoMove(plate.getPlateId(), newPlate.getPlateId())
                                }
                            }


                        }
                    }
                }

                KING -> {
                    for (i in intArrayOf(-1, 0, 1)) {
                        for (j in intArrayOf(-1, 0, 1)) {
                            if (!(i == 0 && j == 0)) {
                                if (row + i in 1..8 && column + j in 1..8) {
                                    val newPlate = allPlates[getPlateId(row + i, column + j)]
                                    when (newPlate.sight) {
                                        Sight.WHITE -> {
                                            if (plate.sight == Sight.BLACK && newPlate.canBeAttackedByWhite == false) {
                                                pseudoMove(
                                                    plate.getPlateId(),
                                                    newPlate.getPlateId()
                                                )
                                            }
                                        }

                                        Sight.BLACK -> {
                                            if (plate.sight == Sight.WHITE && newPlate.canBeAttackedByBlack == false) {
                                                pseudoMove(
                                                    plate.getPlateId(),
                                                    newPlate.getPlateId()
                                                )
                                            }
                                        }

                                        Sight.NONE -> {

                                            if ((plate.sight == Sight.WHITE && newPlate.canBeAttackedByBlack == false) || (plate.sight == Sight.BLACK && newPlate.canBeAttackedByWhite == false))
                                                pseudoMove(
                                                    plate.getPlateId(),
                                                    newPlate.getPlateId()
                                                )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (false !in flag && flag.isNotEmpty()) {
        viewModel.getEndGame().value = when (playersTurn) {
            Sight.WHITE -> Sight.BLACK
            Sight.BLACK -> Sight.WHITE
            Sight.NONE -> null
        }
    }
}

/**
 * Метод возвращает копию списка
 */
fun List<Plate>.copy(): List<Plate> {
    val a = mutableListOf<Plate>()
    this.forEach {
        a.add(it.copy())
    }
    return a
}

/**
 * принимает в себя ряд и колонну, возвращает место в массиве
 */
fun getPlateId(row: Int, column: Int): Int {
    return 8 * (8 - row) + (column - 1)
}

/**
 * Возвращает слой и колону фигуры
 */
fun getPlateRowAndColumn(plateId: Int): List<Int> {
    return listOf(plateId / 8 + 1, plateId % 8 + 1)
}

/**
 * Функция отдаёт право хода другой стороне
 */
fun changePlayersTurn(turn: Sight, viewModel: MainActivityViewModel) {
    viewModel.getPlayersTurn().value = when (turn) {
        Sight.WHITE -> Sight.BLACK
        Sight.BLACK -> Sight.WHITE
        Sight.NONE -> Sight.NONE
    }
    viewModel.getTurns().value = viewModel.getTurns().value!! + 1


}

/**
 * функция размечает клетки, находящиеся под боем, а так же проверяет шахи. Если на доске шах, то возвращает `true`, иначе `false`
 */
fun attackableInTheory(allPlate: List<Plate>, viewModel: MainActivityViewModel): Boolean {
    //Очищаем, что бы проверить всё с нуля
    allPlate.forEach { it ->
        it.enableToAttackedInTheory(Sight.WHITE, false)
        it.enableToAttackedInTheory(Sight.BLACK, false)
    }

    //Проверяем всё с нуля
    allPlate.forEach { plate ->
        val sight = plate.sight
        when (plate.figure) {
            NONE -> null
            PAWN -> {//Проверка на атаки
                for (i in intArrayOf(-1, 1)) {
                    val nextPlateColumn = plate.column + i
                    if (nextPlateColumn in 1..8 && plate.row + 1 * sight.value in 1..8) {
                        allPlate[getPlateId(
                            plate.row + 1 * sight.value,
                            nextPlateColumn
                        )].enableToAttackedInTheory(sight, true)


                    }
                }
            }

            ROOK -> {
                val direction = listOf(
                    (plate.row + 1..8),
                    (1..plate.row - 1).reversed(),
                    (1..plate.column - 1).reversed(),
                    (plate.column + 1..8)
                )
                direction.forEachIndexed { index, it ->
                    for (i in it) {
                        val plateId = when (index) {
                            0, 1 -> getPlateId(i, plate.column)
                            2, 3 -> getPlateId(plate.row, i)
                            else -> error("Нет такого индекса") //выстрелит ошибку
                        }

                        val newPlate = allPlate[plateId]

                        when (newPlate.sight) {
                            Sight.WHITE -> {
                                newPlate.enableToAttackedInTheory(plate.sight, true)
                                break
                            }

                            Sight.BLACK -> {
                                newPlate.enableToAttackedInTheory(plate.sight, true)
                                break
                            }

                            Sight.NONE -> {
                                newPlate.enableToAttackedInTheory(plate.sight, true)
                            }
                        }
                    }
                }
            }

            KNIGHT -> {
                for (i in intArrayOf(-1, 1)) {
                    for (j in intArrayOf(-1, 1)) {
                        fun check(row: Int, column: Int) {
                            if (row in 1..8 && column in 1..8) {
                                val newPlate = allPlate[getPlateId(row, column)]
                                newPlate.enableToAttackedInTheory(plate.sight, true)
                            }
                        }

                        //Вертикальные поля
                        val verticalRow = plate.row + 2 * i
                        val verticalColumn = plate.column + j
                        check(verticalRow, verticalColumn)

                        //Горизонтальные поля
                        val horizontalRow = plate.row + i
                        val horizontalColumn = plate.column + 2 * j
                        check(horizontalRow, horizontalColumn)
                    }
                }
            }

            BISHOP -> {

                val borders = 1..8
                val row = plate.row
                val column = plate.column

                for (flag in 1..4) {
                    for (i in 1..7) {
                        val newPlate = when {
                            flag == 1 && row + i in borders && column + i in borders -> allPlate[getPlateId(
                                row + i,
                                column + i
                            )]

                            flag == 2 && row + i in borders && column - i in borders -> allPlate[getPlateId(
                                row + i,
                                column - i
                            )]

                            flag == 3 && row - i in borders && column + i in borders -> allPlate[getPlateId(
                                row - i,
                                column + i
                            )]

                            flag == 4 && row - i in borders && column - i in borders -> allPlate[getPlateId(
                                row - i,
                                column - i
                            )]

                            else -> break
                        }

                        when (newPlate.sight) {
                            Sight.WHITE -> {
                                newPlate.enableToAttackedInTheory(plate.sight, true)
                                break
                            }

                            Sight.BLACK -> {
                                newPlate.enableToAttackedInTheory(plate.sight, true)
                                break
                            }

                            Sight.NONE -> {
                                newPlate.enableToAttackedInTheory(plate.sight, true)
                            }
                        }


                    }
                }
            }

            QUEEN -> {
                //Проверка по горизонтали
                val direction = listOf(
                    (plate.row + 1..8),
                    (1..plate.row - 1).reversed(),
                    (1..plate.column - 1).reversed(),
                    (plate.column + 1..8)
                )
                direction.forEachIndexed { index, it ->
                    for (i in it) {
                        val plateId = when (index) {
                            0, 1 -> getPlateId(i, plate.column)
                            2, 3 -> getPlateId(plate.row, i)
                            else -> error("Ладья сломалась") //выстрелит ошибку
                        }

                        val newPlate = allPlate[plateId]

                        when (newPlate.sight) {
                            Sight.WHITE -> {
                                newPlate.enableToAttackedInTheory(plate.sight, true)
                                break
                            }

                            Sight.BLACK -> {
                                newPlate.enableToAttackedInTheory(plate.sight, true)
                                break
                            }

                            Sight.NONE -> {
                                newPlate.enableToAttackedInTheory(plate.sight, true)
                            }
                        }
                    }
                }
                //Проверка по диагонали
                val borders = 1..8
                val row = plate.row
                val column = plate.column

                for (flag in 1..4) {
                    for (i in 1..7) {
                        val newPlate = when {
                            flag == 1 && row + i in borders && column + i in borders -> allPlate[getPlateId(
                                row + i,
                                column + i
                            )]

                            flag == 2 && row + i in borders && column - i in borders -> allPlate[getPlateId(
                                row + i,
                                column - i
                            )]

                            flag == 3 && row - i in borders && column + i in borders -> allPlate[getPlateId(
                                row - i,
                                column + i
                            )]

                            flag == 4 && row - i in borders && column - i in borders -> allPlate[getPlateId(
                                row - i,
                                column - i
                            )]

                            else -> break //Выстрелит ошибку
                        }

                        when (newPlate.sight) {
                            Sight.WHITE -> {
                                newPlate.enableToAttackedInTheory(plate.sight, true)
                                break
                            }

                            Sight.BLACK -> {
                                newPlate.enableToAttackedInTheory(plate.sight, true)
                                break
                            }

                            Sight.NONE -> {
                                newPlate.enableToAttackedInTheory(plate.sight, true)
                            }
                        }


                    }
                }
            }

            KING -> {

                for (i in intArrayOf(-1, 0, 1)) {
                    for (j in intArrayOf(-1, 0, 1)) {
                        if (!(i == 0 && j == 0)) {
                            if (plate.row + i in 1..8 && plate.column + j in 1..8) {
                                val newPlate = allPlate[getPlateId(plate.row + i, plate.column + j)]
                                newPlate.enableToAttackedInTheory(plate.sight, true)
                            }
                        }
                    }
                }
            }
        }
    }

    //Смотрим, не под шахом ли король
    allPlate.forEach { it ->
        if (it.figure == KING) {
            when (it.sight) {
                Sight.WHITE -> {
                    if (it.canBeAttackedByBlack && viewModel.getCheck().value != Sight.BLACK) {
                        viewModel.getCheck().value = it.sight
                        return true
                    } else {
                        viewModel.getCheck().value = Sight.NONE
                    }
                }

                Sight.BLACK ->
                    if (it.canBeAttackedByWhite && viewModel.getCheck().value != Sight.WHITE) {
                        viewModel.getCheck().value = it.sight
                        return true
                    } else {
                        viewModel.getCheck().value = Sight.NONE
                    }

                Sight.NONE -> null
            }
        }
    }
    return false
}

/**
 * Функция сохранения хода в базу данных
 */
fun saveMove(
    sight: Sight,
    oldPlateId: Int,
    figure: Figure,
    newPlateId: Int,
    viewModelForRoom: ViewModelForRoom,
    check: Boolean
) {
    val move = Moves(0, sight.value, oldPlateId, figure.value, newPlateId, check)
    viewModelForRoom.addMove(move)
}

/**
 * Метод сохраняет текущее положение фигур в таблицу
 */
fun saveOldPlate(
    viewModelForRoom: ViewModelForRoom,
    oldPlate: Plate,
    newPlate: Plate
) {
    val newPlate2 = OldPlate(newPlate.getPlateId(),newPlate.figure.value,newPlate.sight.value)
    val oldPlate2 = OldPlate(oldPlate.getPlateId(), oldPlate.figure.value, oldPlate.sight.value)
    viewModelForRoom.addOldPlate(oldPlate2)
    viewModelForRoom.addOldPlate(newPlate2)
}

/**
 * У каждой фигуры в перечислении [Figure] есть [value]. На основе этого числа эта функция возвращает объект [Figure]
 */
fun getFigureByValue(value:Int): Figure {
    return when(value){
        1 -> PAWN
        2 -> ROOK
        3->KNIGHT
        4->BISHOP
        5->QUEEN
        6->KING
        else -> NONE
    }
}

/**
 * Считает ценность фигур из мапы и возвращает ответ
 */
fun countFiguresInPawns(map: MutableMap<Figure, Int>):Int{
    var count = 0
    map.forEach { it->
        when (it.key){
            NONE -> null
            PAWN -> count += it.value
            ROOK -> count += it.value * 5
            KNIGHT -> count += it.value * 3
            BISHOP -> count += it.value * 3
            QUEEN -> count += it.value * 10
            KING -> null
        }
    }
    return count
}