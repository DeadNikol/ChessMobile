package com.example.mychess.alertdialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mychess.MainActivity
import com.example.mychess.R
import com.example.mychess.databinding.DeleteMeBinding
import com.example.mychess.figures.Figure
import com.example.mychess.viewmodel.MainActivityViewModel

class ChangePawnToAnotherFigureDialog(val viewModel: MainActivityViewModel, val rotateBlackFigures: Boolean) : DialogFragment() {
    var choice = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DeleteMeBinding.inflate(layoutInflater)
        with(binding) {
            if (viewModel.getTurns().value!! % 2 == 0) { // Если белые довели пешку, показываем белые фигуры
                imbtnQueen.setBackgroundResource(R.drawable.white_queen)
                imbtnQueen.setOnClickListener {
                    viewModel.getChangePawnTo().value = Figure.QUEEN
                    dismiss()
                }
                imbtnBishop.setBackgroundResource(R.drawable.white_bishop)
                imbtnBishop.setOnClickListener {
                    viewModel.getChangePawnTo().value = Figure.BISHOP
                    dismiss()
                }
                imbtnRook.setBackgroundResource(R.drawable.white_rook)
                imbtnRook.setOnClickListener {
                    viewModel.getChangePawnTo().value = Figure.ROOK
                    dismiss()
                }
                imbtnKnight.setBackgroundResource(R.drawable.white_knight)
                imbtnKnight.setOnClickListener {
                    viewModel.getChangePawnTo().value = Figure.KNIGHT
                    dismiss()
                }
            }
            else{ //Если чёрные довели пешку, показываем чёрные фигуры
                if(rotateBlackFigures){
//                    imbtnQueen.rotationX = 180f
//                    imbtnBishop.rotationX = 180f
//                    imbtnKnight.rotationX = 180f
//                    imbtnRook.rotationX = 180f
                    binding.root.rotationX = 180f
                }
                imbtnQueen.setBackgroundResource(R.drawable.black_queen)
                imbtnQueen.setOnClickListener {
                    viewModel.getChangePawnTo().value = Figure.QUEEN
                    dismiss()
                }
                imbtnBishop.setBackgroundResource(R.drawable.black_bishop)
                imbtnBishop.setOnClickListener {
                    viewModel.getChangePawnTo().value = Figure.BISHOP
                    dismiss()
                }
                imbtnRook.setBackgroundResource(R.drawable.black_rook)
                imbtnRook.setOnClickListener {
                    viewModel.getChangePawnTo().value = Figure.ROOK
                    dismiss()
                }
                imbtnKnight.setBackgroundResource(R.drawable.black_knight)
                imbtnKnight.setOnClickListener {
                    viewModel.getChangePawnTo().value = Figure.KNIGHT
                    dismiss()
                }
            }

        }
        return binding.root
    }
}

