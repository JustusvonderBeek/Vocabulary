package com.cloudsheeptech.vocabulary.recap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.cloudsheeptech.vocabulary.R
import com.cloudsheeptech.vocabulary.databinding.FragmentRecapBinding
import com.cloudsheeptech.vocabulary.databinding.FragmentRecapStartBinding

class RecapStartFragment : Fragment() {

    private lateinit var binding : FragmentRecapStartBinding
    private lateinit var viewModel : RecapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recap, container, false)


        return binding.root
    }

}