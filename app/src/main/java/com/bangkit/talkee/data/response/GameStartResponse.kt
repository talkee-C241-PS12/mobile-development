package com.bangkit.talkee.data.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class GameStartResponse(

	@field:SerializedName("poin")
	val poin: Double? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("idgame")
	val idgame: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("idusergame")
	val idusergame: String? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null,

	@field:SerializedName("tips")
	val tips: String? = null,

	@field:SerializedName("pertanyaan")
	val pertanyaan: List<PertanyaanItem>,

	@field:SerializedName("status")
	val status: String? = null
)

data class PertanyaanItem(

	@field:SerializedName("jawaban1")
	val jawaban1: String? = null,

	@field:SerializedName("jawaban2")
	val jawaban2: String? = null,

	@field:SerializedName("jawaban3")
	val jawaban3: String? = null,

	@field:SerializedName("jawaban")
	val jawaban: String? = null,

	@field:SerializedName("tipe")
	val tipe: Int,

	@field:SerializedName("idpertanyaan")
	val idpertanyaan: String? = null,

) : Parcelable {
	constructor(parcel: Parcel) : this(
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readString(),
		parcel.readInt(),
		parcel.readString()
	)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(jawaban1)
		parcel.writeString(jawaban2)
		parcel.writeString(jawaban3)
		parcel.writeString(jawaban)
		parcel.writeInt(tipe)
		parcel.writeString(idpertanyaan)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<PertanyaanItem> {
		override fun createFromParcel(parcel: Parcel): PertanyaanItem {
			return PertanyaanItem(parcel)
		}

		override fun newArray(size: Int): Array<PertanyaanItem?> {
			return arrayOfNulls(size)
		}
	}
}
