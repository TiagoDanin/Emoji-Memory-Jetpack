package com.tiagodanin.example.jetpack.emojimemory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EmojiViewModel : ViewModel() {
    private val emojis = MutableLiveData<List<EmojiModel>>()
    private val won = MutableLiveData(false)

    fun getEmojis(): LiveData<List<EmojiModel>> = emojis
    fun getWon(): LiveData<Boolean> = won

    fun loadEmojis() {
        won.value = false
        emojis.value = listOf(
            EmojiModel("😍"),
            EmojiModel("🥰"),
            EmojiModel("😘"),
            EmojiModel("😭"),
            EmojiModel("😢"),
            EmojiModel("😂"),
            EmojiModel("😍"),
            EmojiModel("🥰"),
            EmojiModel("😘"),
            EmojiModel("😭"),
            EmojiModel("😢"),
            EmojiModel("😂"),
        ).shuffled()
    }

    fun updateShowVisibleCard(id: String) {
        val current = emojis.value ?: return
        val alreadySelected = current.count { it.isSelect }
        if (alreadySelected >= 2) return

        val afterClick = current.map { item ->
            if (item.id == id && item.isVisible && !item.isSelect) {
                item.copy(isSelect = true)
            } else item
        }
        emojis.value = afterClick

        val selected = afterClick.filter { it.isSelect }
        if (selected.size < 2) return

        val isMatch = selected[0].char == selected[1].char
        val matchedChar = selected[0].char

        viewModelScope.launch {
            delay(700)
            val resolved = afterClick.map { item ->
                when {
                    isMatch && item.char == matchedChar ->
                        item.copy(isSelect = false, isVisible = false)
                    item.isSelect -> item.copy(isSelect = false)
                    else -> item
                }
            }
            emojis.value = resolved

            if (resolved.none { it.isVisible }) {
                won.value = true
            }
        }
    }
}
