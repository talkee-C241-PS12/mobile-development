package com.bangkit.talkee.data.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LeaderboardResponse(

    @field:SerializedName("listUser")
    val listUser: List<ListUserItem> = emptyList(),
)

data class ListUserItem(

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("rank")
    val rank: Int? = null,

    @field:SerializedName("score")
    val score: Int? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeInt(rank ?: 0)
        parcel.writeInt(score ?: 0)
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
