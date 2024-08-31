package ua.teri.totp.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Http {
    private val client = OkHttpClient.Builder().callTimeout(2, TimeUnit.SECONDS).followRedirects(false).build()
    private val executor = Executors.newSingleThreadExecutor()

    fun head(url: String): Response {
        return execute(Request.Builder().url(url).head().build())
    }

    fun execute(request: Request): Response {
        return executor.submit<Response> {
            client.newCall(request).execute()
        }.get()  // Wait for the result synchronously
    }
}