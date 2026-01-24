package com.overklassniy.stankinschedule.schedule.repository.data.repository

import android.content.Context
import com.overklassniy.stankinschedule.schedule.core.domain.model.PairModel
import com.overklassniy.stankinschedule.schedule.parser.domain.model.ParseResult
import com.overklassniy.stankinschedule.schedule.parser.domain.model.ParserSettings
import com.overklassniy.stankinschedule.schedule.parser.domain.usecase.ParserUseCase
import com.overklassniy.stankinschedule.schedule.repository.domain.repository.ScheduleLoaderService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.joda.time.DateTime
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.net.Proxy
import javax.inject.Inject

class MoodleLoaderService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val parserUseCase: ParserUseCase
) : ScheduleLoaderService {

    private val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    private val LOGIN_URL = "https://edu.stankin.ru/login/index.php"

    private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

    private val cookieJar = object : CookieJar {
        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            val host = url.host
            cookieStore.getOrPut(host) { mutableListOf() }.apply {
                cookies.forEach { newCookie ->
                    removeAll { it.name == newCookie.name }
                }
                addAll(cookies)
            }
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            return cookieStore[url.host] ?: emptyList()
        }
    }

    private val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .proxy(Proxy.NO_PROXY)
        .followRedirects(true)
        .followSslRedirects(true)
        .addNetworkInterceptor { chain ->
            val request = chain.request()
            val response = chain.proceed(request)

            if (response.code == 407) {
                return@addNetworkInterceptor response.newBuilder()
                    .code(401)
                    .message("Unauthorized (Rewritten from 407)")
                    .build()
            }
            response
        }
        .build()

    override suspend fun schedule(category: String, schedule: String): List<PairModel> = withContext(Dispatchers.IO) {
        val tempFile = downloadFileInternal(schedule, "schedule_import")

        val currentYear = DateTime.now().year
        val settings = ParserSettings(
            scheduleYear = currentYear,
            parserThreshold = 0.5f
        )

        val results = parserUseCase.parsePDF(tempFile.absolutePath, settings)

        val pairs = mutableListOf<PairModel>()
        for (result in results) {
            if (result is ParseResult.Success) {
                pairs.add(result.pair)
            }
        }

        tempFile.delete()

        return@withContext pairs
    }

    override suspend fun downloadScheduleFile(category: String, schedule: String, fileName: String): String = withContext(Dispatchers.IO) {
        val file = downloadFileInternal(schedule, fileName)
        return@withContext file.absolutePath
    }

    private suspend fun downloadFileInternal(url: String, fileName: String): File = withContext(Dispatchers.IO) {
        cookieStore.clear()

        var request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .build()
            
        var response = client.newCall(request).execute()

        val contentType = response.header("Content-Type")
        if (response.request.url.toString().contains("/login/") || 
            (contentType != null && contentType.contains("text/html")) ||
            response.code == 401) {
            
            response.close()

            val loginPageRequest = Request.Builder()
                .url(LOGIN_URL)
                .header("User-Agent", USER_AGENT)
                .build()
            
            val loginPageResponse = client.newCall(loginPageRequest).execute()
            val loginPageHtml = loginPageResponse.body?.string() ?: ""
            loginPageResponse.close()

            val doc = Jsoup.parse(loginPageHtml)
            val loginToken = doc.select("input[name=logintoken]").attr("value")

            val formBody = FormBody.Builder()
                .add("username", "guest")
                .add("password", "guest")
                .apply {
                    if (loginToken.isNotEmpty()) {
                        add("logintoken", loginToken)
                    }
                }
                .build()

            val loginRequest = Request.Builder()
                .url(LOGIN_URL)
                .header("User-Agent", USER_AGENT)
                .post(formBody)
                .build()
            
            val loginResponse = client.newCall(loginRequest).execute()
            loginResponse.close()

            request = Request.Builder()
                .url(url)
                .header("User-Agent", USER_AGENT)
                .build()
                
            response = client.newCall(request).execute()
        }

        if (!response.isSuccessful) {
            throw Exception("Failed to download schedule. Response code: ${response.code}")
        }

        val body = response.body ?: throw Exception("Empty body")

        val downloadsDir = File(context.filesDir, "schedule_downloads")
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        
        val file = File(downloadsDir, "${fileName}.pdf")
        FileOutputStream(file).use { output ->
            body.byteStream().use { input ->
                input.copyTo(output)
            }
        }

        return@withContext file
    }
}
