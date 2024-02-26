package com.haker.test_mvvm_hilt_room_retrofit.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Source(
    val id: String? = "",
    val name: String? = ""
): Parcelable
