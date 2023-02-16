package com.dzakdzaks.nikparser

import com.dzakdzaks.nikparser.model.NIKParserResponse

interface NIKParser {
    fun parseNik(nik: String): NIKParserResponse
}
