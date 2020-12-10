package com.hlag.routine

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Routine(var name: String, var steps: @RawValue ArrayList<Step>) : Parcelable {
    override fun toString(): String {
        return name
    }

    fun getNext(): Step?{
        steps.forEach{ step ->
            if (!step.checked){
                return step
            }
        }

        return null
    }
}