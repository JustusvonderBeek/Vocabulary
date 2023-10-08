package com.cloudsheeptech.vocabulary.learning

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.cloudsheeptech.vocabulary.R
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.databinding.FragmentLearningBinding
import java.io.File

class LearningFragment : Fragment() {

    private lateinit var viewModel: LearningViewModel
    private lateinit var binding : FragmentLearningBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_learning, container, false)
        val vocabFile = File(requireActivity().applicationContext.filesDir, "vocabulary.json")
        val viewModelFactory = LearningViewModelFactory(Vocabulary(vocabFile))
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[LearningViewModel::class.java]
        binding.learningVM = viewModel
        binding.lifecycleOwner = this

        viewModel.editToggle.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {edit ->
                edit.let {
                    Log.i("LearningFragment", "Observer changing to focusable")
                    binding.vocabView.focusable = View.FOCUSABLE
                    // For whatever reason this API is boolean now?
                    binding.vocabView.isFocusableInTouchMode = true
                    binding.translateView.focusable = View.FOCUSABLE
                    binding.translateView.isFocusableInTouchMode = true
                    binding.editButton.text = "Update word"

                    // Show keyboard
                    val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(binding.vocabView, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        })

        viewModel.editedToggle.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {edited ->
                edited.let {
                    Log.i("LearningFragment", "Observer changing to no focus")
                    binding.vocabView.focusable = View.NOT_FOCUSABLE
                    binding.vocabView.isFocusableInTouchMode = false
                    binding.translateView.focusable = View.NOT_FOCUSABLE
                    binding.translateView.isFocusableInTouchMode = false
                    binding.editButton.text = "Start editing word"
                    viewModel.wordEdited()
                }
            }
        })

        return binding.root
    }

}