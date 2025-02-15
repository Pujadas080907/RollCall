package com.example.rollcall.Navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rollcall.Classroom.DegreeDetailScreen
import com.example.rollcall.Classroom.DegreeScreen
import com.example.rollcall.Database.DegreeViewModel
import com.example.rollcall.Database.DegreeViewModelFactory
import com.example.rollcall.Mothview.MonthViewScreen
import com.example.rollcall.studentlists.StudentViewModel
import com.example.rollcall.studentlists.StudentViewModelFactory


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

        composable("degreeDetail/{degreeId}/{degreeName}/{degreeYear}/{degreeSection}") { backStackEntry ->
            val degreeId = backStackEntry.arguments?.getString("degreeId")?.toInt() ?: 0
            val degreeName = backStackEntry.arguments?.getString("degreeName") ?: ""
            val degreeYear = backStackEntry.arguments?.getString("degreeYear")?.toIntOrNull() ?: 1
            val degreeSection = backStackEntry.arguments?.getString("degreeSection") ?: "A"
            val studentViewModel: StudentViewModel = viewModel(
                factory = StudentViewModelFactory(application = LocalContext.current.applicationContext as Application)
            )
            DegreeDetailScreen(navController = navController,
                degreeId = degreeId,
                degreeName = degreeName,
                degreeYear = degreeYear,
                degreeSection = degreeSection,
                studentViewModel = studentViewModel)
        }
    }
}