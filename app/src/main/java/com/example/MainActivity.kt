package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.KanjiEntity
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AuthState
import com.example.viewmodel.GoogleProfile
import com.example.viewmodel.KanjiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val viewModel: KanjiViewModel = viewModel()
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AnimatedContent(
            targetState = authState,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
            },
            label = "auth_transition"
        ) { state ->
            when (state) {
                is AuthState.Unauthenticated -> {
                    LoginScreen(
                        onLoginSuccess = { email, name ->
                            viewModel.loginWithGoogle(email, name)
                        }
                    )
                }
                is AuthState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = Color(0xFFD32F2F),
                                strokeWidth = 5.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "ගිණුමට සම්බන්ධ වෙමින්...",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                is AuthState.Authenticated -> {
                    DashboardScreen(
                        profile = state.profile,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (String, String) -> Unit) {
    var emailInput by remember { mutableStateFlowOf("rusithheshan12345@gmail.com") }
    var nameInput by remember { mutableStateFlowOf("Rusith Heshan") }
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF1F1),
                        Color(0xFFFFE5E5),
                        Color.White
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 480.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Draw Beautiful Red Sun Rising Symbol
            Canvas(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
            ) {
                // Red Sun circle
                drawCircle(
                    color = Color(0xFFD32F2F),
                    radius = size.minDimension / 2.5f
                )
                // Sun corona rays
                drawCircle(
                    color = Color(0xFFD32F2F).copy(alpha = 0.15f),
                    radius = size.minDimension / 1.8f,
                    style = Stroke(width = 8f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Brand Titles
            Text(
                text = "Japan අත්වැල",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFD32F2F),
                fontFamily = FontFamily.Serif,
                textAlign = TextAlign.Center
            )

            Text(
                text = "JFT Exam Kanji Instructor",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF37474F),
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Explanation Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "සම්පූර්ණ JFT විභාග කන්ජි කාඩ්පත් 448ම සිංහල සහ ඉංග්‍රීසි අර්ථ සහිතව මෙහි අඩංගු වේ.",
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        color = Color(0xFF455A64),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "ඔබේ ඉගෙනීමේ ප්‍රගතිය මනාව සුරැකීමට කරුණාකර පළමුව Google ගිණුමෙන් සම්බන්ධ වන්න.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = Color(0xFF78909c),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Button
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("google_signin_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Google Sign In",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Google ගිණුමෙන් ඇතුල් වන්න",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }

    // Interactive custom SSO login sheet
    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .widthIn(max = 400.dp)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Google Sign In (Simulated)",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF202124)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "accounts.google.com",
                            fontSize = 12.sp,
                            color = Color(0xFF5F6368)
                        )
                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        // Email Field
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Google Email Addr") },
                            modifier = Modifier.fillMaxWidth().testTag("email_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Name Field
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Display Name") },
                            modifier = Modifier.fillMaxWidth().testTag("name_input"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Cancel", color = Color(0xFF5F6368))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    showDialog = false
                                    onLoginSuccess(emailInput, nameInput)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Log In", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Custom remember stateFlow binder helper
fun <T> mutableStateFlowOf(value: T) = mutableStateOf(value)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    profile: GoogleProfile,
    viewModel: KanjiViewModel
) {
    var activeTab by remember { mutableStateOf("LEARN") } // "LEARN" vs "CHECK"

    val progressCards by viewModel.kanjiSet.collectAsStateWithLifecycle()
    val totalCount = 448 // 448 in native list scope
    val learnedCount by viewModel.learnedCount.collectAsStateWithLifecycle()
    val selectedCard by viewModel.selectedCard.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "JFT Kanji Master",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color(0xFFD32F2F)
                        )
                        Text(
                            text = profile.displayName,
                            fontSize = 12.sp,
                            color = Color(0xFF5F6368),
                            fontWeight = FontWeight.Medium
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.testTag("logout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Log out",
                            tint = Color(0xFFD32F2F)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFF5F5)
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                modifier = Modifier.navigationBarsPadding()
            ) {
                NavigationBarItem(
                    selected = activeTab == "LEARN",
                    onClick = { activeTab = "LEARN" },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Learn JFT Kanji", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFD32F2F),
                        selectedTextColor = Color(0xFFD32F2F),
                        indicatorColor = Color(0xFFFFF1F1)
                    )
                )
                NavigationBarItem(
                    selected = activeTab == "CHECK",
                    onClick = { activeTab = "CHECK" },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
                    label = { Text("Check Knowledge", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFD32F2F),
                        selectedTextColor = Color(0xFFD32F2F),
                        indicatorColor = Color(0xFFFFF1F1)
                    )
                )
            }
        },
        containerColor = Color(0xFFFAFAFA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Native progress header card
            CircularProgressHeader(
                learned = learnedCount,
                total = totalCount
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (activeTab == "LEARN") {
                LearnKanjiView(
                    viewModel = viewModel,
                    cards = progressCards,
                    onCardClick = { viewModel.selectCard(it) }
                )
            } else {
                CheckKnowledgeView(
                    viewModel = viewModel,
                    allCards = progressCards
                )
            }
        }
    }

    // Active Card detailed sheet dialog overlay
    if (selectedCard != null) {
        KanjiDetailOverlay(
            card = selectedCard!!,
            onClose = { viewModel.selectCard(null) },
            onMarkOk = { 
                viewModel.markAsLearned(it)
                viewModel.selectCard(null) // auto dismiss on state change
            },
            onMarkNotYet = { 
                viewModel.markAsLearning(it)
                viewModel.selectCard(null) // auto dismiss on state change
            },
            onToggleFavorite = { id, fav -> viewModel.toggleFavorite(id, fav) }
        )
    }
}

@Composable
fun CircularProgressHeader(learned: Int, total: Int) {
    val percent = if (total > 0) (learned.toFloat() / total.toFloat() * 100f) else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(60.dp)
            ) {
                CircularProgressIndicator(
                    progress = if (total > 0) learned.toFloat() / total.toFloat() else 0f,
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFE8F5E9),
                    strokeWidth = 6.dp,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = String.format("%.0f%%", percent),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF2E7D32)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ඉගෙනීමේ ප්‍රගතිය (JFT Progress)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF37474F)
                )
                Text(
                    text = "$learned / $total Cards Mastered (ප්‍රගුණ කළා)",
                    fontSize = 13.sp,
                    color = Color(0xFF78909c),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun LearnKanjiView(
    viewModel: KanjiViewModel,
    cards: List<KanjiEntity>,
    onCardClick: (KanjiEntity) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val progressFilter by viewModel.progressFilter.collectAsStateWithLifecycle()
    var searchKeyword by remember { mutableStateOf("") }

    val categories = listOf("All", "Irodori Starter", "Irodori Elementary 1", "Irodori Elementary 2")
    val filters = listOf(
        FilterChipData("All", "සියල්ල"),
        FilterChipData("Learning", "තමන ඉගෙන ගන්නවා"),
        FilterChipData("Learned", "ප්‍රගුණ කළා")
    )

    // Filter by search text locally
    val displayedCards = remember(cards, searchKeyword) {
        if (searchKeyword.isBlank()) {
            cards
        } else {
            val kw = searchKeyword.lowercase().trim()
            cards.filter {
                it.kanji.contains(kw) ||
                        it.reading.contains(kw) ||
                        it.englishMeaning.lowercase().contains(kw) ||
                        it.sinhalaMeaning.contains(kw)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search & Filter Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(bottom = 8.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchKeyword,
                onValueChange = { searchKeyword = it },
                placeholder = { Text("කන්ජි, සිංහල හෝ English සෙවීම...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchKeyword.isNotEmpty()) {
                        IconButton(onClick = { searchKeyword = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD32F2F),
                    unfocusedBorderColor = Color(0xFFECEFF1)
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("kanji_search_field")
            )

            // Category tag Row
            Text(
                "Level/පොත තෝරන්න:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF78909c),
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 2.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setCategory(cat) },
                        label = { Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFF1F1),
                            selectedLabelColor = Color(0xFFD32F2F)
                        )
                    )
                }
            }

            // Study state tag Row
            Text(
                "කාණ්ඩය:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF78909c),
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 2.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filters.forEach { flt ->
                    val isSelected = flt.id == progressFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setProgressFilter(flt.id) },
                        label = { Text("${flt.sinhala} (${flt.id})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE8F5E9),
                            selectedLabelColor = Color(0xFF2E7D32)
                        )
                    )
                }
            }
        }

        // List Grid of Kanji cards
        if (displayedCards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFB0BEC5),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "කන්ජි කාඩ්පත් කිසිවක් හමු නොවිය.",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF546E7A)
                    )
                    Text(
                        text = "වෙනත් ශීර්ෂ පදයක් සෙවීමට උත්සාහ කරන්න.",
                        fontSize = 13.sp,
                        color = Color(0xFF90A4AE)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(150.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedCards, key = { it.id }) { card ->
                    KanjiSmallCard(
                        card = card,
                        onClick = {
                            focusManager.clearFocus()
                            onCardClick(card)
                        }
                    )
                }
            }
        }
    }
}

data class FilterChipData(val id: String, val sinhala: String)

@Composable
fun KanjiSmallCard(card: KanjiEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick)
            .testTag("kanji_card_${card.id}"),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Card Top Info: ID, Favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${card.id}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF90A4AE)
                )

                // Colored tag showing success status
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (card.isLearned) Color(0xFF4CAF50) else Color(0xFFFF9800))
                )
            }

            // Kanji word (bold middle)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = card.reading,
                    fontSize = 11.sp,
                    color = Color(0xFF78909c),
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = card.kanji,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF212121),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Card meanings
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = card.sinhalaMeaning,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD32F2F),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = card.englishMeaning,
                    fontSize = 11.sp,
                    color = Color(0xFF546E7A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CheckKnowledgeView(
    viewModel: KanjiViewModel,
    allCards: List<KanjiEntity>
) {
    val quizIndex by viewModel.quizIndex.collectAsStateWithLifecycle()
    val isRevealed by viewModel.quizIsRevealed.collectAsStateWithLifecycle()

    if (allCards.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Quiz එක සඳහා මුලින්ම Kanji ලිස්ට් එකක් තෝරන්න.",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color(0xFF546E7A)
            )
        }
        return
    }

    // Safely wrap index in list bounds
    val currentIdx = if (allCards.isNotEmpty()) quizIndex % allCards.size else 0
    val activeCard = allCards[currentIdx]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top index label
        Text(
            text = "ප්‍රශ්න අංකය: ${currentIdx + 1} / ${allCards.size}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF546E7A),
            modifier = Modifier.padding(top = 8.dp)
        )

        // Large Quiz Flashcard
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp)
                .clickable {
                    if (isRevealed) {
                        viewModel.hideQuizSolution()
                    } else {
                        viewModel.revealQuizSolution()
                    }
                }
                .testTag("quiz_flashcard"),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.5.dp, Color(0xFFFFF1F1))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!isRevealed) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Secret Canvas Background Design for mystery
                        Canvas(modifier = Modifier.size(100.dp)) {
                            drawCircle(
                                color = Color(0xFFECEFF1),
                                radius = size.minDimension / 2.2f,
                                style = Stroke(width = 4f)
                            )
                            drawPath(
                                path = Path().apply {
                                    moveTo(size.width * 0.4f, size.height * 0.3f)
                                    lineTo(size.width * 0.6f, size.height * 0.5f)
                                    lineTo(size.width * 0.4f, size.height * 0.7f)
                                },
                                color = Color(0xFFB0BEC5),
                                style = Stroke(width = 6f)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Only Kanji Word in middle (Bold layout)
                        Text(
                            text = activeCard.kanji,
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF212121),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "අර්ථය බැලීමට මෙහි ක්ලික් කරන්න (Click to reveal)",
                            fontSize = 12.sp,
                            color = Color(0xFF90A4AE),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    // Revealed State displays full Detailed Card parameters
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Hiragana Above
                        Text(
                            text = activeCard.reading,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78909c),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Large Kanji
                        Text(
                            text = activeCard.kanji,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF212121),
                            textAlign = TextAlign.Center
                        )

                        // Mnemonic illustration placeholder
                        Spacer(modifier = Modifier.height(12.dp))
                        KanjiMnemonicSymbolView(kanji = activeCard.kanji)

                        Spacer(modifier = Modifier.height(12.dp))

                        // Onyomi & Kunyomi
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Onyomi (චීන කියවීම)", fontSize = 10.sp, color = Color(0xFF90A4AE), fontWeight = FontWeight.Bold)
                                Text(activeCard.onyomi.ifEmpty { "-" }, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF37474F))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Kunyomi (ජපන් කියවීම)", fontSize = 10.sp, color = Color(0xFF90A4AE), fontWeight = FontWeight.Bold)
                                Text(activeCard.kunyomi.ifEmpty { "-" }, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color(0xFF37474F))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Sinhala meaning
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFFF1F1), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text("සිංහල තේරුම:", fontSize = 11.sp, color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                            Text(
                                text = activeCard.sinhalaMeaning,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD32F2F),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // English meaning
                        Text("English Meaning:", fontSize = 10.sp, color = Color(0xFF90A4AE), fontWeight = FontWeight.Bold)
                        Text(
                            text = activeCard.englishMeaning,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF37474F),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Progress actions: OK and NOT YET
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = { viewModel.markAsLearning(activeCard.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("NOT YET", fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { viewModel.markAsLearned(activeCard.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("OK (සුදානම්)", fontWeight = FontWeight.Black, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // NEXT & PREVIOUS Action row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.prevQuiz(allCards.size) },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF37474F)),
                border = BorderStroke(1.5.dp, Color(0xFFCFD8DC)),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("prev_quiz_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Prev")
                Spacer(modifier = Modifier.width(8.dp))
                Text("PREVIOUS", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.nextQuiz(allCards.size) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("next_quiz_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("NEXT", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Default.ArrowForward, contentDescription = "Next")
            }
        }
    }
}

@Composable
fun KanjiMnemonicSymbolView(kanji: String) {
    Canvas(
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp)
    ) {
        val w = size.width
        val h = size.height

        when {
            kanji.contains("水") -> {
                // Drop 1
                drawPath(
                    path = Path().apply {
                        moveTo(w * 0.5f, h * 0.15f)
                        quadraticBezierTo(w * 0.25f, h * 0.65f, w * 0.25f, h * 0.75f)
                        quadraticBezierTo(w * 0.25f, h * 0.9f, w * 0.5f, h * 0.9f)
                        quadraticBezierTo(w * 0.75f, h * 0.9f, w * 0.75f, h * 0.75f)
                        quadraticBezierTo(w * 0.75f, h * 0.65f, w * 0.5f, h * 0.15f)
                    },
                    color = Color(0xFF29B6F6)
                )
            }
            kanji.contains("日本") || kanji == "国" -> {
                // Red rising sun
                drawCircle(color = Color(0xFFC62828), radius = w * 0.35f)
                drawCircle(color = Color(0xFFE53935).copy(alpha = 0.3f), radius = w * 0.45f, style = Stroke(6f))
            }
            kanji.contains("父") -> {
                // Gentle Slate Tie/Collar
                val collar = Path().apply {
                    moveTo(w * 0.2f, h * 0.3f)
                    lineTo(w * 0.5f, h * 0.5f)
                    lineTo(w * 0.8f, h * 0.3f)
                }
                drawPath(collar, color = Color(0xFF546E7A), style = Stroke(6f))
                val tie = Path().apply {
                    moveTo(w * 0.45f, h * 0.5f)
                    lineTo(w * 0.55f, h * 0.5f)
                    lineTo(w * 0.58f, h * 0.8f)
                    lineTo(w * 0.5f, h * 0.9f)
                    lineTo(w * 0.42f, h * 0.8f)
                    close()
                }
                drawPath(tie, color = Color(0xFF37474F))
            }
            kanji.contains("母") -> {
                // Heart shape
                drawPath(
                    path = Path().apply {
                        moveTo(w * 0.5f, h * 0.35f)
                        cubicTo(w * 0.2f, h * 0.1f, w * 0.05f, h * 0.5f, w * 0.5f, h * 0.85f)
                        cubicTo(w * 0.95f, h * 0.5f, w * 0.8f, h * 0.1f, w * 0.5f, h * 0.35f)
                    },
                    color = Color(0xFFEC407A)
                )
            }
            kanji.contains("日") -> {
                // Shining Golden Sun
                drawCircle(color = Color(0xFFFFB300), radius = w * 0.25f)
                for (i in 0 until 8) {
                    val angle = i * (Math.PI / 4)
                    val startX = (w * 0.5f + Math.cos(angle) * (w * 0.32f)).toFloat()
                    val startY = (h * 0.5f + Math.sin(angle) * (h * 0.32f)).toFloat()
                    val endX = (w * 0.5f + Math.cos(angle) * (w * 0.46f)).toFloat()
                    val endY = (h * 0.5f + Math.sin(angle) * (h * 0.46f)).toFloat()
                    drawLine(color = Color(0xFFFFB300), start = Offset(startX, startY), end = Offset(endX, endY), strokeWidth = 6f)
                }
            }
            kanji.contains("月") -> {
                // Moon
                drawPath(
                    path = Path().apply {
                        moveTo(w * 0.4f, h * 0.2f)
                        cubicTo(w * 0.75f, h * 0.2f, w * 0.75f, h * 0.8f, w * 0.4f, h * 0.8f)
                        cubicTo(w * 0.55f, h * 0.7f, w * 0.55f, h * 0.3f, w * 0.4f, h * 0.2f)
                    },
                    color = Color(0xFF90A4AE)
                )
            }
            kanji.contains("火") -> {
                // Flame
                drawPath(
                    path = Path().apply {
                        moveTo(w * 0.5f, h * 0.15f)
                        quadraticBezierTo(w * 0.35f, h * 0.55f, w * 0.3f, h * 0.75f)
                        quadraticBezierTo(w * 0.35f, h * 0.9f, w * 0.5f, h * 0.9f)
                        quadraticBezierTo(w * 0.65f, h * 0.9f, w * 0.7f, h * 0.75f)
                        quadraticBezierTo(w * 0.65f, h * 0.55f, w * 0.5f, h * 0.15f)
                    },
                    color = Color(0xFFFF7043)
                )
                drawPath(
                    path = Path().apply {
                        moveTo(w * 0.5f, h * 0.4f)
                        quadraticBezierTo(w * 0.4f, h * 0.65f, w * 0.38f, h * 0.75f)
                        quadraticBezierTo(w * 0.42f, h * 0.85f, w * 0.5f, h * 0.85f)
                        quadraticBezierTo(w * 0.58f, h * 0.85f, w * 0.62f, h * 0.75f)
                        quadraticBezierTo(w * 0.6f, h * 0.65f, w * 0.5f, h * 0.4f)
                    },
                    color = Color(0xFFFFCA28)
                )
            }
            kanji.contains("木") -> {
                // Simple Tree
                drawRect(color = Color(0xFF8D6E63), topLeft = Offset(w * 0.45f, h * 0.5f), size = Size(w * 0.1f, h * 0.35f))
                drawCircle(color = Color(0xFF66BB6A), radius = w * 0.25f, center = Offset(w * 0.5f, h * 0.35f))
            }
            kanji.contains("金") -> {
                // Golden Diamond
                drawPath(
                    path = Path().apply {
                        moveTo(w * 0.5f, h * 0.2f)
                        lineTo(w * 0.8f, h * 0.5f)
                        lineTo(w * 0.5f, h * 0.8f)
                        lineTo(w * 0.2f, h * 0.5f)
                        close()
                    },
                    color = Color(0xFFFFD54F)
                )
            }
            kanji.contains("土") -> {
                // Earth sprout
                drawArc(color = Color(0xFF795548), startAngle = 0f, sweepAngle = 180f, useCenter = true, topLeft = Offset(w * 0.2f, h * 0.5f), size = Size(w * 0.6f, w * 0.4f))
                drawLine(color = Color(0xFF4CAF50), start = Offset(w * 0.5f, h * 0.5f), end = Offset(w * 0.5f, h * 0.25f), strokeWidth = 6f)
                drawOval(color = Color(0xFF4CAF50), topLeft = Offset(w * 0.48f, h * 0.22f), size = Size(w * 0.15f, h * 0.1f))
            }
            else -> {
                // Nice default Kanji brush symbol
                drawRoundRect(
                    color = Color(0xFFECEFF1),
                    topLeft = Offset(w * 0.2f, h * 0.2f),
                    size = Size(w * 0.6f, h * 0.6f),
                    cornerRadius = CornerRadius(16f, 16f)
                )
                drawLine(
                    color = Color(0xFFD32F2F),
                    start = Offset(w * 0.3f, h * 0.5f),
                    end = Offset(w * 0.7f, h * 0.5f),
                    strokeWidth = 6f
                )
                drawLine(
                    color = Color(0xFFD32F2F),
                    start = Offset(w * 0.5f, h * 0.3f),
                    end = Offset(w * 0.5f, h * 0.7f),
                    strokeWidth = 6f
                )
            }
        }
    }
}

@Composable
fun KanjiDetailOverlay(
    card: KanjiEntity,
    onClose: () -> Unit,
    onMarkOk: (Int) -> Unit,
    onMarkNotYet: (Int) -> Unit,
    onToggleFavorite: (Int, Boolean) -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .widthIn(max = 440.dp)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header control metadata row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Card #${card.id} • ${card.category}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78909c)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onToggleFavorite(card.id, !card.isFavorite) }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Favorite",
                                    tint = if (card.isFavorite) Color(0xFFFFB300) else Color(0xFFCFD8DC)
                                )
                            }
                            IconButton(onClick = onClose) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color(0xFF546E7A)
                                )
                            }
                        }
                    }

                    // Card Content following exactly the user instructions!
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        // Kanjiyata udin kanjiye theruma hiraganawalin (Hiragana above)
                        Text(
                            text = card.reading,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF546E7A),
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Card eke madin lokuwata kanjiya (Large bold Kanji word in center!)
                        Text(
                            text = card.kanji,
                            fontSize = 58.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF111111),
                            fontFamily = FontFamily.Serif
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // custom generated adaptive mnemonic sketch symbol (puluwannam witharak rupayak danna)
                        KanjiMnemonicSymbolView(kanji = card.kanji)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Yatin podiyata onyomi saha kunyomi (Onyomi and Kunyomi details)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFAFAFA), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Onyomi (චීන)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF90A4AE))
                                Text(card.onyomi.ifEmpty { "-" }, fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFF37474F))
                            }
                            Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color(0xFFCFD8DC)))
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Kunyomi (ජපන්)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF90A4AE))
                                Text(card.kunyomi.ifEmpty { "-" }, fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color(0xFF37474F))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Eetath yatin Sinhala akurin sinhala theruma (Sinhala meaning under)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawBehind {
                                    drawRoundRect(
                                        color = Color(0xFFFFF1F1),
                                        size = size,
                                        cornerRadius = CornerRadius(24f)
                                    )
                                }
                                .padding(14.dp)
                        ) {
                            Text(
                                "සිංහල තේරුම",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = card.sinhalaMeaning,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD32F2F),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Eetath yatin english theruma (English meaning at bottom of state)
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "English Meaning",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF90A4AE)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = card.englishMeaning,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF263238),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons: "OK" (Mastered) VS "NOT YET" (Learning)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onMarkNotYet(card.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("not_yet_button"),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "NOT YET",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = { onMarkOk(card.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("ok_button"),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "OK (ප්‍රගුණයි)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
