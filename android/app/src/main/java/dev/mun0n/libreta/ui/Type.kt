package dev.mun0n.libreta.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mun0n.libreta.R

val Desk = Color(0xFFE4D2BC)
val Paper = Color(0xFFF6F1E4)
val Ink = Color(0xFF1C1915)
val InkSoft = Color(0xFF5C564C)
val ButtonFill = Color(0xFFE7DCC8)
val MarkerYellow = Color(0xFFFFDF4A)

val Atkinson = FontFamily(
    Font(R.font.atkinson_hyperlegible_regular, FontWeight.Normal),
    Font(R.font.atkinson_hyperlegible_bold, FontWeight.Bold),
)

@OptIn(ExperimentalTextApi::class)
val Fraunces = FontFamily(
    Font(
        R.font.fraunces,
        weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600)),
    ),
)

private val colors = lightColorScheme(
    primary = Ink,
    onPrimary = Paper,
    background = Desk,
    surface = Paper,
    onSurface = Ink,
)

@Composable
fun LibretaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(
            headlineLarge = TextStyle(
                fontFamily = Fraunces,
                fontWeight = FontWeight.SemiBold,
                fontSize = 48.sp,
            ),
            bodyLarge = TextStyle(
                fontFamily = Atkinson,
                fontSize = 22.sp,
            ),
        ),
        content = content,
    )
}

@Composable
fun PaperButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 64.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (emphasized) Ink else ButtonFill,
            contentColor = if (emphasized) Paper else Ink,
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = text,
            fontFamily = Atkinson,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center,
            color = if (emphasized) Paper else Ink,
        )
    }
}
