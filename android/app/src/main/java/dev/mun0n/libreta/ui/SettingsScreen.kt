package dev.mun0n.libreta.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mun0n.libreta.Copy
import dev.mun0n.libreta.game.DirectionMode
import dev.mun0n.libreta.state.LibretaViewModel
import dev.mun0n.libreta.state.SizeMode

@Composable
fun SettingsScreen(vm: LibretaViewModel, modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
            .background(Desk)
            .padding(horizontal = 28.dp, vertical = 16.dp),
    ) {
        Text(Copy.settings, fontFamily = Fraunces, fontSize = 48.sp, color = Ink)
        Text(
            Copy.doneCount(vm.completedCount),
            fontFamily = Atkinson,
            fontSize = 22.sp,
            color = Ink,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
        )
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Text(Copy.letterSize, fontFamily = Atkinson, fontSize = 20.sp, color = InkSoft)
            SizeMode.entries.forEach { mode ->
                Choice(
                    title = Copy.sizes.getValue(mode.name),
                    hint = Copy.sizeHint.getValue(mode.name),
                    selected = vm.settings.size == mode,
                    onClick = { vm.setSize(mode) },
                )
            }
            Text(
                Copy.directions,
                fontFamily = Atkinson,
                fontSize = 20.sp,
                color = InkSoft,
                modifier = Modifier.padding(top = 16.dp),
            )
            Choice(
                title = Copy.book,
                hint = Copy.bookHint,
                selected = vm.settings.directions == DirectionMode.book,
                onClick = { vm.setDirections(DirectionMode.book) },
            )
            Choice(
                title = Copy.straight,
                hint = Copy.straightHint,
                selected = vm.settings.directions == DirectionMode.straight,
                onClick = { vm.setDirections(DirectionMode.straight) },
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .heightIn(min = 64.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    if (vm.settings.sound) Copy.soundOn else Copy.soundOff,
                    fontFamily = Atkinson,
                    fontSize = 24.sp,
                    color = Ink,
                    modifier = Modifier.weight(1f),
                )
                Switch(
                    checked = vm.settings.sound,
                    onCheckedChange = vm::setSound,
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = Ink,
                        checkedThumbColor = Paper,
                    ),
                )
            }
        }
        PaperButton(Copy.back, vm::back, Modifier.fillMaxWidth().padding(top = 12.dp))
    }
}

@Composable
private fun Choice(title: String, hint: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .heightIn(min = 64.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Paper)
            .border(if (selected) 3.dp else 1.dp, if (selected) Ink else Ink.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { this.selected = selected }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(title, fontFamily = Atkinson, fontSize = 24.sp, color = Ink)
        Text(hint, fontFamily = Atkinson, fontSize = 16.sp, color = InkSoft)
    }
}
