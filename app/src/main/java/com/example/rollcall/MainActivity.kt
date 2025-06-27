package com.example.rollcall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rollcall.authentication.AuthViewModel
import com.example.rollcall.navGraph.SetNavGraph
import com.example.rollcall.ui.theme.RollCallTheme
import com.google.firebase.FirebaseApp
import kotlin.getValue


class MainActivity : ComponentActivity() {
//    private lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

//        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setContent {
            RollCallTheme {
                val authViewModel: AuthViewModel = viewModel(
                    factory = AndroidViewModelFactory.getInstance(application)
                )
                SetNavGraph(authViewModel)
            }
        }
    }
}

