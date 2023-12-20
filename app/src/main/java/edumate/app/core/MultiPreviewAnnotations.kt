package edumate.app.core

import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers

/**
 * Add this multi preview annotation to a composable to render the composable in extra small and
 * extra large font size.
 *
 * Read more in the [documentation](https://d.android.com/jetpack/compose/tooling#preview-multipreview)
 */
@Preview(
    name = "small font",
    group = "font scales",
    fontScale = 0.5f,
)
@Preview(
    name = "large font",
    group = "font scales",
    fontScale = 1.5f,
)
annotation class FontScalePreviews

/**
 * Add this multi preview annotation to a composable to render the composable on various device
 * sizes: phone, foldable, and tablet.
 *
 * Read more in the [documentation](https://d.android.com/jetpack/compose/tooling#preview-multipreview)
 */
@Preview(
    name = "phone",
    group = "devices",
    device = "id:pixel_6_pro",
    showSystemUi = true,
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
)
@Preview(
    name = "foldable",
    group = "devices",
    device = "spec:width=673.5dp,height=841dp,dpi=480",
    showSystemUi = true,
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
)
@Preview(
    name = "tablet",
    group = "devices",
    device = "spec:width=1280dp,height=800dp,dpi=480",
    showSystemUi = true,
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
    wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE,
)
annotation class DevicePreviews

/**
 * Add this multi preview annotation to a composable to render the composable in various common
 * configurations:
 * - Dark theme
 * - Small and large font size
 * - various device sizes
 *
 * Read more in the [documentation](https://d.android.com/jetpack/compose/tooling#preview-multipreview)
 *
 * _Note: Combining multi preview annotations doesn't mean all the different combinations are shown.
 * Instead, each multi preview annotation acts by its own and renders only its own variants._
 */
@Preview(
    name = "dark theme",
    group = "themes",
    uiMode = UI_MODE_NIGHT_YES,
)
@FontScalePreviews
@DevicePreviews
annotation class CompletePreviews
