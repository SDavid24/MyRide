package com.newagedavid.myride.presentation.navigation

fun showBottomBar(route: String?): Boolean {
    return route in listOf("home", "history")
}
