package com.dzakdzaks.nikparserapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dzakdzaks.nikparser.NIKParserImpl
import com.dzakdzaks.nikparser.model.NIKParserResponse
import com.dzakdzaks.nikparserapp.ui.theme.NIKParserTheme
import com.squareup.moshi.Moshi

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NIKParserTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Home()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("NIK Parser") }) },
        content = { paddingValues ->
            MyContent(paddingValues)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyContent(paddingValues: PaddingValues) {
    val context = LocalContext.current

    var text by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    val maxChar = 16

    val digitPattern = remember { Regex("^\\d+\$") }

    var isError by remember { mutableStateOf(false) }

    var result by remember { mutableStateOf("") }

    val nikParser = NIKParserImpl(context)

    val nikParserAdapter by lazy {
        Moshi.Builder().build().adapter(NIKParserResponse::class.java)
    }

    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            label = { Text("NIK") },
            value = text,
            onValueChange = {
                if (it.annotatedString.length <= 16
                    && (it.annotatedString.isEmpty() || it.annotatedString.matches(digitPattern))
                ) {
                    text = it
                    isError = if (text.annotatedString.length == maxChar) {
                        val nikParseResult = nikParser.parseNik(it.annotatedString.text)
                        result = nikParserAdapter.toJson(nikParseResult)
                        !nikParseResult.isValid
                    } else {
                        it.annotatedString.isNotEmpty()
                    }
                }
            },
            isError = isError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            supportingText = {
                Text(
                    text = if (isError) "NIK not valid" else ""
                )
            }
        )
        Text(
            modifier = Modifier.padding(16.dp),
            text = result
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NIKParserTheme {
        Home()
    }
}
