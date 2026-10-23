package dev.mun0n.libreta.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mun0n.libreta.Copy
import dev.mun0n.libreta.data.themes
import dev.mun0n.libreta.state.LibretaViewModel

@Composable
fun HomeScreen(vm: LibretaViewModel, modifier: Modifier = Modifier) {
    val current = vm.current
    val resumeTheme = themes.find { it.id == current?.themeId }
    Column(
        modifier
            .fillMaxSize()
            .background(Desk)
            .padding(horizontal = 28.dp, vertical = 16.dp),
    ) {
        Text(Copy.appName, fontFamily = Fraunces, fontSize = 56.sp, color = Ink)
        Text(
            Copy.howTo,
            fontFamily = Atkinson,
            fontSize = 22.sp,
            color = Ink,
            modifier = Modifier.padding(top = 6.dp, bottom = 16.dp),
        )
        if (vm.resumeAvailable && current != null && resumeTheme != null) {
            PaperButton(
                text = Copy.continueWith(resumeTheme.name),
                onClick = vm::resume,
                emphasized = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            )
            Text(
                Copy.progress(current.found.size, current.total),
                fontFamily = Atkinson,
                fontSize = 20.sp,
                color = InkSoft,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }
        Text(
            Copy.chooseTheme,
            fontFamily = Fraunces,
            fontSize = 28.sp,
            color = Ink,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 12.dp),
        ) {
            items(themes, key = { it.id }) { theme ->
                Surface(
                    onClick = { vm.startTheme(theme.id) },
                    shape = RoundedCornerShape(16.dp),
                    color = Paper,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 104.dp),
                ) {
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ThemeGlyph(theme.id, Modifier.size(56.dp))
                        Spacer(Modifier.width(14.dp))
                        Text(
                            theme.name,
                            fontFamily = Atkinson,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = Ink,
                        )
                    }
                }
            }
        }
        PaperButton(Copy.settings, vm::goSettings, Modifier.fillMaxWidth())
    }
}
