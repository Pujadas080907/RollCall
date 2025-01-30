package com.example.rollcall.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rollcall.Classroom.DegreeScreen
import com.example.rollcall.MothView.MonthViewScreen


@Composable
fun SetNavGraph(navController: NavHostController){

    NavHost(navController = navController, startDestination = Routes.Degree.routes) {


        composable(Routes.Degree.routes){
            DegreeScreen(navController = navController)
        }
        composable(Routes.MonthView.routes){
            MonthViewScreen(navController = navController)
        }
    }
}