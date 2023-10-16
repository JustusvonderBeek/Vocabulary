package com.cloudsheeptech.vocabulary.edit

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cloudsheeptech.vocabulary.R
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.databinding.FragmentEditBinding
import java.io.File

class EditFragment : Fragment() {

    private lateinit var binding : FragmentEditBinding
    private lateinit var viewModel : EditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit, container, false)
        // TODO: Fix this vocabulary not being passed around
        val vocabFile = File(requireActivity().applicationContext.filesDir, "vocabulary.json")
        val viewModelFactory = EditViewModelFactory(vocabulary = Vocabulary(vocabFile))
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[EditViewModel::class.java]
        binding.viewModel = viewModel
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

        return binding.root
    }

}