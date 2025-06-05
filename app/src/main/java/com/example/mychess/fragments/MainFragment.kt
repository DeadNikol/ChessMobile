package com.example.mychess.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mychess.R
import com.example.mychess.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentMainBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding.btnToGame.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_chessFragment)
        }
        binding.btnToSaves.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_savesFragment)
        }

        return binding.root
    }

}