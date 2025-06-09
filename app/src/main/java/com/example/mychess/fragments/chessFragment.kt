package com.example.mychess.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.sqlite.SQLITE_DATA_INTEGER
import com.example.mychess.MainActivity
import com.example.mychess.R
import com.example.mychess.alertdialog.ChangePawnToAnotherFigureDialog
import com.example.mychess.alertdialog.ConfirmationToCreateNewGame
import com.example.mychess.databinding.FragmentChessBinding
import com.example.mychess.figures.Figure
import com.example.mychess.figures.Plate
import com.example.mychess.figures.Sight
import com.example.mychess.figures.attackableInTheory
import com.example.mychess.figures.checkMoves
import com.example.mychess.figures.checkmate
import com.example.mychess.figures.countFiguresInPawns
import com.example.mychess.figures.getFigureByValue
import com.example.mychess.fragments.recyclerView.RecyclerViewAdapter
import com.example.mychess.room.data.OldPlate
import com.example.mychess.room.data.ViewModelForRoom
import com.example.mychess.viewmodel.MainActivityViewModel

class chessFragment : Fragment() {
    private lateinit var binding: FragmentChessBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var viewModelForRoom: ViewModelForRoom
    private lateinit var check: MutableLiveData<Boolean>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var allPlates: MutableList<Plate>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentChessBinding.inflate(layoutInflater)
        //Объекты viewModel
        val viewModelProvider = ViewModelProvider(this)
        viewModel = viewModelProvider[MainActivityViewModel::class.java]
        viewModelForRoom = viewModelProvider[ViewModelForRoom::class.java]
        check = MutableLiveData()
        sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true) //задаём меню тулбара(троеточие справа вверху) внутри этой активности

        //Пихаем все поля в одну переменную
        allPlates = mutableListOf<Plate>()
        with(binding) {
            val listOfRows = listOf(row1, row2, row3, row4, row5, row6, row7, row8)
            listOfRows.forEachIndexed { index, it ->
                with(it) {
                    allPlates.addAll(
                        arrayOf(
                            Plate(it.place1, it.fl1, 8 - index, 1, Figure.NONE, Sight.NONE),
                            Plate(it.place2, it.fl2, 8 - index, 2, Figure.NONE, Sight.NONE),
                            Plate(it.place3, it.fl3, 8 - index, 3, Figure.NONE, Sight.NONE),
                            Plate(it.place4, it.fl4, 8 - index, 4, Figure.NONE, Sight.NONE),
                            Plate(it.place5, it.fl5, 8 - index, 5, Figure.NONE, Sight.NONE),
                            Plate(it.place6, it.fl6, 8 - index, 6, Figure.NONE, Sight.NONE),
                            Plate(it.place7, it.fl7, 8 - index, 7, Figure.NONE, Sight.NONE),
                            Plate(it.place8, it.fl8, 8 - index, 8, Figure.NONE, Sight.NONE),
                        )
                    )
                }
            }
        }

        //Вспоминаем предыдущую партию
        viewModelForRoom.readOldPlates.observe(viewLifecycleOwner) { it ->
            if (it.size == 64) {
                //Вспомогательные мапы
                val white = mutableMapOf<Figure, Int>(
                    Figure.PAWN to 0, Figure.ROOK to 0, Figure.BISHOP to 0,
                    Figure.KNIGHT to 0, Figure.QUEEN to 0, Figure.KING to 0
                )
                val black = mutableMapOf<Figure, Int>(
                    Figure.PAWN to 0, Figure.ROOK to 0, Figure.BISHOP to 0,
                    Figure.KNIGHT to 0, Figure.QUEEN to 0, Figure.KING to 0
                )

                allPlates.forEachIndexed { index, itPlate ->
                    itPlate.apply {
                        sight = when (it[index].sight) {
                            -1 -> Sight.BLACK; 1 -> Sight.WHITE
                            else -> Sight.NONE
                        }
                        figure = getFigureByValue(it[index].figure)
                        update(sharedPreferences)
                    }

                    //Статистика фигур каждой стороны
                    when (itPlate.sight) {
                        Sight.WHITE -> {
                            when (itPlate.figure) {
                                Figure.NONE -> null
                                Figure.PAWN -> white[Figure.PAWN] = white[Figure.PAWN]!! + 1
                                Figure.ROOK -> white[Figure.ROOK] = white[Figure.ROOK]!! + 1
                                Figure.KNIGHT -> white[Figure.KNIGHT] = white[Figure.KNIGHT]!! + 1
                                Figure.BISHOP -> white[Figure.BISHOP] = white[Figure.BISHOP]!! + 1
                                Figure.QUEEN -> white[Figure.QUEEN] = white[Figure.QUEEN]!! + 1
                                Figure.KING -> white[Figure.KING] = white[Figure.KING]!! + 1
                            }

                        }

                        Sight.BLACK -> {
                            when (itPlate.figure) {
                                Figure.NONE -> null
                                Figure.PAWN -> black[Figure.PAWN] = black[Figure.PAWN]!! + 1
                                Figure.ROOK -> black[Figure.ROOK] = black[Figure.ROOK]!! + 1
                                Figure.KNIGHT -> black[Figure.KNIGHT] = black[Figure.KNIGHT]!! + 1
                                Figure.BISHOP -> black[Figure.BISHOP] = black[Figure.BISHOP]!! + 1
                                Figure.QUEEN -> black[Figure.QUEEN] = black[Figure.QUEEN]!! + 1
                                Figure.KING -> black[Figure.KING] = black[Figure.KING]!! + 1
                            }
                        }

                        Sight.NONE -> null
                    }
                    viewModel.getWhiteFigures().value = white
                    viewModel.getBlackFigures().value = black
                    viewModel.getDifferenceInPawns().value =
                        countFiguresInPawns(white) - countFiguresInPawns(black)
                }
                //На случай, если на доске уже мат
                checkmate(allPlates, viewModel, Sight.WHITE)
                checkmate(allPlates, viewModel, Sight.BLACK)
            }
        }

        //RecyclerView
        val adapter = RecyclerViewAdapter()
        binding.rvMain.adapter = adapter
        binding.rvMain.layoutManager = GridLayoutManager(requireContext(), 2)
        viewModelForRoom.readAllData.observe(viewLifecycleOwner) { it ->
            adapter.setData(it)
            //Задаём право хода при создании окна, если в партии уже совершены ходы
            viewModel.getPlayersTurn().value = when (it.size % 2) {
                0 -> Sight.WHITE
                else -> Sight.BLACK
            }
        }

        //Выводит разницу ценности съеденых фигур
        viewModel.getDifferenceInPawns().observe(viewLifecycleOwner) { it ->
            binding.tvDifferenceInPawns.text = it.toString()
        }

        //При шахе делаем экран красным
        check.observe(viewLifecycleOwner) { it ->
            if (!it) {
                binding.root.background = null
            } else
                binding.root.background =
                    ContextCompat.getDrawable(binding.root.context, R.drawable.king_under_attack)
        }


        //Разукрашиваем поле, задаём всем полям поведение, расставляем фигуры
        allPlates.forEachIndexed { itIndex, it ->
            it.button.background =
                resources.getDrawable(R.drawable.emptyness) //Что бы кнопок не было видно

            //В этих файлах можно задать цвет клеткам на поле
            if (it.row % 2 == 0 && it.column % 2 == 0) it.frame.background =
                resources.getDrawable(R.drawable.square_black)
            if (it.row % 2 == 0 && it.column % 2 == 1) it.frame.background =
                resources.getDrawable(R.drawable.square_white)
            if (it.row % 2 == 1 && it.column % 2 == 0) it.frame.background =
                resources.getDrawable(R.drawable.square_white)
            if (it.row % 2 == 1 && it.column % 2 == 1) it.frame.background =
                resources.getDrawable(R.drawable.square_black)


            //Задаём поведение при нажатии
            it.button.setOnClickListener { itbtn ->
                checkMoves(
                    it,
                    allPlates,
                    viewModel.getPlayersTurn().value!!,
                    viewModel,
                    parentFragmentManager,
                    viewModel.getTurns().value!!,
                    viewModelForRoom,
                    sharedPreferences,
                )
            }
        }


        //Если пешка добралась до края доски, то её нужно поменять на выбранную фигуру
        viewModel.getChangePawnTo().observe(viewLifecycleOwner) {
            allPlates.forEach { it ->
                if ((it.row == 8 || it.row == 1) && it.figure == Figure.PAWN) {
                    it.figure = viewModel.getChangePawnTo().value!!
                    it.update(sharedPreferences)
                    //Сохраняем новую фигуру(что бы та не считалась пешкой)
                    viewModelForRoom.addOldPlate(
                        OldPlate(
                            it.getPlateId(),
                            it.figure.value,
                            it.sight.value
                        )
                    )
                }
            }

        }
        //Если один из королей пал, право хода забирается у обоих сторон
        viewModel.getEndGame().observe(viewLifecycleOwner) {

            Toast.makeText(
                requireContext(),
                "${viewModel.getEndGame().value!!.name} wins!!",
                Toast.LENGTH_SHORT
            ).show()

            viewModel.getPlayersTurn().value = Sight.NONE
        }
        //Показываем очерёдность хода
        viewModel.getPlayersTurn().observe(viewLifecycleOwner) { it ->
            binding.tvPlayersTurn.text = it.name
            binding.tvPlayersTurn.background = when (it) {
                Sight.WHITE -> ContextCompat.getDrawable(
                    binding.tvPlayersTurn.context,
                    R.drawable.rounded_shape_white
                )

                Sight.BLACK -> ContextCompat.getDrawable(
                    binding.tvPlayersTurn.context,
                    R.drawable.rounded_shape_black
                )

                Sight.NONE -> null
            }
            binding.tvPlayersTurn.setTextColor(
                when (it) {
                    Sight.WHITE -> ContextCompat.getColor(
                        binding.tvPlayersTurn.context,
                        R.color.black
                    )

                    Sight.BLACK -> ContextCompat.getColor(
                        binding.tvPlayersTurn.context,
                        R.color.white
                    )

                    Sight.NONE -> ContextCompat.getColor(
                        binding.tvPlayersTurn.context,
                        R.color.black
                    )
                }
            )
        }

        //проверяем на наличие мата
        viewModel.getTurns().observe(viewLifecycleOwner) {
            //Каждый ход следим за разницей в фигурах
            with(viewModel.getDifferenceInPawns()) {
                if (viewModel.getWhiteFigures().isInitialized && viewModel.getBlackFigures().isInitialized)
                    value =
                        countFiguresInPawns(viewModel.getWhiteFigures().value!!) - countFiguresInPawns(
                            viewModel.getBlackFigures().value!!
                        )
            }
            checkmate(allPlates, viewModel, viewModel.getPlayersTurn().value!!)
            viewModel.getAllPlates().value = allPlates//Сохраняем позицию

            check.value = attackableInTheory(allPlates, viewModel)
        }

        //Расстваляем фигуры для начала партии
        binding.btnToNewGame.setOnClickListener {

            ConfirmationToCreateNewGame(viewModel, viewModelForRoom, allPlates, sharedPreferences).show(parentFragmentManager, "confirmation")


        }


        return binding.root
    }

    //Эта функция связывает элементы из xml с менюшкой в фрагменте
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        menu.findItem(R.id.reverseBlackFigures).isChecked = sharedPreferences.getBoolean("reverseBlackPawn", false)
    }

    //А эта функция определяет поведение
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.reverseBlackFigures -> {
                item.isChecked = !item.isChecked // Для запоминания
                val editor = sharedPreferences.edit() // создаём объект редактора
                editor.putBoolean(
                    "reverseBlackPawn",
                    item.isChecked
                ) //Помещаем туда значение из этой менюшки
                editor.apply() //Загружаем в память
                allPlates.forEach { it ->
                    it.update(sharedPreferences)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
