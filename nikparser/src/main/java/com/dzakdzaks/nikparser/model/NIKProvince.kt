package com.dzakdzaks.nikparser.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NIKProvince(
    val id: String,
    val name: String,
)
