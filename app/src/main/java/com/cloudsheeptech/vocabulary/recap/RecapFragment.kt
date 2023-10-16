package com.cloudsheeptech.vocabulary.recap

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
import com.cloudsheeptech.vocabulary.addedit.AddViewModel
import com.cloudsheeptech.vocabulary.addedit.AddViewModelFactory
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.databinding.FragmentAddBinding
import com.cloudsheeptech.vocabulary.databinding.FragmentRecapBinding
import java.io.File

class RecapFragment : Fragment() {

    private lateinit var binding : FragmentRecapBinding
    private lateinit var viewModel : RecapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recap, container, false)

        val vocabFile = File(requireActivity().applicationContext.filesDir, "vocabulary.json")
        val vocabulary = Vocabulary.getInstance(vocabFile)
        val viewModelFactory = RecapViewModelFactory(vocabulary)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[RecapViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                RecapResult.CORRECT -> {
                    binding.correctResultText.visibility = View.VISIBLE
                    binding.incorrectResultText.visibility = View.GONE
                    binding.hintText.visibility = View.GONE
                }
                RecapResult.INCORRECT -> {
                    binding.correctResultText.visibility = View.GONE
                    binding.incorrectResultText.visibility = View.VISIBLE
                    binding.hintText.visibility = View.VISIBLE
                }
                RecapResult.NONE -> {
                    binding.correctResultText.visibility = View.GONE
                    binding.incorrectResultText.visibility = View.GONE
                    binding.hintText.visibility = View.GONE
                }
                null -> {
                    Log.e("RecapFragment", "Given result is null.")
                    binding.correctResultText.visibility = View.GONE
                    binding.incorrectResultText.visibility = View.GONE
                    binding.hintText.visibility = View.GONE
                }
            }
        })



        return binding.root
    }
}