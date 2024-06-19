package com.bangkit.talkee.data.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class KelasDetailResponse(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("data")
	val data: List<KelasDetailItem?>? = null,

	@field:SerializedName("namakelas")
	val namakelas: String? = null,

	@field:SerializedName("idkelas")
	val idkelas: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class KelasDetailItem(

	@field:SerializedName("kata")
	val kata: String? = null,

	@field:SerializedName("video")
	val video: String? = null

) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(kata)
		parcel.writeString(video)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<KelasDetailItem> {
		override fun createFromParcel(parcel: Parcel): KelasDetailItem {
			return KelasDetailItem(parcel)
		}

		override fun newArray(size: Int): Array<KelasDetailItem?> {
			return arrayOfNulls(size)
		}
	}

}
