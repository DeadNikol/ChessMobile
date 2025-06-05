package com.example.mychess.room.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


/**
 * База данных
 */
@Database(entities = [Moves::class, OldPlate::class], version = 1, exportSchema = false) //В базе данных всего одна табличка
abstract class DataBase: RoomDatabase() {

    abstract fun userDao(): Dao //Возвращаем класс с обращениями к базе данных

    //Говорим, что база данных у нас будет лишь одна
    companion object{
        @Volatile
        private var INSTANCE: DataBase? = null

        fun getDataBase(context: Context): DataBase{ //Получаем одну и ту же базу данных, а не создаём новые всякий раз, как та понадобится
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataBase::class.java,
                    "moves_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}