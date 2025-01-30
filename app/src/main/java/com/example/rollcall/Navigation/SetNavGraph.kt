package com.example.rollcall.Navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rollcall.Classroom.DegreeScreen
import com.example.rollcall.Database.DegreeViewModel
import com.example.rollcall.Database.DegreeViewModelFactory
import com.example.rollcall.MothView.MonthViewScreen


@Composable
fun SetNavGraph(navController: NavHostController){

    NavHost(navController = navController, startDestination = Routes.Degree.routes) {


        composable(Routes.Degree.routes){
            val degreeViewModel: DegreeViewModel = viewModel(
                factory = DegreeViewModelFactory(application = LocalContext.current.applicationContext as Application)
            )
            DegreeScreen(navController = navController,degreeViewModel)
        }
        composable(Routes.MonthView.routes){
            MonthViewScreen(navController = navController)
        }
    }
}