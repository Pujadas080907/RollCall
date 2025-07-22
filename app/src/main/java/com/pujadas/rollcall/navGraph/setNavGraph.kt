
package com.pujadas.rollcall.navGraph

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pujadas.rollcall.Reports.EditReportPage
import com.pujadas.rollcall.Reports.FullReportPage
import com.pujadas.rollcall.authentication.AuthViewModel
import com.pujadas.rollcall.authentication.LoginPage
import com.pujadas.rollcall.authentication.SignUpPage
import com.pujadas.rollcall.classroom.ClassroomPage
import com.pujadas.rollcall.monthview.MonthViewPage
import com.pujadas.rollcall.authentication.AuthCheckPage
import com.pujadas.rollcall.degreedetail.DegreeDetailPage
import com.pujadas.rollcall.firebasedatabase.ClassroomData
import com.pujadas.rollcall.monthview.MonthDetailedViewPage
import com.pujadas.rollcall.monthview.PercentagePage
import com.pujadas.rollcall.monthview.ReportAndPercentagePage
import com.pujadas.rollcall.monthview.attendancereport.AttendanceReportPage
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SetNavGraph(authViewModel: AuthViewModel) {
    val navController: NavHostController = rememberNavController()
    val duration = 700

    // 🚀 Always start from AuthCheckPage to decide destination dynamically
    val startDestination = "authcheck"

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        // 👇 Auth Check Page handles redirect based on authState
        composable(Routes.authchek.routes) {
            AuthCheckPage(navController, authViewModel)
        }

        composable(
            route = Routes.Login.routes,
            enterTransition = { fadeIn(tween(duration)) + scaleIn(tween(duration), initialScale = 0.8f) },
            exitTransition = { fadeOut(tween(duration)) + scaleOut(tween(duration), targetScale = 1.2f) },
            popEnterTransition = { fadeIn(tween(duration)) + scaleIn(tween(duration), initialScale = 1.2f) },
            popExitTransition = { fadeOut(tween(duration)) + scaleOut(tween(duration), targetScale = 0.8f) },
        ) {
            LoginPage(navController, authViewModel)
        }

        composable(
            route = Routes.SignUp.routes,
            enterTransition = { fadeIn(tween(duration)) + scaleIn(tween(duration), initialScale = 0.8f) },
            exitTransition = { fadeOut(tween(duration)) + scaleOut(tween(duration), targetScale = 1.2f) },
            popEnterTransition = { fadeIn(tween(duration)) + scaleIn(tween(duration), initialScale = 1.2f) },
            popExitTransition = { fadeOut(tween(duration)) + scaleOut(tween(duration), targetScale = 0.8f) },
        ) {
            SignUpPage(navController, authViewModel)
        }

        composable(route = Routes.classroompage.routes) {
            ClassroomPage(navController, authViewModel)
        }

        composable(route = Routes.monthViewPage.routes) {
            MonthViewPage(navController)
        }
        composable(
            route = Routes.degreeDetailPage.routes,
            arguments = listOf(
                navArgument("degree")  { type = NavType.StringType },
                navArgument("year")    { type = NavType.StringType },
                navArgument("section") { type = NavType.StringType },
                navArgument("cid")     { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val degree  = backStackEntry.arguments?.getString("degree") ?: ""
            val year    = backStackEntry.arguments?.getString("year") ?: ""
            val section = backStackEntry.arguments?.getString("section") ?: ""
            val cid      = backStackEntry.arguments?.getString("cid") ?: ""
            DegreeDetailPage(navController, degree, year, section,cid)
        }
        composable(
            route = "fullreportpage/{degree}/{year}/{section}/{cid}/{selectedDate}",
            arguments = listOf(
                navArgument("degree") { type = NavType.StringType },
                navArgument("year") { type = NavType.StringType },
                navArgument("section") { type = NavType.StringType },
                navArgument("cid") { type = NavType.StringType },
                navArgument("selectedDate") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val degree = backStackEntry.arguments?.getString("degree") ?: ""
            val year = backStackEntry.arguments?.getString("year") ?: ""
            val section = backStackEntry.arguments?.getString("section") ?: ""
            val cid = backStackEntry.arguments?.getString("cid") ?: ""
            val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""

            val classroom = ClassroomData(
                id = cid,
                degree = degree,
                year = year,
                section = section
            )

            FullReportPage(
                navController = navController,
                classroom = classroom,
                initialDate = selectedDate
            )
        }
        composable(
            route = Routes.monthDetailedViewPage.routes,
            arguments = listOf(
                navArgument("degree") { type = NavType.StringType },
                navArgument("year") { type = NavType.StringType },
                navArgument("section") { type = NavType.StringType },
                navArgument("cid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val degree = backStackEntry.arguments?.getString("degree") ?: ""
            val year = backStackEntry.arguments?.getString("year") ?: ""
            val section = backStackEntry.arguments?.getString("section") ?: ""
            val cid = backStackEntry.arguments?.getString("cid") ?: ""

            val classroom = ClassroomData(
                id = cid,
                degree = degree,
                year = year,
                section = section
            )

            MonthDetailedViewPage(navController, classroom)
        }
        composable(
            route = Routes.reportAndPercentagePage.routes,
            arguments = listOf(
                navArgument("degree") { type = NavType.StringType },
                navArgument("year") { type = NavType.StringType },
                navArgument("section") { type = NavType.StringType },
                navArgument("cid") { type = NavType.StringType },
                navArgument("monthName") { type = NavType.StringType },
                navArgument("yearNum") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val degree = backStackEntry.arguments?.getString("degree") ?: ""
            val year = backStackEntry.arguments?.getString("year") ?: ""
            val section = backStackEntry.arguments?.getString("section") ?: ""
            val cid = backStackEntry.arguments?.getString("cid") ?: ""
            val monthName = backStackEntry.arguments?.getString("monthName") ?: ""
            val yearNum = backStackEntry.arguments?.getInt("yearNum") ?: 2025

            val classroom = ClassroomData(
                id = cid,
                degree = degree,
                year = year,
                section = section
            )

            ReportAndPercentagePage(
                navController = navController,
                classroom = classroom,
                monthName = monthName,
                year = yearNum
            )
        }
        composable(
            route = Routes.attendanceReportPage.routes,
            arguments = listOf(
                navArgument("degree") { type = NavType.StringType },
                navArgument("year") { type = NavType.StringType },
                navArgument("section") { type = NavType.StringType },
                navArgument("cid") { type = NavType.StringType },
                navArgument("monthName") { type = NavType.StringType },
                navArgument("yearNum") { type = NavType.IntType }
            )
        ) {
            val degree = it.arguments?.getString("degree") ?: ""
            val year = it.arguments?.getString("year") ?: ""
            val section = it.arguments?.getString("section") ?: ""
            val cid = it.arguments?.getString("cid") ?: ""
            val monthName = it.arguments?.getString("monthName") ?: ""
            val yearNum = it.arguments?.getInt("yearNum") ?: 2025

            val classroom = ClassroomData(degree, year, section, cid)
            AttendanceReportPage(navController, classroom, monthName, yearNum)
        }

        composable(
            Routes.percentagePage.routes,
            arguments = listOf(
                navArgument("degree") { type = NavType.StringType },
                navArgument("year") { type = NavType.StringType },
                navArgument("section") { type = NavType.StringType },
                navArgument("cid") { type = NavType.StringType },
                navArgument("monthName") { type = NavType.StringType },
                navArgument("yearNum") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val classroom = ClassroomData(
                degree = backStackEntry.arguments?.getString("degree") ?: "",
                year = backStackEntry.arguments?.getString("year") ?: "",
                section = backStackEntry.arguments?.getString("section") ?: "",
                id = backStackEntry.arguments?.getString("cid") ?: "",
                userEmail = "" // if needed
            )
            val month = backStackEntry.arguments?.getString("monthName") ?: ""
            val yearNum = backStackEntry.arguments?.getString("yearNum")?.toIntOrNull() ?: 0

            PercentagePage(navController, classroom, month, yearNum)
        }

        composable(
            Routes.editReportPage.routes,
            arguments = listOf(
                navArgument("degree") { type = NavType.StringType },
                navArgument("year") { type = NavType.StringType },
                navArgument("section") { type = NavType.StringType },
                navArgument("cid") { type = NavType.StringType }, // ✅ FIXED
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val degree = backStackEntry.arguments?.getString("degree") ?: ""
            val year = backStackEntry.arguments?.getString("year") ?: ""
            val section = backStackEntry.arguments?.getString("section") ?: ""
            val classroomId = backStackEntry.arguments?.getString("cid") ?: "" // ✅ FIXED
            val date = backStackEntry.arguments?.getString("date") ?: ""

            val classroom = ClassroomData(degree, year, section, classroomId)

            EditReportPage(navController, classroom, date)
        }






    }

}
