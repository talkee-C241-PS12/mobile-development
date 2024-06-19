package com.bangkit.talkee.data.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class KelasResponse(

	@field:SerializedName("data")
	val data: List<KelasItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class KelasItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("namakelas")
	val namakelas: String? = null,

	@field:SerializedName("idkelas")
	val idkelas: String? = null

) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(image)
		parcel.writeString(namakelas)
		parcel.writeString(idkelas)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<KelasItem> {
		override fun createFromParcel(parcel: Parcel): KelasItem {
			return KelasItem(parcel)
		}

		override fun newArray(size: Int): Array<KelasItem?> {
			return arrayOfNulls(size)
		}
	}
}
