package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KanjiDao {
    @Query("SELECT * FROM kanji_cards ORDER BY id ASC")
    fun getAllCards(): Flow<List<KanjiEntity>>

    @Query("SELECT * FROM kanji_cards WHERE id = :id")
    suspend fun getCardById(id: Int): KanjiEntity?

    @Query("SELECT * FROM kanji_cards WHERE category = :category ORDER BY id ASC")
    fun getCardsByCategory(category: String): Flow<List<KanjiEntity>>

    @Query("SELECT * FROM kanji_cards WHERE isLearned = :isLearned ORDER BY id ASC")
    fun getCardsByProgress(isLearned: Boolean): Flow<List<KanjiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: KanjiEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<KanjiEntity>)

    @Query("UPDATE kanji_cards SET isLearned = :isLearned WHERE id = :id")
    suspend fun updateProgress(id: Int, isLearned: Boolean)

    @Query("UPDATE kanji_cards SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Query("SELECT COUNT(*) FROM kanji_cards")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM kanji_cards WHERE isLearned = 1")
    fun getLearnedCount(): Flow<Int>
}
