package com.overklassniy.stankinschedule.core.domain.ext

/**
 * Удаляет элементы из коллекции, удовлетворяющие заданному условию [filter].
 *
 * Аналог метода `removeIf`, доступного в Java 8+, реализованный для поддержки совместимости.
 * Использует итератор для безопасного удаления элементов во время обхода.
 *
 * @param T Тип элементов в коллекции.
 * @param filter Предикат, определяющий, какие элементы нужно удалить. Возвращает `true`, если элемент нужно удалить.
 * @return `true`, если хотя бы один элемент был удален, иначе `false`.
 */
fun <T> MutableCollection<T>.removeIf7(filter: (item: T) -> Boolean): Boolean {
    var removed = false
    val it = iterator()
    while (it.hasNext()) {
        if (filter(it.next())) {
            it.remove()
            removed = true
        }
    }
    return removed
}