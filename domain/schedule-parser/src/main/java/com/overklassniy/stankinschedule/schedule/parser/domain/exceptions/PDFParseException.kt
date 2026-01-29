package com.overklassniy.stankinschedule.schedule.parser.domain.exceptions

/**
 * Исключение, возникающее при ошибках парсинга PDF файла.
 *
 * @param message Сообщение об ошибке.
 * @param cause Причина ошибки (опционально).
 */
class PDFParseException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {

    companion object {

        /**
         * Создает исключение, когда файл не найден.
         *
         * @param path Путь к файлу.
         * @return [PDFParseException] с соответствующим сообщением.
         */
        fun fileNotFound(path: String): PDFParseException {
            return PDFParseException("Не удалось открыть файл: $path")
        }

        /**
         * Создает исключение, когда файл не является корректным PDF или поврежден.
         *
         * @param cause Исходное исключение (опционально).
         * @return [PDFParseException] с соответствующим сообщением.
         */
        fun invalidPDF(cause: Throwable? = null): PDFParseException {
            return PDFParseException(
                "Файл не является корректным PDF документом или повреждён",
                cause
            )
        }

        /**
         * Создает исключение, когда PDF файл защищен паролем.
         *
         * @return [PDFParseException] с соответствующим сообщением.
         */
        fun passwordProtected(): PDFParseException {
            return PDFParseException("PDF файл защищён паролем")
        }

        /**
         * Создает исключение при общей ошибке чтения PDF.
         *
         * @param cause Исходное исключение (опционально).
         * @return [PDFParseException] с соответствующим сообщением.
         */
        fun parsingError(cause: Throwable? = null): PDFParseException {
            return PDFParseException(
                "Ошибка при чтении PDF файла",
                cause
            )
        }
    }
}
