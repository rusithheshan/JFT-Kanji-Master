package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.KanjiDatabase
import com.example.data.KanjiEntity
import com.example.data.KanjiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class GoogleProfile(
    val email: String,
    val displayName: String,
    val photoUrl: String? = null
)

sealed interface AuthState {
    object Unauthenticated : AuthState
    object Loading : AuthState
    data class Authenticated(val profile: GoogleProfile) : AuthState
}

class KanjiViewModel(application: Application) : AndroidViewModel(application) {

    private val database = KanjiDatabase.getDatabase(application, viewModelScope)
    private val repository = KanjiRepository(database.kanjiDao())

    // --- Authentication State ---
    private val sharedPrefs = application.getSharedPreferences("kanji_auth", Context.MODE_PRIVATE)
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // --- Study State Selection ---
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _progressFilter = MutableStateFlow("All") // "All", "Learning", "Learned"
    val progressFilter: StateFlow<String> = _progressFilter.asStateFlow()

    // Reactive list of kanji cards filtered by category & progress
    val kanjiSet: StateFlow<List<KanjiEntity>> = combine(
        repository.allCards,
        _selectedCategory,
        _progressFilter
    ) { cards, category, progress ->
        var filtered = cards
        if (category != "All") {
            filtered = filtered.filter { it.category == category }
        }
        if (progress == "Learning") {
            filtered = filtered.filter { !it.isLearned }
        } else if (progress == "Learned") {
            filtered = filtered.filter { it.isLearned }
        }
        filtered
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Learned cards count
    val learnedCount: StateFlow<Int> = repository.learnedCount.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    // Current open card in Detail View
    private val _selectedCard = MutableStateFlow<KanjiEntity?>(null)
    val selectedCard: StateFlow<KanjiEntity?> = _selectedCard.asStateFlow()

    // --- Check Kanji Knowledge State ---
    private val _quizIndex = MutableStateFlow(0)
    val quizIndex: StateFlow<Int> = _quizIndex.asStateFlow()

    private val _quizIsRevealed = MutableStateFlow(false)
    val quizIsRevealed: StateFlow<Boolean> = _quizIsRevealed.asStateFlow()

    init {
        // Automatically check last Google login
        checkSavedAuth()
    }

    private fun checkSavedAuth() {
        val isLoggedIn = sharedPrefs.getBoolean("is_logged_in", false)
        if (isLoggedIn) {
            val email = sharedPrefs.getString("user_email", "rusithheshan12345@gmail.com") ?: "rusithheshan12345@gmail.com"
            val name = sharedPrefs.getString("user_name", "Rusith Heshan") ?: "Rusith Heshan"
            _authState.value = AuthState.Authenticated(
                GoogleProfile(email = email, displayName = name)
            )
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun loginWithGoogle(email: String, name: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            kotlinx.coroutines.delay(800) // Simulated delay for professional native feel
            sharedPrefs.edit()
                .putBoolean("is_logged_in", true)
                .putString("user_email", email)
                .putString("user_name", name)
                .apply()
            _authState.value = AuthState.Authenticated(
                GoogleProfile(email = email, displayName = name)
            )
        }
    }

    fun logout() {
        sharedPrefs.edit().clear().apply()
        _authState.value = AuthState.Unauthenticated
    }

    // --- Card Methods ---
    fun selectCard(card: KanjiEntity?) {
        _selectedCard.value = card
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setProgressFilter(filter: String) {
        _progressFilter.value = filter
    }

    // Action learned toggles
    fun markAsLearned(cardId: Int) {
        viewModelScope.launch {
            repository.updateProgress(cardId, true)
            // Update selected card state to match in detail screen
            if (_selectedCard.value?.id == cardId) {
                _selectedCard.value = _selectedCard.value?.copy(isLearned = true)
            }
        }
    }

    fun markAsLearning(cardId: Int) {
        viewModelScope.launch {
            repository.updateProgress(cardId, false)
            // Update selected card state to match in detail screen
            if (_selectedCard.value?.id == cardId) {
                _selectedCard.value = _selectedCard.value?.copy(isLearned = false)
            }
        }
    }

    fun toggleFavorite(cardId: Int, isFav: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(cardId, isFav)
            if (_selectedCard.value?.id == cardId) {
                _selectedCard.value = _selectedCard.value?.copy(isFavorite = isFav)
            }
        }
    }

    // --- Quiz Knowledge Methods ---
    fun setQuizIndex(index: Int) {
        _quizIndex.value = index
        _quizIsRevealed.value = false
    }

    fun revealQuizSolution() {
        _quizIsRevealed.value = true
    }

    fun hideQuizSolution() {
        _quizIsRevealed.value = false
    }

    fun nextQuiz(cardsSize: Int) {
        if (cardsSize > 0) {
            _quizIndex.value = (_quizIndex.value + 1) % cardsSize
            _quizIsRevealed.value = false
        }
    }

    fun prevQuiz(cardsSize: Int) {
        if (cardsSize > 0) {
            _quizIndex.value = if (_quizIndex.value - 1 < 0) cardsSize - 1 else _quizIndex.value - 1
            _quizIsRevealed.value = false
        }
    }
}
