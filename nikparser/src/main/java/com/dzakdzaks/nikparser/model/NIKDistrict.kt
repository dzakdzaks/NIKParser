package com.dzakdzaks.nikparser.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NIKDistrict(
    val id: String,
    val name: String,
    val zipCode: String,
)
