package com.tiagodanin.example.jetpack.emojimemory

import java.util.UUID

data class EmojiModel(
    val char: String,
    val isVisible: Boolean = true,
    val isSelect: Boolean = false,
    val id: String = UUID.randomUUID().toString(),
)
