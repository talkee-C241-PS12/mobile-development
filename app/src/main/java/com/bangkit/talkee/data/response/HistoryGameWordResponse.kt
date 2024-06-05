package com.bangkit.talkee.data.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class HistoryGameResponse(

    @field:SerializedName("listQuestion")
    val listQuestion: List<ListQuestionItem> = emptyList(),
)

data class ListQuestionItem(

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("type")
    val type: Int,

    @field:SerializedName("question_no")
    val questionNo: Int,

    @field:SerializedName("question")
    val question: String? = null,

    @field:SerializedName("answers")
    val answers: String? = null,

    @field:SerializedName("answer")
    val answer: String? = null,

    @field:SerializedName("isCorrect")
    val isCorrect: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(type)
        parcel.writeInt(questionNo)
        parcel.writeString(question)
        parcel.writeString(answers)
        parcel.writeString(answer)
        parcel.writeString(isCorrect)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ListGameItem> {
        override fun createFromParcel(parcel: Parcel): ListGameItem {
            return ListGameItem(parcel)
        }

        override fun newArray(size: Int): Array<ListGameItem?> {
            return arrayOfNulls(size)
        }
    }
}