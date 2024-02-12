package com.example.note.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.note.R

class MainPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_page, container, false)

        val button: Button = view.findViewById(R.id.button)
        button.setOnClickListener {
            // A gombra kattintáskor hajtsd végre az átirányítást a következő fragmentre
            findNavController().navigate(R.id.action_mainPageFragment_to_homeFragment)
        }

        return view
    }

}