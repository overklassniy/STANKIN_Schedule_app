package com.overklassniy.stankinschedule.core.domain.ext

/**
 * Сравнивает две версии в формате X.Y.Z.
 *
 * Числовые компоненты версии сравниваются позиционно слева направо.
 * Нечисловые компоненты игнорируются, недостающие компоненты
 * считаются равными нулю.
 *
 * @receiver Текущая версия.
 * @param other Версия для сравнения.
 * @return true, если [other] новее [this].
 */
infix fun String.isOlderVersionThan(other: String): Boolean {
    val latest = other.split(".").mapNotNull { it.toIntOrNull() }
    val current = this.split(".").mapNotNull { it.toIntOrNull() }

    return (0 until maxOf(latest.size, current.size))
        .map { i -> latest.getOrElse(i) { 0 } to current.getOrElse(i) { 0 } }
        .firstOrNull { (l, c) -> l != c }
        ?.let { (l, c) -> l > c }
        ?: false
}
