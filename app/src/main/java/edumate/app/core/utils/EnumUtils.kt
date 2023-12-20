package edumate.app.core.utils

inline fun <reified T : Enum<T>> String.enumValueOf(defaultValue: T? = null): T? =
    enumValues<T>().firstOrNull { it.name.equals(this, ignoreCase = true) } ?: defaultValue

inline fun <reified T : Enum<*>> enumValueOf(name: String): T? = T::class.java.enumConstants?.firstOrNull { it.name == name }
