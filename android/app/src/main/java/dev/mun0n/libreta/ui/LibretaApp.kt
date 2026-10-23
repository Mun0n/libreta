package dev.mun0n.libreta.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.activity.compose.BackHandler
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.mun0n.libreta.data.themes
import dev.mun0n.libreta.game.entries
import dev.mun0n.libreta.game.generate
import dev.mun0n.libreta.state.LibretaViewModel
import dev.mun0n.libreta.state.Screen

@Composable
fun LibretaApp(vm: LibretaViewModel = viewModel()) {
    BackHandler(enabled = vm.screen != Screen.Home) {
        if (vm.screen == Screen.Puzzle) vm.goHome() else vm.back()
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(Desk)
            .padding(WindowInsets.safeDrawing.asPaddingValues()),
    ) {
        when (vm.screen) {
            Screen.Home -> HomeScreen(vm)
            Screen.Settings -> SettingsScreen(vm)
            Screen.Puzzle -> PuzzleRoute(vm)
        }
    }
}

@Composable
private fun PuzzleRoute(vm: LibretaViewModel) {
    val current = vm.current
    val theme = themes.find { it.id == current?.themeId }
    if (current == null || theme == null) {
        LaunchedEffect(Unit) { vm.goHome() }
        return
    }
    val puzzle = remember(current.seed, theme.id, current.size, current.count, current.directions) {
        generate(
            seed = current.seed,
            words = entries(theme.words),
            size = current.size,
            directions = current.directions,
            count = current.count,
        )
    }
    PuzzleScreen(vm, theme, puzzle, current)
}
