package com.hlag.routine

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Step (var duration: Int, var text: String) : Parcelable {
}