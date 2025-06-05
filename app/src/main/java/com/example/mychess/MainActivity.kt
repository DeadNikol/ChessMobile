package com.example.mychess

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.mychess.databinding.ActivityMainBinding
import com.example.mychess.databinding.FragmentChessBinding
import com.example.mychess.fragments.chessFragment
import com.example.mychess.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var binding: ActivityMainBinding
    private lateinit var host: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]

        //Нахождение навигационного контроллера в активности представленно двумя строками
        host =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = host.navController


        val appBarConfiguration =
            AppBarConfiguration(navController.graph) //Что бы получать названия фрагментов и выводить их в toolbar'е
        setSupportActionBar(binding.toolbar) //Троеточие на тулбаре
        binding.toolbar.setupWithNavController(
            navController,
            appBarConfiguration
        ) //Связываем тулбар и граф навигации
    }

//

}