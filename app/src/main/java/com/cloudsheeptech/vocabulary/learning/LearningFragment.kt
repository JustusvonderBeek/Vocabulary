package com.cloudsheeptech.vocabulary.learning

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.cloudsheeptech.vocabulary.R
import com.cloudsheeptech.vocabulary.databinding.FragmentLearningBinding

class LearningFragment : Fragment() {

    private lateinit var viewModel: LearningViewModel
    private lateinit var binding : FragmentLearningBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_learning, container, false)
        val viewModelFactory = LearningViewModelFactory()
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[LearningViewModel::class.java]
        binding.learningVM = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

}