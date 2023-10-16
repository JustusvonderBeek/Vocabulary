package com.cloudsheeptech.vocabulary

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.databinding.ActivityMainBinding
import com.cloudsheeptech.vocabulary.learning.LearningViewModel
import com.cloudsheeptech.vocabulary.learning.LearningViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    // Create the vocabulary here and pass the data around the app so that every fragment share the same data
    private lateinit var learningViewModel : LearningViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val botNav : BottomNavigationView = binding.bottomNavigation
        val navController = findNavController(R.id.navHostFragment)
        val appBarConfig = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfig)
//        NavigationUI.setupActionBarWithNavController(this, navController)
        botNav.setupWithNavController(navController)

        val vocabularyFile = File(applicationContext.filesDir, "vocabulary.json")
        val vocabulary = Vocabulary(vocabularyFile)
        val activityViewModel by viewModels<LearningViewModel> { LearningViewModelFactory(vocabulary) }
        learningViewModel = activityViewModel
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.navHostFragment)
        return navController.navigateUp()
    }

}