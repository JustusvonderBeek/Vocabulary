package com.cloudsheeptech.vocabulary.recap

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cloudsheeptech.vocabulary.R
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.databinding.FragmentRecapBinding
import com.cloudsheeptech.vocabulary.databinding.FragmentRecapStartBinding
import java.io.File

class RecapStartFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding : FragmentRecapStartBinding
    private val viewModel : RecapViewModel by activityViewModels()

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.i("RecapFragment", "Selection was ${p0!!.selectedItemPosition}")
        viewModel.setupDirection(RecapDirection.fromInt(p0!!.selectedItemPosition))
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.i("RecapFragment", "Nothing selected. Handling default case")
        viewModel.setupDirection(RecapDirection.BOTH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recap_start, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val spinner = binding.languageSelectionSpinner
        ArrayAdapter.createFromResource(requireContext(), R.array.language_direction, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this

        viewModel.navigateToRecap.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(RecapStartFragmentDirections.actionRecapStartFragmentToRecap())
                viewModel.onRecapNavigated()
            }
        })

        return binding.root
    }

}