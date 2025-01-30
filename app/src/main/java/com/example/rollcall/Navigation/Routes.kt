package com.example.rollcall.Navigation

sealed class Routes(val routes: String) {

    //Classroom
    object Degree : Routes("Degree_screen")

    //Month view
    object MonthView : Routes("Month_view")


}