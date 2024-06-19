package com.bangkit.talkee.data.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GameResponse(

	@field:SerializedName("data")
	val data: List<GameItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class GameItem(

	@field:SerializedName("poin")
	val poin: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("idgame")
	val idgame: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null

) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readValue(Int::class.java.classLoader) as? Int,
		parcel.readString(),
		parcel.readString(),
		parcel.readString()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeValue(poin)
		parcel.writeString(image)
		parcel.writeString(idgame)
		parcel.writeString(nama)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<GameItem> {
		override fun createFromParcel(parcel: Parcel): GameItem {
			return GameItem(parcel)
		}

		override fun newArray(size: Int): Array<GameItem?> {
			return arrayOfNulls(size)
		}
	}
}
