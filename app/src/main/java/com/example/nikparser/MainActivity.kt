package com.example.nikparser

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.dzakdzaks.nikparser.NIKParser
import com.dzakdzaks.nikparser.NIKParserImpl
import com.dzakdzaks.nikparser.model.NIKParserResponse
import com.example.nikparser.ui.theme.NIKParserTheme
import com.squareup.moshi.Moshi

class MainActivity : ComponentActivity() {

    private val nikParser: NIKParser = NIKParserImpl(this) { isDataReady ->
        Log.e("walwaw", isDataReady.toString())
    }

    private val nikParserAdapter by lazy {
        Moshi.Builder().build().adapter(NIKParserResponse::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NIKParserTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(nikParserAdapter.toJson(nikParser.parseNik("3275091507010001")))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, onClickAction: (Int) -> Unit = {}) {
    ClickableText(
        text = AnnotatedString(name),
        onClick = onClickAction
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NIKParserTheme {
        Greeting("Android")
    }
}
