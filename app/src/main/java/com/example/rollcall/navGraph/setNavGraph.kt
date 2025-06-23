
package com.example.rollcall.navGraph

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
import com.example.rollcall.authentication.AuthViewModel
import com.example.rollcall.authentication.LoginPage
import com.example.rollcall.authentication.SignUpPage
import com.example.rollcall.classroom.ClassroomPage
import com.example.rollcall.monthview.MonthViewPage
import com.example.rollcall.authentication.AuthCheckPage
import com.example.rollcall.degreedetail.DegreeDetailPage
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
            MonthViewPage()
        }
        composable(                                                     // new added
            route = Routes.degreeDetailPage.routes,                     // new added
            arguments = listOf(                                         // new added
                navArgument("degree")  { type = NavType.StringType },   // new added
                navArgument("year")    { type = NavType.StringType },   // new added
                navArgument("section") { type = NavType.StringType },
                navArgument("cid")     { type = NavType.StringType } // new added
            )                                                           // new added
        ) { backStackEntry ->                                           // new added
            val degree  = backStackEntry.arguments?.getString("degree") ?: ""   // new added
            val year    = backStackEntry.arguments?.getString("year") ?: ""     // new added
            val section = backStackEntry.arguments?.getString("section") ?: ""
            val cid      = backStackEntry.arguments?.getString("cid") ?: ""
            DegreeDetailPage(navController, degree, year, section,cid)
        }
    }
}
