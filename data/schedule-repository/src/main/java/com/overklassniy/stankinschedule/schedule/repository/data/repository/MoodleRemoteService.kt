package com.overklassniy.stankinschedule.schedule.repository.data.repository

import android.util.Log
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryCategory
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryDescription
import com.overklassniy.stankinschedule.schedule.repository.domain.model.RepositoryItem
import com.overklassniy.stankinschedule.schedule.repository.domain.repository.ScheduleRemoteService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class MoodleRemoteService @Inject constructor() : ScheduleRemoteService {

    private val moodleUrl = "https://edu.stankin.ru/course/view.php?id=11557"
    private val loginUrl = "https://edu.stankin.ru/login/index.php"
    private val TAG = "MoodleRemoteService"
    private val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
    private val TIMEOUT_MS = 60_000

    private fun createTrustAllSslSocketFactory(): SSLSocketFactory {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, SecureRandom())
        return sslContext.socketFactory
    }

    override suspend fun description(): RepositoryDescription = withContext(Dispatchers.IO) {
        val now = DateTime.now()
        val academicYearStart = if (now.monthOfYear >= 9) now.year else now.year - 1
        
        return@withContext RepositoryDescription(
            lastUpdate = "Moodle",
            categories = listOf(
                RepositoryCategory("Расписание", academicYearStart)
            )
        )
    }

    override suspend fun category(category: String): List<RepositoryItem> = withContext(Dispatchers.IO) {
        val items = mutableListOf<RepositoryItem>()
        Log.d(TAG, "Starting category scraping for: $category")

        val cookies = mutableMapOf<String, String>()

        try {
            Log.d(TAG, "Connecting to Moodle URL: $moodleUrl")
            val response = Jsoup.connect(moodleUrl)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .sslSocketFactory(createTrustAllSslSocketFactory())
                .execute()
            
            cookies.putAll(response.cookies())
            var doc = response.parse()
            Log.d(TAG, "Initial connection successful. Title: ${doc.title()}")

            if (doc.title().contains("Вход") || doc.select(".login-form").isNotEmpty()) {
                Log.d(TAG, "Login page detected. Attempting guest login...")
                val loginForm = doc.select("form[action*='login/index.php']").first()
                val loginAction = loginForm?.attr("action") ?: loginUrl
                val loginToken = loginForm?.select("input[name=logintoken]")?.attr("value") ?: ""
                Log.d(TAG, "Login token extracted: ${if(loginToken.isNotEmpty()) "YES" else "NO"}")

                Log.d(TAG, "Sending login POST request...")
                val loginResponse = Jsoup.connect(loginAction)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .data("username", "guest")
                    .data("password", "guest")
                    .apply {
                        if (loginToken.isNotEmpty()) {
                            data("logintoken", loginToken)
                        }
                    }
                    .cookies(cookies)
                    .method(Connection.Method.POST)
                    .followRedirects(false)
                    .sslSocketFactory(createTrustAllSslSocketFactory())
                    .execute()
                
                cookies.putAll(loginResponse.cookies())
                Log.d(TAG, "Login response code: ${loginResponse.statusCode()}")
                
                if (loginResponse.statusCode() == 303 || loginResponse.statusCode() == 302) {
                     val location = loginResponse.header("Location")
                     if (location != null) {
                         Log.d(TAG, "Login successful (Redirect). Location: $location")

                         val redirectResponse = Jsoup.connect(location)
                            .userAgent(USER_AGENT)
                            .timeout(TIMEOUT_MS)
                            .cookies(cookies)
                            .sslSocketFactory(createTrustAllSslSocketFactory())
                            .execute()
                            
                         cookies.putAll(redirectResponse.cookies())
                         doc = redirectResponse.parse()
                     } else {
                         Log.e(TAG, "Login redirect failed: Location header is null")
                     }
                } else {
                     Log.e(TAG, "Login failed. Status code: ${loginResponse.statusCode()}")
                     doc = loginResponse.parse()
                     val errorMsg = doc.select(".error").text()
                     if (errorMsg.isNotEmpty()) Log.e(TAG, "Moodle Error: $errorMsg")
                }
                
                Log.d(TAG, "Page title after login flow: ${doc.title()}")
            }

            val folderLinks = doc.select("a[href*='mod/folder/view.php']")
            val relevantFolders = mutableListOf<String>()
            Log.d(TAG, "Found ${folderLinks.size} total folder links")
            
            for (link in folderLinks) {
                val text = link.text()

                val isExam = text.contains("Экзамен", ignoreCase = true) || text.contains("Зачет", ignoreCase = true)
                val isGroupList = text.contains("Списки групп", ignoreCase = true)

                if ((text.contains("Расписание", ignoreCase = true) || text.contains("курс", ignoreCase = true)) 
                    && !isExam && !isGroupList) {
                    
                    val href = link.attr("href")
                    relevantFolders.add(href)
                    Log.d(TAG, "Relevant folder found: $text -> $href")
                } else {
                    Log.d(TAG, "Skipped folder: $text")
                }
            }
            Log.d(TAG, "Total relevant folders to process: ${relevantFolders.size}")

            for (folderUrl in relevantFolders) {
                try {
                    Log.d(TAG, "Processing folder: $folderUrl")
                    val folderResponse = Jsoup.connect(folderUrl)
                        .userAgent(USER_AGENT)
                        .timeout(TIMEOUT_MS)
                        .cookies(cookies)
                        .sslSocketFactory(createTrustAllSslSocketFactory())
                        .execute()

                    cookies.putAll(folderResponse.cookies())
                    val folderDoc = folderResponse.parse()
                    
                    val fileLinks = folderDoc.select("a")
                    var filesInFolder = 0
                    for (link in fileLinks) {
                        val href = link.attr("href")
                        if (href.contains("forcedownload=1") || href.contains("/mod/resource/view.php") || href.contains("pluginfile.php")) {
                            
                            val text = link.text()
                            if (text.contains("Скачать папку", ignoreCase = true)) continue

                            var name = text.replace(" Файл", "").replace(" File", "").trim()
                            if (name.endsWith(".pdf", ignoreCase = true)) {
                                name = name.substringBeforeLast(".")
                            }

                            val scheduleRegex = Regex("^[А-Яа-яA-Za-z]+-\\d+-\\d+.*")
                            
                            val isSchedule = name.matches(scheduleRegex) && !name.contains("Экзамен", ignoreCase = true)
                            
                            if (name.isNotEmpty() && isSchedule) {
                                items.add(RepositoryItem(name, href, category))
                                filesInFolder++
                                Log.d(TAG, "File found: $name -> $href")
                            } else {
                                Log.d(TAG, "Skipped file: $name")
                            }
                        }
                    }
                    Log.d(TAG, "Found $filesInFolder files in folder")
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing folder $folderUrl", e)
                    e.printStackTrace()
                }
            }
            
        } catch (e: Exception) {
             Log.e(TAG, "Fatal error in scraping", e)
             throw Exception("Failed to load schedule from Moodle: ${e.message}", e)
        }
        
        Log.d(TAG, "Scraping finished. Total items found: ${items.size}")
        return@withContext items
    }
}
