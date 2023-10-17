package com.cloudsheeptech.vocabulary.editlist

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
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.cloudsheeptech.vocabulary.R
import com.cloudsheeptech.vocabulary.data.SwipeToDeleteHandler
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.databinding.FragmentEditlistBinding
import com.cloudsheeptech.vocabulary.learning.LearningViewModel
import java.io.File

class EditlistFragment : Fragment(), MenuProvider {

    private lateinit var binding : FragmentEditlistBinding
    private lateinit var viewModel : EditlistViewModel
    private val learningViewModel : LearningViewModel by activityViewModels()

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.edit_frm_dropdown, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.addVocabularyBtn -> {
                viewModel.navigateToAddWord()
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_editlist, container, false)

        // Adding the dropdown menu in the toolbar
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // TODO: Fix this vocabulary not being passed around
        val vocabFile = File(requireActivity().applicationContext.filesDir, "vocabulary.json")
        val vocabulary = Vocabulary.getInstance(vocabFile)
        val viewModelFactory = EditlistViewModelFactory(vocabulary = Vocabulary.getInstance(vocabFile))

        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[EditlistViewModel::class.java]
        binding.viewModel = viewModel
        binding.learnViewModel = learningViewModel
        binding.lifecycleOwner = this


        val adapter = WordListItemAdapter(vocabulary, WordListItemAdapter.WordListItemListener { wordId ->
            Log.i("EditFragment", "Tapped on word with ID $wordId")
            viewModel.editWord(wordId)
        })
        binding.vocabList.adapter = adapter
        // Allow removing item with swipe
        val deleteHelper = ItemTouchHelper(SwipeToDeleteHandler(adapter))
        deleteHelper.attachToRecyclerView(binding.vocabList)

        viewModel.vocabulary.liveWordList.observe(viewLifecycleOwner, Observer {
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

        viewModel.navigateToAdd.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(EditlistFragmentDirections.actionEditToAdd())
                viewModel.onAddWordNavigated()
            }
        })

        viewModel.navigateToEdit.observe(viewLifecycleOwner, Observer {selected ->
            if (selected > -1) {
                findNavController().navigate(EditlistFragmentDirections.actionEditToEditFragment(selected))
                viewModel.onEditWordNavigated()
            }
        })

        return binding.root
    }
}