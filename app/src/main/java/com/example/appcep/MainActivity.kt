package com.example.appcep

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class MainActivity : ComponentActivity() {

    private lateinit var client: HttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        client = HttpClient(CIO) {

        }

        val buttonCEP = findViewById<Button>(R.id.button)
        val editText = findViewById<EditText>(R.id.editText)

        editText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                lifecycleScope.launch {
                    val response = requestCep(editText.text.toString())
                    updateUI(response)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                TODO("Not yet implemented")
            }
        })

        buttonCEP.setOnClickListener {
            lifecycleScope.launch {
                val response = requestCep(editText.text.toString())
                updateUI(response)
            }
        }
    }

    private suspend fun requestCep(cep: String): String {
        try {
            val response: HttpResponse = client.get("https://viacep.com.br/ws/${cep}/json/")
            val responseBody = response.bodyAsText()

            val json = JSONObject(responseBody)
            return "CEP: ${json.getString("cep")} \n" +
                    "Logradouro: ${json.getString("logradouro")} \n" +
                    "Bairro: ${json.getString("bairro")} \n" +
                    "Localidade: ${json.getString("localidade")}"
        }
        catch (e: Exception) {
            return "Failed to retrieve data: ${e.localizedMessage}"
        }
    }

    private suspend fun updateUI(result: String) {
        withContext(Dispatchers.Main) {
            // Update UI on the main thread
            val textViewCep = findViewById<TextView>(R.id.infoCEP)
            textViewCep.text = result
        }
    }

    override fun onDestroy() {
        client.close()
        super.onDestroy()
    }

}

