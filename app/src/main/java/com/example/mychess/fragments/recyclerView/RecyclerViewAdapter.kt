package com.example.mychess.fragments.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mychess.R
import com.example.mychess.figures.Figure
import com.example.mychess.room.data.Moves

/**
 * Класс адаптер для recyclerView
 */
class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    private var moveList = emptyList<Moves>() //Список ходов

    fun setData(move: List<Moves>) { //Задаём список ходов
        this.moveList = move
        notifyDataSetChanged()
    }

    //Связываем разметку
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        //Возвращаем разметку
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_element, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //Заполняем разметку
        val currentItem = moveList[position]
        val figure = when (currentItem.figure) {
            1 -> ""
            2 -> "R"
            3 -> "N"
            4 -> "B"
            5 -> "Q"
            6 -> "K"
            else -> ""
        }
        val positionFrom = when (currentItem.fromPlateId % 8 + 1) {
            1 -> "a"
            2 -> "b"
            3 -> "c"
            4 -> "d"
            5 -> "e"
            6 -> "f"
            7 -> "g"
            8 -> "h"
            else -> ""
        } + (8 - currentItem.fromPlateId / 8 ).toString()

        val positionTo = when (currentItem.toPlateId % 8 + 1) {
            1 -> "a"
            2 -> "b"
            3 -> "c"
            4 -> "d"
            5 -> "e"
            6 -> "f"
            7 -> "g"
            8 -> "h"
            else -> ""
        } + (8 - currentItem.toPlateId / 8 ).toString()

        val check = when(currentItem.check){
            true -> "+"
            false -> ""
        }

        val turn = when(currentItem.sight){
            1 -> (position /2 +1).toString() +"..."
            else -> ""
        }

        holder.tvTitle.text = "$turn $figure$positionFrom -> $positionTo$check"
    }

    //Возвращаем её размеры
    override fun getItemCount(): Int {
        return moveList.size
    }
}