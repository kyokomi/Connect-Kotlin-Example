package dev.kyokomi.eliza

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import buf.connect.demo.eliza.v1.Eliza
import buf.connect.demo.eliza.v1.ElizaServiceClient
import build.buf.connect.ProtocolClientConfig
import build.buf.connect.extensions.GoogleJavaProtobufStrategy
import build.buf.connect.impl.ProtocolClient
import build.buf.connect.okhttp.ConnectOkHttpClient
import build.buf.connect.protocols.NetworkProtocol
import dev.kyokomi.eliza.ui.theme.ElizaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var elizaServiceClient: ElizaServiceClient

    private val _talksStateFlow = MutableStateFlow<List<String>>(emptyList())
    private val talksStateFlow: StateFlow<List<String>> = _talksStateFlow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val client = ProtocolClient(
            httpClient = ConnectOkHttpClient(),
            config = ProtocolClientConfig(
                host = "https://demo.connect.build/",
                serializationStrategy = GoogleJavaProtobufStrategy(),
                networkProtocol = NetworkProtocol.CONNECT,
            ),
        )
        elizaServiceClient = ElizaServiceClient(client)

        setContent {
            val talks by talksStateFlow.collectAsState()

            ElizaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ElizaScreen(
                        talks = talks,
                        onClickSendButton = { sendText ->
                            addTalks(sendText)
                            lifecycleScope.launch(Dispatchers.IO) {
                                val response = talkToEliza(sendText)
                                response?.let { addTalks(it) }
                            }
                        },
                    )
                }
            }
        }
    }

    private fun addTalks(text: String) {
        _talksStateFlow.value = _talksStateFlow.value.toMutableList().apply {
            add(text)
        }
    }

    private suspend fun talkToEliza(sentence: String): String? {
        val response =
            elizaServiceClient.say(Eliza.SayRequest.newBuilder().setSentence(sentence).build())
        val elizaSentence = response.success { success -> success.message.sentence }
        response.failure { failure ->
            Log.e("MainActivity", "${failure.error}")
        }
        Log.d("MainActivity", "$elizaSentence")
        return elizaSentence
    }
}

@Composable
fun ElizaScreen(
    modifier: Modifier = Modifier,
    talks: List<String>,
    onClickSendButton: (String) -> Unit
) {
    var textValue by remember { mutableStateOf("") }
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                modifier = Modifier.weight(1.0f),
                value = textValue,
                onValueChange = {
                    textValue = it
                },
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Button(onClick = {
                onClickSendButton(textValue)
                textValue = ""
            }) {
                Text(text = "Send")
            }
        }

        LazyColumn(
            modifier = Modifier.padding(8.dp),
            content = {
                items(talks) { talk ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp),
                        text = talk
                    )
                    Divider()
                }
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ElizaScreenPreview() {
    ElizaTheme {
        ElizaScreen(
            talks = listOf("Hello", "World"),
            onClickSendButton = {},
        )
    }
}