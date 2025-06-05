package com.example.mychess.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mychess.databinding.FragmentSavesBinding
import com.example.mychess.fragments.recyclerView.RecyclerViewAdapter
import com.example.mychess.room.data.ViewModelForRoom

class SavesFragment : Fragment() {

    private lateinit var binding: FragmentSavesBinding
    private lateinit var viewModelForRoom: ViewModelForRoom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSavesBinding.inflate(layoutInflater)
        viewModelForRoom = ViewModelProvider(this)[ViewModelForRoom::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val adapter = RecyclerViewAdapter()
        binding.rvSaves.adapter = adapter
        binding.rvSaves.layoutManager = GridLayoutManager(requireContext(), 2)

        //Когда данные обновляются, обновляется и список
        viewModelForRoom.readAllData.observe(viewLifecycleOwner) { it->
             adapter.setData(it)
        }
        return binding.root
    }

}