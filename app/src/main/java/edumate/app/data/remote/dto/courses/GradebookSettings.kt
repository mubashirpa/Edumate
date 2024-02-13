package edumate.app.data.remote.dto.courses

import kotlinx.serialization.Serializable

@Serializable
data class GradebookSettings(
    val calculationType: CalculationType? = null,
    val displaySetting: DisplaySetting? = null,
    val gradeCategories: List<GradeCategory>? = null,
)
