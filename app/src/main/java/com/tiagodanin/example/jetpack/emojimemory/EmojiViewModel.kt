package com.tiagodanin.example.jetpack.emojimemory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EmojiViewModel : ViewModel() {
    private val emojis: MutableLiveData<List<EmojiModel>> by lazy {
        MutableLiveData<List<EmojiModel>>()
    }

    fun getEmojis(): LiveData<List<EmojiModel>> = emojis

    fun loadEmojis() {
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
        val selects = current.filter { it.isSelect }
        val selectCount = selects.size
        val charFind: String =
            if (selectCount >= 2 && selects[0].char == selects[1].char) selects[0].char else ""

        val updated = current.map { item ->
            var next = item
            if (selectCount >= 2) {
                next = next.copy(isSelect = false)
            }
            if (next.char == charFind && charFind.isNotEmpty()) {
                next = next.copy(isVisible = false)
            }
            if (next.id == id && next.isVisible) {
                next = next.copy(isSelect = true)
            }
            next
        }

        if (updated.none { it.isVisible }) {
            loadEmojis()
            return
        }

        emojis.value = updated
    }
}
