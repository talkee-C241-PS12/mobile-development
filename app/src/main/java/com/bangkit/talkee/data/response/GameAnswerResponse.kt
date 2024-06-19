package com.bangkit.talkee.data.response

import com.google.gson.annotations.SerializedName

data class GameAnswerResponse(

	@field:SerializedName("process")
	val process: Boolean? = null,

	@field:SerializedName("hasil")
	val hasil: Boolean? = null,

	@field:SerializedName("status")
	val status: String? = null
)
