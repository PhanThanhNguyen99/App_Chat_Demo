package com.example.chatapp.user

import android.net.Uri
import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        val id :String?=null,
    val name :String?= null,
    val image : String ?=null,
        val status : String ?=null
):Parcelable{}
