package com.example.koratime.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationModel(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val address: String? = null,
) : Parcelable