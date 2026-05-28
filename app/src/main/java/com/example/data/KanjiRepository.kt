package com.example.data

import kotlinx.coroutines.flow.Flow

class KanjiRepository(private val kanjiDao: KanjiDao) {

    val allCards: Flow<List<KanjiEntity>> = kanjiDao.getAllCards()
    val learnedCount: Flow<Int> = kanjiDao.getLearnedCount()

    fun getCardsByCategory(category: String): Flow<List<KanjiEntity>> {
        return kanjiDao.getCardsByCategory(category)
    }

    fun getCardsByProgress(isLearned: Boolean): Flow<List<KanjiEntity>> {
        return kanjiDao.getCardsByProgress(isLearned)
    }

    suspend fun updateProgress(id: Int, isLearned: Boolean) {
        kanjiDao.updateProgress(id, isLearned)
    }

    suspend fun updateFavorite(id: Int, isFavorite: Boolean) {
        kanjiDao.updateFavorite(id, isFavorite)
    }

    suspend fun insertCard(card: KanjiEntity) {
        kanjiDao.insertCard(card)
    }
}
