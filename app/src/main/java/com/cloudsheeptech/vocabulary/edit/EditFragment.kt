package com.cloudsheeptech.vocabulary.edit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cloudsheeptech.vocabulary.R
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.databinding.FragmentEditBinding
import com.cloudsheeptech.vocabulary.learning.LearningViewModel
import java.io.File

class EditFragment : Fragment(), MenuProvider {

    private lateinit var binding : FragmentEditBinding
    private lateinit var viewModel : EditViewModel
    private val learningViewModel : LearningViewModel by activityViewModels()

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_frm_dropdown, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.addVocabularyBtn -> {
                learningViewModel.navigateToAddWord()
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit, container, false)

        // Adding the dropdown menu in the toolbar
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // TODO: Fix this vocabulary not being passed around
        val vocabFile = File(requireActivity().applicationContext.filesDir, "vocabulary.json")
        val viewModelFactory = EditViewModelFactory(vocabulary = Vocabulary(vocabFile))

        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[EditViewModel::class.java]
        binding.viewModel = viewModel
        binding.learnViewModel = learningViewModel
        binding.lifecycleOwner = this

        val adapter = WordListItemAdapter(WordListItemAdapter.WordListItemListener { wordId ->
            Log.i("EditFragment", "Tapped on word with ID $wordId")
            // TODO: Implement handling
        })

        binding.vocabList.adapter = adapter

        viewModel.vocabList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })

        binding.refreshLayout.setOnRefreshListener {
            Log.i("EditFragment", "On refresh called")
            viewModel.updateVocabulary()
        }

        viewModel.refreshing.observe(viewLifecycleOwner, Observer {
            if (!it) {
                Log.i("EditFragment", "Refreshing finished")
                binding.refreshLayout.isRefreshing = false
            }
        })

        learningViewModel.navigateToWord.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(EditFragmentDirections.actionEditToAdd())
                learningViewModel.onAddWordNavigated()
            }
        })

        return binding.root
    }
}