package edumate.app.domain.model.courses

data class GradeBookSettings(
    val calculationType: CalculationType = CalculationType.CALCULATION_TYPE_UNSPECIFIED,
    val displaySetting: DisplaySetting = DisplaySetting.DISPLAY_SETTING_UNSPECIFIED,
    val gradeCategories: GradeCategory? = null
)