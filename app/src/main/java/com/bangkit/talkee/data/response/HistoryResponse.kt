package com.bangkit.talkee.data.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("data")
	val data: List<HistoryItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class HistoryItem(

	@field:SerializedName("poin")
	val poin: Double? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("idgame")
	val idgame: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("poinuser")
	val poinuser: Double? = null,

	@field:SerializedName("idusergame")
	val idusergame: String? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null,

	@field:SerializedName("tips")
	val tips: String? = null,

	@field:SerializedName("pertanyaan")
	val pertanyaan: String? = null

) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readValue(Double::class.java.classLoader) as? Double,
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readValue(Double::class.java.classLoader) as? Double,
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString()
	) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeValue(poin)
		parcel.writeString(image)
		parcel.writeString(idgame)
		parcel.writeString(nama)
		parcel.writeValue(poinuser)
		parcel.writeString(idusergame)
		parcel.writeString(tanggal)
		parcel.writeString(tips)
		parcel.writeString(pertanyaan)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<HistoryItem> {
		override fun createFromParcel(parcel: Parcel): HistoryItem {
			return HistoryItem(parcel)
		}

		override fun newArray(size: Int): Array<HistoryItem?> {
			return arrayOfNulls(size)
		}
	}
}