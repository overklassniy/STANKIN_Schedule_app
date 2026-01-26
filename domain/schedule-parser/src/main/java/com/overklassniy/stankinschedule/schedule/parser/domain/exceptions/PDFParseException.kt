package com.overklassniy.stankinschedule.schedule.parser.domain.exceptions

/**
 * Исключение, возникающее при ошибке парсинга PDF файла.
 *
 * @param message Сообщение об ошибке
 * @param cause Исходное исключение (опционально)
 */
class PDFParseException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause) {
    
    companion object {
        /**
         * Создает исключение для случая, когда файл не найден или недоступен.
         */
        fun fileNotFound(path: String): PDFParseException {
            return PDFParseException("Не удалось открыть файл: $path")
        }
        
        /**
         * Создает исключение для случая, когда файл не является валидным PDF.
         */
        fun invalidPDF(cause: Throwable? = null): PDFParseException {
            return PDFParseException(
                "Файл не является корректным PDF документом или повреждён",
                cause
            )
        }
        
        /**
         * Создает исключение для случая, когда PDF защищен паролем.
         */
        fun passwordProtected(): PDFParseException {
            return PDFParseException("PDF файл защищён паролем")
        }
        
        /**
         * Создает исключение для общей ошибки парсинга.
         */
        fun parsingError(cause: Throwable? = null): PDFParseException {
            return PDFParseException(
                "Ошибка при чтении PDF файла",
                cause
            )
        }
    }
}
