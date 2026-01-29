package com.overklassniy.stankinschedule.news.core.domain.model

/**
 * Полное содержимое новости.
 *
 * @property id Идентификатор новости.
 * @property date Дата публикации.
 * @property title Заголовок.
 * @property previewImageUrl Ссылка на превью изображение.
 * @property text Текстовое содержимое (может содержать HTML).
 * @property deltaFormat Содержимое в формате Delta (JSON для Quill редактора).
 */
data class NewsContent(
    val id: Int,
    val date: String,
    val title: String,
    val previewImageUrl: String,
    val text: String,
    val deltaFormat: String,
) {

    /**
     * Генерирует HTML-страницу для отображения новости с использованием Quill JS.
     *
     * Создает HTML шаблон, который инициализирует Quill в режиме read-only и рендерит контент из формата Delta.
     *
     * @param backgroundColor Цвет фона страницы (CSS значение).
     * @return Строка, содержащая полный HTML код страницы.
     */
    fun prepareQuillPage(backgroundColor: String = "inherit"): String = """
        <!DOCTYPE html>
        <html lang="ru">
            <head>
                <meta charset='UTF-8'>
                <meta name='viewport' content='width=device-width, initial-scale=1'>
                <link rel="stylesheet" href="file:///android_asset/news/quill.css">         
                <script src="file:///android_asset/news/quill.min.js" type="text/javascript"></script>
            </head>
            <body style="background-color: $backgroundColor;">
                <div id="raw-text"> $text </div>
                <div id="editor" style="display: none;"></div>
                <div id="viewer" style="padding: 0.8rem;"></div>        
                <script>
                    var delta = $deltaFormat
                    var quill = new Quill('#editor', { readOnly: true });
                    try {
                        quill.setContents(delta);
                    } catch (error) {
                        console.error(error);
                    }
                    document.getElementById("viewer").innerHTML = quill.root.innerHTML;
                    document.getElementById("editor").remove();            
                    Android.onNewsLoaded();          
                </script>
            </body>
        </html>
    """.trimIndent()
}