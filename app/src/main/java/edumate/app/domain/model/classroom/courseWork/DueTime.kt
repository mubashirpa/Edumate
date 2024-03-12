package edumate.app.domain.model.classroom.courseWork

data class DueTime(
    val hours: Int? = null,
    val minutes: Int? = null,
    val nanos: Int? = null,
    val seconds: Int? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DueTime) return false

        if (hours != other.hours) return false
        if (minutes != other.minutes) return false
        if (seconds != other.seconds) return false
        if (nanos != other.nanos) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hours ?: 0
        result = 31 * result + (minutes ?: 0)
        result = 31 * result + (seconds ?: 0)
        result = 31 * result + (nanos ?: 0)
        return result
    }
}
