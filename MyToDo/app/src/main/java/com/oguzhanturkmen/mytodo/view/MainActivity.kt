package com.oguzhanturkmen.mytodo.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.oguzhanturkmen.mytodo.R
import com.oguzhanturkmen.mytodo.databinding.ActivityMainBinding
import com.oguzhanturkmen.mytodo.view.repository.NoteRepository
import com.oguzhanturkmen.mytodo.view.service.NoteDatabase
import com.oguzhanturkmen.mytodo.view.viewmodel.NoteViewModel
import com.oguzhanturkmen.mytodo.view.viewmodel.NoteViewModelProviderFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navigationController : NavController
    lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentHost) as NavHostFragment
        navigationController = navHostFragment.navController

        NavigationUI.setupActionBarWithNavController(this,navigationController)
        setUpViewModel()
    }
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navigationController,null)
    }
    private fun setUpViewModel() {
        val noteRepository = NoteRepository(
            NoteDatabase(this)
        )

        val viewModelProviderFactory =
            NoteViewModelProviderFactory(
                application, noteRepository
            )

        noteViewModel = ViewModelProvider(
            this,
            viewModelProviderFactory
        )[NoteViewModel::class.java]
    }
}