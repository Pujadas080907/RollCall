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

    //degree detail page
    object degreeDetailPage: Routes(
        "degdetail/{degree}/{year}/{section}/{cid}"
    )

    //full report page
    object fullReportPage : Routes("fullreportpage")
}