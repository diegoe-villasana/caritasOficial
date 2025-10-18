package com.example.template2025.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.template2025.R

val Gotham = FontFamily(
    Font(R.font.gotham_thin, FontWeight.Thin),
    Font(R.font.gotham_xlight, FontWeight.ExtraLight),
    Font(R.font.gotham_light, FontWeight.Light),
    Font(R.font.gotham_book, FontWeight.Normal),
    Font(R.font.gotham_medium, FontWeight.Medium),
    Font(R.font.gotham_bold, FontWeight.Bold),
    Font(R.font.gotham_ultra, FontWeight.ExtraBold),
    Font(R.font.gotham_black, FontWeight.Black),

    Font(R.font.gotham_thinitalic, FontWeight.Thin, style = androidx.compose.ui.text.font.FontStyle.Italic),
    Font(R.font.gotham_xlightitalic, FontWeight.ExtraLight, style = androidx.compose.ui.text.font.FontStyle.Italic),
    Font(R.font.gotham_lightitalic, FontWeight.Light, style = androidx.compose.ui.text.font.FontStyle.Italic),
    Font(R.font.gotham_bookitalic, FontWeight.Normal, style = androidx.compose.ui.text.font.FontStyle.Italic),
    Font(R.font.gotham_mediumitalic, FontWeight.Medium, style = androidx.compose.ui.text.font.FontStyle.Italic),
    Font(R.font.gotham_bolditalic, FontWeight.Bold, style = androidx.compose.ui.text.font.FontStyle.Italic),
    Font(R.font.gotham_ultraitalic, FontWeight.ExtraBold, style = androidx.compose.ui.text.font.FontStyle.Italic),
    Font(R.font.gotham_blackitalic, FontWeight.Black, style = androidx.compose.ui.text.font.FontStyle.Italic)
)

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = Gotham,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp
    ),
    headlineLarge = TextStyle(
    fontFamily = Gotham,
    fontWeight = FontWeight.Medium,
    fontSize = 32.sp
    ),
    titleLarge = TextStyle(
    fontFamily = Gotham,
    fontWeight = FontWeight.Medium,
    fontSize = 22.sp
    ),
    bodyLarge = TextStyle(
    fontFamily = Gotham,
    fontWeight = FontWeight.Normal,
    fontSize = 16.sp
    ),
    labelLarge = TextStyle(
    fontFamily = Gotham,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp
    )
)
// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)