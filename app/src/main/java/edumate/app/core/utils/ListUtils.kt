package edumate.app.core.utils

inline fun <T> MutableList<T>.moveItemToFirstPosition(predicate: (T) -> Boolean) {
    for (element in withIndex()) {
        if (predicate(element.value)) {
            removeAt(element.index)
            add(0, element.value)
            break
        }
    }
}

inline fun <T> List<T>.moveItemToFirstPosition(predicate: (T) -> Boolean): List<T> {
    for (element in withIndex()) {
        if (predicate(element.value)) {
            return toMutableList().apply {
                removeAt(element.index)
                add(0, element.value)
            }.toList()
        }
    }
    return this
}
