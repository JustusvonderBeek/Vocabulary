package com.cloudsheeptech.vocabulary.learning

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.cloudsheeptech.vocabulary.R
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.databinding.FragmentLearningBinding

class LearningFragment : Fragment() {

    private lateinit var viewModel: LearningViewModel
    private lateinit var binding : FragmentLearningBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_learning, container, false)
        val viewModelFactory = LearningViewModelFactory(Vocabulary())
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[LearningViewModel::class.java]
        binding.learningVM = viewModel
        binding.lifecycleOwner = this

        viewModel.editToggle.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {edit ->
                edit.let {
                    Log.i("LearningFragment", "Observer changing focusability")
                    binding.vocabView.inputType = InputType.TYPE_CLASS_TEXT
                    binding.vocabView.focusable = View.FOCUSABLE
                    binding.translateView.inputType = InputType.TYPE_CLASS_TEXT
                    binding.translateView.focusable = View.FOCUSABLE
                    binding.editButton.text = "Update word"
                }
            }
        })

        viewModel.editedToggle.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {edited ->
                edited.let {
                    Log.i("LearningFragment", "Observer changing focusability")
                    binding.vocabView.inputType = InputType.TYPE_NULL
                    binding.vocabView.focusable = View.NOT_FOCUSABLE
                    binding.translateView.inputType = InputType.TYPE_NULL
                    binding.translateView.focusable = View.NOT_FOCUSABLE
                    binding.editButton.text = "Start editing word"
                    viewModel.wordEdited()
                }
            }
        })

        return binding.root
    }

}