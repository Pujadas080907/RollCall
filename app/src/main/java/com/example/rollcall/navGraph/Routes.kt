package com.example.rollcall.navGraph

sealed class Routes(val routes: String) {

    //authentication
    object SignUp : Routes("signup")
    object Login : Routes("login")
    object authchek : Routes("authcheck")

    //classroom
    object classroompage : Routes("classroompage")

//    Month view
    object monthViewPage : Routes("monthviewpage")
}