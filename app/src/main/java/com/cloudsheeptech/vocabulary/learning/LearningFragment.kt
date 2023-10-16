package com.cloudsheeptech.vocabulary.learning

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.cloudsheeptech.vocabulary.R
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.databinding.FragmentLearningBinding
import java.io.File

class LearningFragment : Fragment(), MenuProvider {

    private lateinit var viewModel: LearningViewModel
    private lateinit var binding : FragmentLearningBinding

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.learning_dropdown, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.dd_edit_btn -> {
                viewModel.editWord()
                return true
            }
            R.id.dd_delete_btn -> {
                viewModel.removeWord()
                return true
            }
        }
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_learning, container, false)

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val vocabFile = File(requireActivity().applicationContext.filesDir, "vocabulary.json")
        val viewModelFactory = LearningViewModelFactory(Vocabulary.getInstance(vocabFile))
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[LearningViewModel::class.java]
        binding.learningVM = viewModel
        binding.lifecycleOwner = this

        viewModel.navigateToEdit.observe(viewLifecycleOwner, Observer { selectedId ->
            if (selectedId > 0) {
                findNavController().navigate(LearningFragmentDirections.actionLearningToEditFragment(selectedId))
                viewModel.navigatedToEditWord()
            }
        })

        return binding.root
    }
}