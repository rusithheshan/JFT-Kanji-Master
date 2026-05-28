package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kanji_cards")
data class KanjiEntity(
    @PrimaryKey val id: Int,
    val kanji: String,
    val reading: String,
    val onyomi: String,
    val kunyomi: String,
    val sinhalaMeaning: String,
    val englishMeaning: String,
    val category: String, // "Irodori Starter", "Irodori Elementary 1", "Irodori Elementary 2" etc.
    val isLearned: Boolean = false, // false = "NOT YET", true = "OK"
    val isFavorite: Boolean = false
)
