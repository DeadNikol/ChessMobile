package com.example.mychess.room.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.example.mychess.viewmodel.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * viewModel чисто для работы с базой данных
 */
class ViewModelForRoom(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Moves>>
    val readOldPlates: LiveData<List<OldPlate>>

    private val repository: Repository

    init {
        val dao = DataBase.getDataBase(application).userDao()
        repository = Repository(dao)
        readAllData = repository.readAllData
        readOldPlates = repository.readOldPlates
    }

    fun addMove(move: Moves){
        viewModelScope.launch(Dispatchers.IO){
            repository.addMove(move)
        }
    }

    fun deleteAll(){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteAll()
        }
    }

    //Для запоминания позиции
    fun addOldPlate(oldPlate: OldPlate){
        viewModelScope.launch(Dispatchers.IO){
            repository.addOldPlate(oldPlate)
        }
    }
}