package com.hlag.routine

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Routine(var id : Long, var steps: @RawValue ArrayList<Step>, var name: String) : Parcelable {
}