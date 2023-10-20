package com.cloudsheeptech.vocabulary

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cloudsheeptech.vocabulary.data.Vocabulary
import com.cloudsheeptech.vocabulary.databinding.ActivityMainBinding
import com.cloudsheeptech.vocabulary.learning.LearningViewModel
import com.cloudsheeptech.vocabulary.learning.LearningViewModelFactory
import com.cloudsheeptech.vocabulary.recap.RecapViewModel
import com.cloudsheeptech.vocabulary.recap.RecapViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    // Create the vocabulary here and pass the data around the app so that every fragment share the same data
//    private lateinit var learningViewModel : LearningViewModel
    private lateinit var recapViewModel : RecapViewModel

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
        val vocabulary = Vocabulary.getInstance(vocabularyFile)
//        val activityViewModel by viewModels<LearningViewModel> { LearningViewModelFactory(vocabulary) }
//        learningViewModel = activityViewModel
        val actViewModel by viewModels<RecapViewModel> { RecapViewModelFactory(vocabulary) }
        recapViewModel = actViewModel
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.navHostFragment)
        return navController.navigateUp()
    }

}