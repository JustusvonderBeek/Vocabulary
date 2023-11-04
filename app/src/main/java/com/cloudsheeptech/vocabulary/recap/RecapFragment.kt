package com.cloudsheeptech.vocabulary.recap

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cloudsheeptech.vocabulary.R
import com.cloudsheeptech.vocabulary.addedit.AddViewModelFactory
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.databinding.FragmentAddBinding
import com.cloudsheeptech.vocabulary.databinding.FragmentRecapBinding
import java.io.File

class RecapFragment : Fragment() {

    private lateinit var binding : FragmentRecapBinding
    private val viewModel : RecapViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recap, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.result.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                RecapResult.CORRECT -> {
                    binding.resultText.visibility = View.VISIBLE
                    binding.hintText.visibility = View.GONE
                    binding.countAsCorrectButton.visibility = View.GONE
                }
                RecapResult.COUNT_AS_CORRECT, RecapResult.INCORRECT -> {
                    binding.resultText.visibility = View.VISIBLE
                    binding.hintText.visibility = View.VISIBLE
                    binding.countAsCorrectButton.visibility = View.GONE
                }
                RecapResult.NONE -> {
                    binding.resultText.visibility = View.GONE
                    binding.hintText.visibility = View.GONE
                    binding.countAsCorrectButton.visibility = View.GONE
                }
                null -> {
                    Log.e("RecapFragment", "Given result is null.")
                    binding.resultText.visibility = View.GONE
                    binding.hintText.visibility = View.GONE
                    binding.countAsCorrectButton.visibility = View.GONE
                }
            }
        })

        viewModel.resultText.observe(viewLifecycleOwner, Observer { text ->
            if (text != null) {
                binding.resultText.text = text
            }
        })

        viewModel.forward.observe(viewLifecycleOwner, Observer { forward ->
            val content = forward.getContentIfNotHandled()
            content?.let {
                if (it)
                    binding.checkButton.text = getText(R.string.btn_next_word)
                else
                    binding.checkButton.text = getText(R.string.btn_check_recap)
            }
        })

        viewModel.navigateToRecapStart.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigateUp()
                viewModel.onStartNavigated()
            }
        })

        return binding.root
    }
}