package com.pujadas.rollcall.navGraph

sealed class Routes(val routes: String) {

    //authentication
    object SignUp : Routes("signup")
    object Login : Routes("login")
    object authchek : Routes("authcheck")

    //classroom
    object classroompage : Routes("classroompage")

//    Month view
    object monthViewPage : Routes("monthviewpage")

    object monthDetailedViewPage : Routes(
        "monthdetail/{degree}/{year}/{section}/{cid}"
    )

    //degree detail page
    object degreeDetailPage: Routes(
        "degdetail/{degree}/{year}/{section}/{cid}"
    )

    object reportAndPercentagePage : Routes(
        "reportandpercentage/{degree}/{year}/{section}/{cid}/{monthName}/{yearNum}"
    )

    object attendanceReportPage : Routes(
        "attendancereport/{degree}/{year}/{section}/{cid}/{monthName}/{yearNum}"
    )
    object percentagePage : Routes(
        "percentagePage/{degree}/{year}/{section}/{cid}/{monthName}/{yearNum}"
    )
    object editReportPage : Routes(
        "editreportpage/{degree}/{year}/{section}/{cid}/{date}"
    )
    //full report page
    object fullReportPage : Routes("fullreportpage")


}