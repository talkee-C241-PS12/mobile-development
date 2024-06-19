package com.bangkit.talkee.data.response

import com.google.gson.annotations.SerializedName

data class GameDetailResponse(

	@field:SerializedName("poin")
	val poin: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("idgame")
	val idgame: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("tips")
	val tips: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
