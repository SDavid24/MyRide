package com.newagedavid.myride.data.common.model

data class RideConfirmation(
    val status: String,
    val driver: Driver,
    val estimatedArrival: String
)

