package com.pujadas.rollcall



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pujadas.rollcall.authentication.AuthViewModel
import com.pujadas.rollcall.navGraph.SetNavGraph
import com.pujadas.rollcall.ui.theme.RollCallTheme
import com.google.firebase.FirebaseApp



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)

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

