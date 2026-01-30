package com.overklassniy.stankinschedule.schedule.core.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

/**
 * Текстовый компонент с поддержкой короткого и долгого нажатия.
 *
 * При клике/лонг-клике определяет аннотацию (например, ссылку) по позиции курсора
 * и передаёт её в соответствующий колбэк.
 *
 * @param text Размеченный текст [AnnotatedString] с возможными аннотациями.
 * @param modifier Внешний модификатор.
 * @param style Стиль текста.
 * @param softWrap Перенос строк.
 * @param overflow Поведение переполнения.
 * @param maxLines Максимальное число строк.
 * @param interactionSource Источник взаимодействий для эффектов нажатия.
 * @param onClick Колбэк короткого нажатия с аннотацией под курсором (или null).
 * @param onLongClick Колбэк долгого нажатия с аннотацией под курсором (или null).
 */
@Composable
fun LongClickableText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = false,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource,
    onClick: (annotation: AnnotatedString.Range<String>?) -> Unit,
    onLongClick: (annotation: AnnotatedString.Range<String>?) -> Unit
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val gesture = Modifier.pointerInput(onClick, onLongClick, interactionSource) {
        // Обрабатываем жесты: долгий тап, короткий тап и press (для визуальной обратной связи)
        detectTapGestures(
            onLongPress = { pos ->
                layoutResult.value?.let { layout ->
                    val offset = layout.getOffsetForPosition(pos)
                    val annotation = text.getStringAnnotations(offset, offset).firstOrNull()
                    onLongClick(annotation)
                }
            },
            onTap = { pos ->
                layoutResult.value?.let { layout ->
                    val offset = layout.getOffsetForPosition(pos)
                    val annotation = text.getStringAnnotations(offset, offset).firstOrNull()
                    onClick(annotation)
                }
            },
            onPress = { pos ->
                val annotation = layoutResult.value?.let { layout ->
                    val offset = layout.getOffsetForPosition(pos)
                    text.getStringAnnotations(offset, offset).firstOrNull()
                }

                // Если аннотации под курсором нет — эмитим обычное нажатие для стандартной индикации
                if (annotation == null) {
                    val press = PressInteraction.Press(pos)
                    interactionSource.emit(press)
                    awaitRelease()
                    interactionSource.emit(PressInteraction.Release(press))
                }
            }
        )
    }

    Text(
        text = text,
        modifier = modifier.then(gesture),
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = {
            layoutResult.value = it
        }
    )
}