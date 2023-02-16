package com.dzakdzaks.nikparser.util

import android.content.Context
import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale

internal fun Context.readFromAssets(filename: String): String = try {
    val reader = BufferedReader(InputStreamReader(assets.open(filename)))
    val sb = StringBuilder()
    var line = reader.readLine()
    while (line != null) {
        sb.append(line)
        line = reader.readLine()
    }
    reader.close()
    sb.toString()
} catch (exp: Exception) {
    println("Failed reading line from $filename -> ${exp.localizedMessage}")
    ""
}

internal fun String.capitalized(): String {
    return split(" ").joinToString(" ") {
        it.lowercase().replaceFirstChar { c ->
            if (c.isLowerCase())
                c.titlecase(Locale.getDefault())
            else
                c.toString()
        }
    }
}

internal fun Moshi.fromJsonMapAdapter(json: String): Map<String, String> {
    val type = Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
    val adapter: JsonAdapter<Map<String, String>> = adapter(type)
    return adapter.fromJson(json).orEmpty()
}
