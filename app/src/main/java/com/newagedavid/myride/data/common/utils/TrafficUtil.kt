package com.newagedavid.myride.data.common.utils

import java.util.Calendar

fun simulateTrafficLevel(): Double {
    return listOf(1.0, 1.2, 1.5).random()
}

fun generateRandomMinutesAway(): Int{
    return (5..20).random()}

fun isCurrentTimePeakHour(): Boolean {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return hour in 7..9 || hour in 17..19
}
