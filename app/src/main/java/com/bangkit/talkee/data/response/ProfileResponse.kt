package com.bangkit.talkee.data.response

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

	@field:SerializedName("poin")
	val poin: Double? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("leaderboard")
	val leaderboard: Int? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("riwayat")
	val riwayat: List<RiwayatItem?>? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class RiwayatItem(

	@field:SerializedName("poin")
	val poin: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("idgame")
	val idgame: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("poinuser")
	val poinuser: Any? = null,

	@field:SerializedName("idusergame")
	val idusergame: String? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null,

	@field:SerializedName("tips")
	val tips: String? = null,

	@field:SerializedName("pertanyaan")
	val pertanyaan: Any? = null
)
