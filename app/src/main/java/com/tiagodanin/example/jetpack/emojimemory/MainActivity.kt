package com.tiagodanin.example.jetpack.emojimemory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.tiagodanin.example.jetpack.emojimemory.ui.theme.EmojiMemoryJetpackTheme

class MainActivity : ComponentActivity() {
    private val viewModel: EmojiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView)
            .isAppearanceLightStatusBars = false
        viewModel.loadEmojis()

        setContent {
            EmojiMemoryJetpackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.primary
                ) {
                    MainContent()
                }
            }
        }
    }

    @Composable
    private fun MainContent() {
        val tilt by rememberTilt()

        Scaffold(
            modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.app_name))
                    },
                    actions = {
                        IconButton(onClick = { viewModel.loadEmojis() }) {
                            Icon(
                                Icons.Filled.Refresh,
                                contentDescription = "Reload Game"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                val cards: List<EmojiModel> by viewModel.getEmojis().observeAsState(listOf())
                val won: Boolean by viewModel.getWon().observeAsState(false)

                CardsGrid(cards = cards, tilt = tilt)

                AnimatedVisibility(
                    visible = won,
                    enter = fadeIn() + scaleIn(initialScale = 0.6f),
                    exit = fadeOut() + scaleOut(targetScale = 0.6f),
                ) {
                    WinScreen(onPlayAgain = { viewModel.loadEmojis() })
                }
            }
        }
    }

    @Composable
    private fun CardsGrid(cards: List<EmojiModel>, tilt: Offset) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(cards, key = { it.id }) { card ->
                CardItem(card, tilt)
            }
        }
    }

    @Composable
    private fun CardItem(emoji: EmojiModel, tilt: Offset) {
        val faceUp = emoji.isSelect || !emoji.isVisible
        val rotation by animateFloatAsState(
            targetValue = if (faceUp) 180f else 0f,
            animationSpec = tween(durationMillis = 600),
            label = "flip"
        )
        val alpha by animateFloatAsState(
            targetValue = if (emoji.isVisible) 1f else 0.25f,
            animationSpec = tween(durationMillis = 400),
            label = "alpha"
        )
        val density = LocalDensity.current.density

        Box(modifier = Modifier.padding(6.dp)) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .graphicsLayer {
                        rotationY = rotation
                        cameraDistance = 12f * density
                        this.alpha = alpha
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(enabled = emoji.isVisible && !emoji.isSelect) {
                        viewModel.updateShowVisibleCard(emoji.id)
                    }
            ) {
                if (rotation <= 90f) {
                    CardBack(tilt = tilt)
                } else {
                    CardFront(emoji = emoji, tilt = tilt)
                }
            }
        }
    }

    @Composable
    private fun CardBack(tilt: Offset) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2A2D34))
        ) {
            HoloOverlay(tilt = tilt)
        }
    }

    @Composable
    private fun CardFront(emoji: EmojiModel, tilt: Offset) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationY = 180f }
                .background(Color(0xFF1F2128)),
            contentAlignment = Alignment.Center,
        ) {
            HoloOverlay(tilt = tilt)
            Text(
                text = emoji.char,
                fontSize = 48.sp,
            )
        }
    }

    @Composable
    private fun HoloOverlay(tilt: Offset) {
        val shift = 400f
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0x33B0B7C3),
                            Color(0x33E8ECF1),
                            Color(0x338FA1B8),
                            Color(0x33C9B89A),
                            Color(0x33A8B5C8),
                        ),
                        start = Offset(tilt.x * shift, tilt.y * shift),
                        end = Offset(tilt.x * shift + 500f, tilt.y * shift + 500f),
                    )
                )
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.18f),
                            Color.Transparent,
                        ),
                        center = Offset(
                            x = 180f + tilt.x * 180f,
                            y = 180f + tilt.y * 180f,
                        ),
                        radius = 220f,
                    )
                )
        )
    }

    @Composable
    private fun WinScreen(onPlayAgain: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "🎉",
                    fontSize = 96.sp,
                )
                Text(
                    text = "You won!",
                    color = Color.White,
                    fontSize = 32.sp,
                    modifier = Modifier.padding(top = 16.dp),
                )
                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier.padding(top = 24.dp),
                ) {
                    Text(text = "Play again")
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun DefaultPreview() {
        EmojiMemoryJetpackTheme {
            MainContent()
        }
    }
}
