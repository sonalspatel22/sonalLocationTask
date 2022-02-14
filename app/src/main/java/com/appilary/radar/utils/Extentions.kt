package com.appilary.radar.utils

import android.location.Location

fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}

fun Float?.toDisplay(): String {
    return if (this != null) {
        "(Total Distance = ${toString()})"
    } else {
        "Total Distance = 0.0"
    }
}
