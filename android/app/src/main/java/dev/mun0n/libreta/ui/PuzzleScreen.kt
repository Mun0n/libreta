package dev.mun0n.libreta.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mun0n.libreta.Copy
import dev.mun0n.libreta.data.Theme
import dev.mun0n.libreta.game.Cell
import dev.mun0n.libreta.game.Puzzle
import dev.mun0n.libreta.game.findHit
import dev.mun0n.libreta.state.CurrentPuzzle
import dev.mun0n.libreta.state.LibretaViewModel

@Composable
fun PuzzleScreen(
    vm: LibretaViewModel,
    theme: Theme,
    puzzle: Puzzle,
    current: CurrentPuzzle,
    modifier: Modifier = Modifier,
) {
    key(current.themeId, current.seed) {
        PuzzleBody(vm, theme, puzzle, current, modifier)
    }
}

@Composable
private fun PuzzleBody(
    vm: LibretaViewModel,
    theme: Theme,
    puzzle: Puzzle,
    current: CurrentPuzzle,
    modifier: Modifier,
) {
    val view = LocalView.current
    val sound = remember { FoundSound() }
    var hint by remember { mutableStateOf<Pair<String, Cell>?>(null) }
    var cursor by remember { mutableIntStateOf(0) }
    val done = current.completed || current.found.size >= current.total

    fun unfound() = puzzle.placed.filter { placed -> current.found.none { it.word == placed.word } }

    fun announce(word: String, finishing: Boolean) {
        val text = if (finishing) "${Copy.found(word)}. ${Copy.finished}" else Copy.found(word)
        view.announceForAccessibility(text)
    }

    fun commit(cells: List<Cell>): CommitResult {
        val hit = findHit(puzzle.grid, cells, puzzle.placed) ?: return CommitResult.Miss
        if (current.found.any { it.word == hit.word }) return CommitResult.Already
        val finishing = current.found.size + 1 >= current.total
        announce(hit.display, finishing)
        if (vm.settings.sound) sound.play()
        if (hint?.first == hit.word) hint = null
        vm.mark(hit.word, cells, revealed = false)
        return CommitResult.Found
    }

    fun showHint() {
        val pending = unfound()
        if (pending.isEmpty()) return
        val word = pending[cursor % pending.size]
        cursor += 1
        val cell = word.cells.firstOrNull() ?: return
        hint = word.word to cell
        view.announceForAccessibility(Copy.hintFor(word.display))
    }

    fun revealOne() {
        val pending = unfound()
        if (pending.isEmpty()) return
        val hinted = hint?.let { active -> pending.find { it.word == active.first } }
        val word = hinted ?: pending.first()
        val finishing = current.found.size + 1 >= current.total
        announce(word.display, finishing)
        hint = null
        vm.mark(word.word, word.cells, revealed = true)
    }

    val strokes = current.found.mapNotNull { item ->
        val mark = markFor(current.found, item.word) ?: return@mapNotNull null
        DrawnStroke(item.word, item.cells, mark.stroke, mark.pencil)
    }

    Column(modifier.fillMaxSize().background(Desk).padding(16.dp)) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(Paper)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(theme.name, fontFamily = Fraunces, fontSize = 36.sp, color = Ink)
                    Text(Copy.howTo, fontFamily = Atkinson, fontSize = 16.sp, color = InkSoft)
                }
                Text(
                    if (done) Copy.veryWell else Copy.progress(current.found.size, current.total),
                    fontFamily = Atkinson,
                    fontSize = 28.sp,
                    color = Ink,
                )
            }
            Row(Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp)) {
                BoxWithConstraints(Modifier.weight(1.15f).fillMaxHeight()) {
                    val side = minOf(maxWidth, maxHeight)
                    Box(Modifier.size(side).align(Alignment.Center)) {
                        GridBoard(
                            letters = puzzle.grid,
                            strokes = strokes,
                            hint = hint?.second,
                            mode = current.directions,
                            onCommit = ::commit,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                WordList(
                    puzzle = puzzle,
                    current = current,
                    modifier = Modifier.weight(0.85f).fillMaxHeight().padding(start = 18.dp),
                )
            }
        }
        if (done) {
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PaperButton(Copy.anotherFull, vm::another, Modifier.weight(1.4f), emphasized = true)
                PaperButton(Copy.pickTheme, vm::pickTheme, Modifier.weight(1f))
            }
        } else {
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PaperButton(Copy.home, vm::goHome, Modifier.weight(1f))
                PaperButton(Copy.hint, ::showHint, Modifier.weight(1f))
                PaperButton(Copy.reveal, ::revealOne, Modifier.weight(1.3f))
                PaperButton(Copy.another, vm::another, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun WordList(puzzle: Puzzle, current: CurrentPuzzle, modifier: Modifier) {
    val found = current.found.associateBy { it.word }
    Column(
        modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
    ) {
        for (word in puzzle.placed) {
            val mark = found[word.word]?.let { markFor(current.found, word.word) }
            val description = if (mark == null) word.display else "${word.display}, encontrada"
            Box(
                Modifier
                    .padding(vertical = 2.dp)
                    .background(mark?.wash ?: Paper, RoundedCornerShape(8.dp))
                    .semantics { contentDescription = description }
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(word.display, fontFamily = Atkinson, fontSize = 26.sp, color = Ink)
                if (mark != null) {
                    Canvas(Modifier.matchParentSize()) {
                        val y = size.height / 2f
                        drawLine(
                            color = mark.strike,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 4f,
                        )
                    }
                }
            }
        }
    }
}

