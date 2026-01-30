package com.overklassniy.stankinschedule.core.ui.ext

import android.content.Intent
import android.net.Uri

/**
 * Создаёт [Intent] для отправки данных (файла) через другие приложения.
 *
 * @param uri URI файла для передачи.
 * @param memeType MIME-тип содержимого.
 * @return Готовый [Intent] с флагом предоставления доступа на чтение.
 */
fun shareDataIntent(uri: Uri, memeType: String): Intent {
    return Intent(Intent.ACTION_SEND).apply {
        type = memeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}
