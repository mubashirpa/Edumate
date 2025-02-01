package app.edumate.domain.model.courseWork

import androidx.annotation.Keep

@Keep // To prevent this Enum's serializer from being obfuscated in minified build (Since it is used in navigation)
enum class CourseWorkType {
    ASSIGNMENT,
    MATERIAL,
    MULTIPLE_CHOICE_QUESTION,
    SHORT_ANSWER_QUESTION,
}
