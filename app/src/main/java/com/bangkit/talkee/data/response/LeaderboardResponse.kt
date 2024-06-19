package com.bangkit.talkee.data.response

import com.google.gson.annotations.SerializedName

data class LeaderboardResponse(

	@field:SerializedName("data")
	val data: List<LeaderboardItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class LeaderboardItem(

	@field:SerializedName("poin")
	val poin: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("leaderboard")
	val leaderboard: Int? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("email")
	val email: String? = null
)
