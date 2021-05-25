package com.tiagodanin.example.jetpack.emojimemory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.Icon
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tiagodanin.example.jetpack.emojimemory.ui.theme.EmojiMemoryJetpackTheme

class MainActivity : ComponentActivity() {
    private val viewModel: EmojiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadEmojis()

        setContent {
            EmojiMemoryJetpackTheme {
                MainContent()
            }
        }
    }

    @Composable
    private fun MainContent() {
        Scaffold(
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
        ) {
            val cards: List<EmojiModel> by viewModel.getEmojis().observeAsState(listOf())
            CardsGrid(cards = cards)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun CardsGrid(cards: List<EmojiModel>) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(4)
        ) {
            items(cards.count()) { cardIndex ->
                CardItem(cards[cardIndex])
            }
        }
    }

    @Composable
    private fun CardItem(emoji: EmojiModel) {
        Box(
            modifier = Modifier
                .padding(all = 10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .size(150.dp)
                    .background(
                        color = Color.Black.copy(alpha = if (emoji.isVisible) 0.4F else 0.0F),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        if (emoji.isVisible) {
                            viewModel.updateShowVisibleCard(emoji.id)
                        }
                    }

            ) {
                if (emoji.isSelect) {
                    Text(
                        text = emoji.char,
                        fontSize = 32.sp
                    )
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

