package edumate.app.domain.model.classroom.courseWork

data class DueDate(
    val day: Int? = null,
    val month: Int? = null,
    val year: Int? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DueDate) return false

        if (day != other.day) return false
        if (month != other.month) return false
        if (year != other.year) return false

        return true
    }

    override fun hashCode(): Int {
        var result = day ?: 0
        result = 31 * result + (month ?: 0)
        result = 31 * result + (year ?: 0)
        return result
    }
}
