package edumate.app.domain.model.courses

data class GradeCategory(
    val id: String = "",
    val name: String = "",
    val weight: Int = 0,
    val defaultGradeDenominator: Int = 0,
)
