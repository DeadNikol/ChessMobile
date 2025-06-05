package com.example.mychess.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mychess.figures.Figure
import com.example.mychess.figures.Plate
import com.example.mychess.figures.Sight

class MainActivityViewModel: ViewModel() {
    private val choosenFigure = MutableLiveData<Plate>() //Фигура, которой мы хотим совершить ход
    private val whiteFigures = MutableLiveData<MutableMap<Figure, Int>>() //Множество белых фигур
    private val blackFigures = MutableLiveData<MutableMap<Figure, Int>>() //Множество чёрных фигур
    private val changePawnTo = MutableLiveData<Figure>() //Фигура, на которую меняют пешку
    private val playersTurn = MutableLiveData<Sight>(Sight.WHITE) //Сторона с правом хода
    private val endGame = MutableLiveData<Sight>() //Условия конца игры
    private val check = MutableLiveData<Sight>() //Под шахом ли король
    private val movedBefore = MutableLiveData<MutableList<Boolean>>(mutableListOf(false, false, false, false, false, false))
    private val allPlates = MutableLiveData<MutableList<Plate>>(mutableListOf<Plate>()) //Сохранение позиций всех фигур
    private var differenceInPawns = MutableLiveData<Int>(0) //разница ценности фигур


    private val turns = MutableLiveData<Int>(-1) //Каждое новое передвижение фигур проходит в свой ход (Это не шахматный ход)

    fun getChosenFigure(): MutableLiveData<Plate> = choosenFigure
    fun getChangePawnTo(): MutableLiveData<Figure> = changePawnTo
    fun getPlayersTurn(): MutableLiveData<Sight> = playersTurn
    fun getWhiteFigures() : MutableLiveData<MutableMap<Figure, Int>> = whiteFigures
    fun getBlackFigures() : MutableLiveData<MutableMap<Figure, Int>> = blackFigures
    fun getEndGame(): MutableLiveData<Sight> = endGame
    fun getCheck(): MutableLiveData<Sight> = check
    /**
     * Порядок: Белые - `король`, `Ra1`, `Ra8`; Чёрные - `король`, `Rh1`, `Rh8`
     */
    fun getMovedBefore(): MutableLiveData<MutableList<Boolean>> = movedBefore
    fun getAllPlates(): MutableLiveData<MutableList<Plate>> = allPlates
    fun getDifferenceInPawns(): MutableLiveData<Int> = differenceInPawns

    fun getTurns(): MutableLiveData<Int> = turns
}