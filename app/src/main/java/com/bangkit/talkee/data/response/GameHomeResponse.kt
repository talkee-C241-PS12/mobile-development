package com.bangkit.talkee.data.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GameHomeResponse(

    @field:SerializedName("listGame")
    val listGame: List<ListGameItem> = emptyList(),
)

data class ListGameItem(

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("description")
    val description: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(photoUrl)
        parcel.writeString(title)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ListGameItem> {
        override fun createFromParcel(parcel: Parcel): ListGameItem {
            return ListGameItem(parcel)
        }

        override fun newArray(size: Int): Array<ListGameItem?> {
            return arrayOfNulls(size)
        }
    }
}
