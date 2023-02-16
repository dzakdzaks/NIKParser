package com.dzakdzaks.nikparser.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NIKParserResponse(
    val nik: String, // full nik
    val isValid: Boolean, // true/false
    val province: NIKProvince? = null, // province object
    val regency: NIKRegency? = null, // regency object
    val district: NIKDistrict? = null, // district object
    val birthDate: String? = null, // yyyy-MM-dddd
    val gender: String? = null, // male/female
    val uniqueCode: String? = null, // last four digit
)
