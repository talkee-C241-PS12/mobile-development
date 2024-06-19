package com.bangkit.talkee.data.response

import com.google.gson.annotations.SerializedName

data class HistoryDetailResponse(

	@field:SerializedName("poin")
	val poin: Int? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("idgame")
	val idgame: String? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("poinuser")
	val poinuser: Int? = null,

	@field:SerializedName("idusergame")
	val idusergame: String? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null,

	@field:SerializedName("tips")
	val tips: String? = null,

	@field:SerializedName("pertanyaan")
	val pertanyaan: List<HistoryDetailItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class HistoryDetailItem(

	@field:SerializedName("jawaban2")
	val jawaban2: String? = null,

	@field:SerializedName("jawaban")
	val jawaban: String? = null,

	@field:SerializedName("jawaban3")
	val jawaban3: String? = null,

	@field:SerializedName("tipe")
	val tipe: Int? = null,

	@field:SerializedName("jawabanuser")
	val jawabanuser: String? = null,

	@field:SerializedName("idpertanyaan")
	val idpertanyaan: String? = null,

	@field:SerializedName("jawaban1")
	val jawaban1: String? = null,

	@field:SerializedName("pertanyaan")
	val pertanyaan: String? = null
)
