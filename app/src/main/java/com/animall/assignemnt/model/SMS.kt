package com.animall.assignemnt.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class SMS(val id: String, val date: Date, val body: String,
               val address: String) : Parcelable