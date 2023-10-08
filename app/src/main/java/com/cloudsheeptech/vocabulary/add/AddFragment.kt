package com.cloudsheeptech.vocabulary.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.cloudsheeptech.vocabulary.R
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.databinding.FragmentAddBinding
import java.io.File

class AddFragment : Fragment() {

    private lateinit var binding : FragmentAddBinding
    private lateinit var viewModel : AddViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add, container, false)
        // TODO: Fix this vocabulary not being passed around
        val vocabFile = File(requireActivity().applicationContext.filesDir, "vocabulary.json")
        val viewModelFactory = AddViewModelFactory(vocabulary = Vocabulary(vocabFile))
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory)[AddViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.toast.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }

}