package com.creativeblisslabs.nitnemguktasteek.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.creativeblisslabs.nitnemguktasteek.app.models.DisplayLanguage
import com.creativeblisslabs.nitnemguktasteek.app.ui.theme.*
import com.creativeblisslabs.nitnemguktasteek.models.BaniLine
import com.creativeblisslabs.nitnemguktasteek.utils.translate

enum class SearchCriterion(val label: String, val badge: String) {
    FirstLetter("First Letter (Gurmukhi)", "ੳਅ"),
    FullGurmukhi("Full Gurmukhi", "ਗੁਰ"),
    EnglishTranslation("English Translation", "EN"),
    EnglishTransliteration("English Roman", "ABC"),
    PunjabiTeeka("Punjabi Teeka", "ਪੰ")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedGurbaniSearchModal(
    lines: List<BaniLine>,
    baniTitle: String,
    displayLanguage: DisplayLanguage,
    onLineSelected: (BaniLine, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCriterion by remember { mutableStateOf(SearchCriterion.FullGurmukhi) }

    val filteredResults = remember(searchQuery, selectedCriterion, lines) {
        val query = searchQuery.trim().lowercase()
        if (query.isEmpty()) {
            emptyList<Pair<BaniLine, Int>>()
        } else {
            lines.mapIndexedNotNull { index, line ->
                val matchesQuery = when (selectedCriterion) {
                    SearchCriterion.FirstLetter -> {
                        val firstLetters = line.gurmukhi.split(" ")
                            .filter { it.isNotBlank() }
                            .map { it.first().lowercaseChar() }
                            .joinToString("")
                        firstLetters.contains(query)
                    }
                    SearchCriterion.FullGurmukhi -> {
                        line.gurmukhi.lowercase().contains(query)
                    }
                    SearchCriterion.EnglishTranslation -> {
                        line.translation.lowercase().contains(query)
                    }
                    SearchCriterion.EnglishTransliteration -> {
                        line.transliteration.lowercase().contains(query)
                    }
                    SearchCriterion.PunjabiTeeka -> {
                        line.punjabi.lowercase().contains(query)
                    }
                }
                if (matchesQuery) Pair(line, index) else null
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceColor,
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Advanced Gurbani Search".translate(displayLanguage),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                    Text(
                        text = "$baniTitle • ${lines.size} ${"Lines".translate(displayLanguage)}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Accent.copy(alpha = 0.12f),
                    onClick = onDismiss
                ) {
                    Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Accent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Search Text Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = when (selectedCriterion) {
                            SearchCriterion.FirstLetter -> "Search by first letters (e.g. ੴ ਸ ਨ)..."
                            SearchCriterion.FullGurmukhi -> "Search Gurmukhi words (e.g. ਸਤਿਨਾਮੁ)..."
                            SearchCriterion.EnglishTranslation -> "Search English meaning (e.g. True Name)..."
                            SearchCriterion.EnglishTransliteration -> "Search Roman text (e.g. Satnam)..."
                            SearchCriterion.PunjabiTeeka -> "Search Punjabi translation..."
                        },
                        fontSize = 13.sp,
                        color = TextSecondary.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Gold
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Gold,
                    unfocusedBorderColor = AccentBorder,
                    focusedContainerColor = SurfaceVariant.copy(alpha = 0.35f),
                    unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.20f)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            // Search Criteria Selector Tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(SearchCriterion.values()) { criterion ->
                    val selected = selectedCriterion == criterion
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) Gold.copy(alpha = 0.18f) else SurfaceVariant.copy(alpha = 0.5f),
                        border = BorderStroke(
                            1.dp,
                            if (selected) Gold else Color.Transparent
                        ),
                        onClick = { selectedCriterion = criterion }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = criterion.badge,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (selected) Gold else TextSecondary
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = criterion.label,
                                fontSize = 11.5.sp,
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selected) Primary else TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Results count
            if (searchQuery.isNotBlank()) {
                Text(
                    text = "${filteredResults.size} ${"matches found".translate(displayLanguage)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Gold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            // Results List
            if (filteredResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = TextSecondary.copy(alpha = 0.4f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isEmpty()) "Type above to search across this Bani".translate(displayLanguage)
                            else "No matching lines found".translate(displayLanguage),
                            fontSize = 13.sp,
                            color = TextSecondary.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredResults) { (line, index) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onLineSelected(line, index)
                                    onDismiss()
                                },
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, AccentBorder)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${"Line".translate(displayLanguage)} ${index + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Gold
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Jump",
                                        tint = Gold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = line.gurmukhi,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Primary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                if (line.transliteration.isNotBlank()) {
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = line.transliteration,
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                if (line.translation.isNotBlank()) {
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = line.translation,
                                        fontSize = 12.sp,
                                        color = TextPrimary.copy(alpha = 0.8f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
