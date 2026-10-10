package com.overklassniy.stankinschedule.journal.core.data.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Генератор значений PKCE (RFC 7636) для OIDC-авторизации.
 */
object PkceGenerator {

    /** Длина code_verifier в символах (максимум по RFC 7636 — 128). */
    private const val VERIFIER_LENGTH = 128

    private val random = SecureRandom()

    /**
     * Генерирует случайный code_verifier.
     *
     * @return Строка длиной [VERIFIER_LENGTH] из URL-safe символов.
     */
    fun generateVerifier(): String {
        val bytes = ByteArray(96)
        random.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            .take(VERIFIER_LENGTH)
    }

    /**
     * Генерирует случайное значение state для запроса авторизации.
     *
     * @return URL-safe строка длиной 16 символов.
     */
    fun generateState(): String {
        val bytes = ByteArray(12)
        random.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    /**
     * Вычисляет code_challenge по методу S256 для переданного verifier.
     *
     * @param verifier Значение code_verifier.
     * @return Base64url-закодированный SHA-256 хеш verifier без padding.
     */
    fun challenge(verifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(verifier.toByteArray(Charsets.US_ASCII))
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }
}
