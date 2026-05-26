package org.audiocular.nitnem.app.ui.practice

import org.audiocular.nitnem.app.ui.practice.PracticeColors
import org.audiocular.nitnem.app.ui.practice.practiceColors
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.fadeIn
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import kotlinx.coroutines.delay
import androidx.compose.animation.AnimatedVisibility
import org.audiocular.nitnem.app.audio.StreamAudioController
import org.audiocular.nitnem.app.sync.SyncEditorScreen
import org.audiocular.nitnem.app.ui.theme.*
import org.audiocular.nitnem.models.Bani
import org.audiocular.nitnem.models.BaniLine
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.utils.translateDigits
import org.audiocular.nitnem.utils.translateHindiTransliteration
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.foundation.border

private sealed class QuizQuestion {

    data class FillBlank(
        val line: String,
        val answer: String,
        val chips: List<String>
    ) : QuizQuestion()

    data class Translation(
        val line: String,
        val answer: String,
        val options: List<String>
    ) : QuizQuestion()

    data class NextLine(
        val currentLine: String,
        val answer: String,
        val options: List<String>
    ) : QuizQuestion()

    data class TrueFalse(
        val statement: String,
        val answer: Boolean
    ) : QuizQuestion()

data class SequenceBuilder(
    val originalWords: List<String>,
    val hint: String
) : QuizQuestion()
}

/*
 * ─────────────────────────────────────────────────────────────
 * QUESTION GENERATOR
 * ─────────────────────────────────────────────────────────────
 */

private fun buildQuestions(
    lines: List<BaniLine>,
    displayLanguage: DisplayLanguage
): List<QuizQuestion> {

    val usable =
        lines.filter {
            it.gurmukhi.isNotBlank()
        }

    val questions =
        mutableListOf<QuizQuestion>()

    usable.forEachIndexed { index, line ->

        val words =
            line.gurmukhi
                .split(" ")
                .filter {
                    it.isNotBlank()
                }

        val translationText = when (displayLanguage) {
            DisplayLanguage.Punjabi -> line.punjabi.ifBlank { line.translation }
            DisplayLanguage.Hindi -> line.hindi.translateHindiTransliteration().ifBlank { line.translation }
            DisplayLanguage.English -> line.translation
        }

        /*
         * SMART FILL BLANK
         */

        if (words.size >= 5) {

            val hiddenIndex =
                (1 until words.lastIndex)
                    .random()

            val hiddenWord =
                words[hiddenIndex]

            val display =
                words.mapIndexed { i, word ->

                    if (i == hiddenIndex)
                        "_____"
                    else
                        word

                }.joinToString(" ")

            val distractors =
                usable.flatMap {

                    it.gurmukhi.split(" ")

                }.filter {

                    it.length > 2 &&
                    it != hiddenWord

                }.distinct()
                    .shuffled()
                    .take(5)

            questions.add(

                QuizQuestion.FillBlank(

                    line = display,

                    answer = hiddenWord,

                    chips =
                        (
                            listOf(hiddenWord) +
                            distractors
                        ).shuffled()
                )
            )
        }

        /*
         * TRANSLATION
         */

        if (translationText.isNotBlank()) {

            val distractors =
                usable
                    .filter { it.id != line.id }
                    .shuffled()
                    .take(3)
                    .map {
                        val dt = when (displayLanguage) {
                            DisplayLanguage.Punjabi -> it.punjabi.ifBlank { it.translation }
                            DisplayLanguage.Hindi -> it.hindi.translateHindiTransliteration().ifBlank { it.translation }
                            DisplayLanguage.English -> it.translation
                        }
                        if (dt.isNotBlank()) dt.take(90) else it.translation.take(90)
                    }

            if (distractors.size == 3) {

                questions.add(

                    QuizQuestion.Translation(

                        line =
                            line.gurmukhi,

                        answer =
                            translationText.take(90),

                        options =
                            (
                                listOf(
                                    translationText.take(90)
                                ) +
                                distractors
                            ).shuffled()
                    )
                )
            }
        }

        /*
         * NEXT LINE
         */

        if (index < usable.lastIndex) {

            val nextLine =
                usable[index + 1]

            val distractors =
                usable
                    .filter {
                        it.id != nextLine.id
                    }
                    .shuffled()
                    .take(3)
                    .map {
                        it.gurmukhi.take(70)
                    }

            questions.add(

                QuizQuestion.NextLine(

                    currentLine =
                        line.gurmukhi,

                    answer =
                        nextLine.gurmukhi.take(70),

                    options =
                        (
                            listOf(
                                nextLine.gurmukhi.take(70)
                            ) +
                                distractors
                            ).shuffled()
                    )
            )
        }

        /*
         * TRUE FALSE
         */

        if (translationText.isNotBlank()) {

            val isCorrect =
                listOf(true, false)
                    .random()

            val otherLines = usable.filter { it.id != line.id }
            val shownTranslation =
                if (isCorrect || otherLines.isEmpty()) {
                    translationText
                } else {
                    val otherLine = otherLines.random()
                    val otherText = when (displayLanguage) {
                        DisplayLanguage.Punjabi -> otherLine.punjabi.ifBlank { otherLine.translation }
                        DisplayLanguage.Hindi -> otherLine.hindi.translateHindiTransliteration().ifBlank { otherLine.translation }
                        DisplayLanguage.English -> otherLine.translation
                    }
                    if (otherText.isNotBlank()) otherText else otherLine.translation
                }

      questions.add(

    QuizQuestion.TrueFalse(

        statement =
            "${line.gurmukhi}\n\n$shownTranslation",

        answer =
            isCorrect && (shownTranslation == translationText)
    )
)
        }

        /*
         * SEQUENCE BUILDER
         */

        if (words.size >= 4) {
questions.add(
    QuizQuestion.SequenceBuilder(
        originalWords = words,
        hint = translationText.ifBlank {
            "Arrange the Gurbani line correctly".translate(displayLanguage)
        }
    )
)
        }
    }

    return questions
        .shuffled()
        .take(30)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiGurbaniQuizTab(
    bani: Bani,
    colors: PracticeColors,
    displayLanguage: DisplayLanguage
) {

    val questions = remember(bani.id, displayLanguage) {
        buildQuestions(bani.lines, displayLanguage)
    }

    if (questions.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(

                text = "No quiz available".translate(displayLanguage),

                color = colors.textSecondary,

                fontSize = 15.sp
            )
        }

        return
    }

    /*
     * STATES
     */

    var questionIndex by rememberSaveable {
        mutableStateOf(0)
    }

    var score by rememberSaveable {
        mutableStateOf(0)
    }

    var mistakes by rememberSaveable {
        mutableStateOf(0)
    }

    var quizFinished by rememberSaveable {
        mutableStateOf(false)
    }

    var wrongAnswers by rememberSaveable {
        mutableStateOf<List<Pair<String, String>>>(
            emptyList()
        )
    }

    val question =
        questions[questionIndex]

    var answered by remember(questionIndex) {
        mutableStateOf(false)
    }

    var selectedOption by remember(questionIndex) {
        mutableStateOf<String?>(null)
    }

    var selectedWords by remember(questionIndex) {
        mutableStateOf<List<String>>(emptyList())
    }

    var answerCorrect by remember(questionIndex) {
        mutableStateOf(false)
    }

    val shuffledWords =
        remember(questionIndex) {

            if (question is QuizQuestion.SequenceBuilder) {

                question.originalWords.shuffled()

            } else {

                emptyList()
            }
        }

    val isLastQuestion =
        questionIndex == questions.lastIndex

    /*
     * SHARED COLORS
     */

    val glassCard =
        colors.card.copy(alpha = 0.38f)

    val subtleBorder =
        colors.textSecondary.copy(alpha = 0.08f)

    val optionColor =
        colors.cardSecondary.copy(alpha = 0.32f)

    /*
     * RESULT SCREEN
     */

    if (quizFinished) {

        LazyColumn(

            modifier = Modifier.fillMaxSize(),

            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 16.dp,
                bottom = 120.dp
            ),

            verticalArrangement =
                Arrangement.spacedBy(18.dp)
        ) {

            item {

                Surface(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(34.dp),

                    color = glassCard,

                    border = BorderStroke(
                        1.dp,
                        subtleBorder
                    )
                ) {

                    Column(

                        modifier =
                            Modifier.padding(28.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Text(

                            text = "Quiz Complete".translate(displayLanguage),

                            color =
                                colors.textPrimary,

                            fontSize = 30.sp,

                            fontWeight =
                                FontWeight.ExtraBold
                        )

                        Spacer(
                            Modifier.height(28.dp)
                        )

                        /*
                         * SCORE CIRCLE
                         */

                        Box(

                            modifier = Modifier
                                .size(180.dp)
                                .clip(CircleShape)
                                .background(

                                    Brush.radialGradient(

                                        listOf(

                                            colors.accent.copy(
                                                alpha = 0.24f
                                            ),

                                            Color.Transparent
                                        )
                                    )
                                ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Column(

                                horizontalAlignment =
                                    Alignment.CenterHorizontally
                            ) {

                                Text(

                                    text =
                                        "$score/${questions.size}".translateDigits(displayLanguage),

                                    color =
                                        colors.accent,

                                    fontSize = 46.sp,

                                    fontWeight =
                                        FontWeight.ExtraBold
                                )

                                Spacer(
                                    Modifier.height(6.dp)
                                )

                                Text(

                                    text = "Final Score".translate(displayLanguage),

                                    color =
                                        colors.textSecondary,

                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(
                            Modifier.height(28.dp)
                        )

                        Row(

                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.spacedBy(14.dp)
                        ) {

                            PremiumResultStat(

                                modifier =
                                    Modifier.weight(1f),

                                title = "Correct".translate(displayLanguage),

                                value = "$score".translateDigits(displayLanguage),

                                accent =
                                    colors.success,

                                colors = colors
                            )

                            PremiumResultStat(

                                modifier =
                                    Modifier.weight(1f),

                                title = "Mistakes".translate(displayLanguage),

                                value = "$mistakes".translateDigits(displayLanguage),

                                accent =
                                    colors.error,

                                colors = colors
                            )
                        }

                        Spacer(
                            Modifier.height(28.dp)
                        )

                        Button(

                            modifier =
                                Modifier.fillMaxWidth(),

                            shape =
                                RoundedCornerShape(24.dp),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        colors.accent
                                ),

                            onClick = {

                                questionIndex = 0
                                score = 0
                                mistakes = 0
                                quizFinished = false
                                wrongAnswers = emptyList()
                            }
                        ) {

                            Text(

                                text = "Restart Quiz".translate(displayLanguage),

                                color = Color.White,

                                modifier =
                                    Modifier.padding(
                                        vertical = 6.dp
                                    ),

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }
            }

            /*
             * WRONG ANSWERS
             */

            if (wrongAnswers.isNotEmpty()) {

                item {

                    Text(

                        text = "Review Mistakes".translate(displayLanguage),

                        color =
                            colors.error,

                        fontSize = 22.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }

                itemsIndexed(wrongAnswers) { _, pair ->

                    Surface(

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(28.dp),

                        color =
                            glassCard,

                        border = BorderStroke(
                            1.dp,
                            subtleBorder
                        )
                    ) {

                        Column(

                            modifier =
                                Modifier.padding(20.dp)
                        ) {

                            QuizSectionLabel(
                                title = "Question".translate(displayLanguage),
                                color = colors.error
                            )

                            Spacer(
                                Modifier.height(10.dp)
                            )

                            Text(

                                text =
                                    pair.first,

                                color =
                                    colors.textPrimary,

                                lineHeight = 30.sp,

                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            Spacer(
                                Modifier.height(22.dp)
                            )

                            QuizSectionLabel(
                                title = "Correct Answer".translate(displayLanguage),
                                color = colors.success
                            )

                            Spacer(
                                Modifier.height(10.dp)
                            )

                            Text(

                                text =
                                    pair.second,

                                color =
                                    colors.textPrimary,

                                lineHeight = 30.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        return
    }

    /*
     * MAIN QUIZ SCREEN
     */

    LazyColumn(

        modifier =
            Modifier.fillMaxSize(),

        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 14.dp,
            bottom = 120.dp
        ),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        /*
         * QUESTION CARD
         */

        item {

            Surface(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(34.dp),

                color =
                    glassCard,

                border = BorderStroke(
                    1.dp,
                    subtleBorder
                )
            ) {

                Column(

                    modifier =
                        Modifier.padding(24.dp)
                ) {

                    /*
                     * HEADER
                     */

                    Row(

                        modifier =
                            Modifier.fillMaxWidth(),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(

                            text =
                                "${"Question".translate(displayLanguage)} ${questionIndex + 1}/${questions.size}".translateDigits(displayLanguage),

                            color =
                                colors.textSecondary,

                            fontWeight =
                                FontWeight.SemiBold,

                            fontSize = 13.sp
                        )

                        Spacer(
                            Modifier.weight(1f)
                        )

                        Surface(

                            shape =
                                RoundedCornerShape(100.dp),

                            color =
                                colors.accent.copy(
                                    alpha = 0.14f
                                )
                        ) {

                            Text(

                                text =
                                    "${"Score".translate(displayLanguage)} $score".translateDigits(displayLanguage),

                                color =
                                    colors.accent,

                                fontWeight =
                                    FontWeight.Bold,

                                fontSize = 12.sp,

                                modifier =
                                    Modifier.padding(
                                        horizontal = 14.dp,
                                        vertical = 8.dp
                                    )
                            )
                        }
                    }

                    Spacer(
                        Modifier.height(18.dp)
                    )

                    LinearProgressIndicator(

                        progress = {

                            (questionIndex + 1f) /
                                questions.size
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(CircleShape),

                        color =
                            colors.accent,

                        trackColor =
                            colors.cardSecondary.copy(
                                alpha = 0.34f
                            )
                    )

                    Spacer(
                        Modifier.height(26.dp)
                    )

                    val title = when (question) {

                        is QuizQuestion.FillBlank ->
                            "Fill Missing Word"

                        is QuizQuestion.Translation ->
                            "Translation Match"

                        is QuizQuestion.NextLine ->
                            "Next Line"

                        is QuizQuestion.TrueFalse ->
                            "True or False"

                        is QuizQuestion.SequenceBuilder ->
                            "Arrange Sequence"
                    }

                    QuizSectionLabel(
                        title = title.translate(displayLanguage),
                        color = colors.accent
                    )

                    Spacer(
                        Modifier.height(18.dp)
                    )

                    /*
                     * QUESTION CONTENT
                     */
Surface(

    modifier =
        Modifier.fillMaxWidth(),

    shape =
        RoundedCornerShape(28.dp),

    color =
        colors.cardSecondary.copy(
            alpha = 0.22f
        ),

    border = BorderStroke(

        1.dp,

        colors.textSecondary.copy(
            alpha = 0.10f
        )
    )
) {

    Column(

        modifier =
            Modifier.padding(20.dp)
    ) {

        when (question) {

            is QuizQuestion.TrueFalse -> {

                val parts =
                    question.statement
                        .split("\n\n")

                val gurbani =
                    parts.getOrNull(0)
                        .orEmpty()

                val translation =
                    parts.getOrNull(1)
                        .orEmpty()

                Text(

                    text = gurbani,

                    color =
                        colors.textPrimary,

                    fontSize = 28.sp,

                    lineHeight = 42.sp,

                    fontWeight =
                        FontWeight.ExtraBold
                )

                if (translation.isNotBlank()) {

                    Spacer(
                        Modifier.height(20.dp)
                    )

                    Surface(

                        shape =
                            RoundedCornerShape(
                                22.dp
                            ),

                        color =
                            colors.accent.copy(
                                alpha = 0.12f
                            )
                    ) {

                        Text(

                            text =
                                translation,

                            color =
                                colors.accent,

                            fontWeight =
                                FontWeight.Bold,

                            fontSize = 13.sp,

                            lineHeight = 22.sp,

                            modifier =
                                Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 10.dp
                                )
                        )
                    }
                }
            }

            is QuizQuestion.SequenceBuilder -> {

                Text(

                    text =
                        "Arrange the Gurbani line correctly",

                    color =
                        colors.textPrimary,

                    fontSize = 14.sp,

                    lineHeight = 40.sp,

                    fontWeight =
                        FontWeight.ExtraBold
                )

                Spacer(
                    Modifier.height(22.dp)
                )

                Surface(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            24.dp
                        ),

                    color =
                        colors.accent.copy(
                            alpha = 0.08f
                        )
                ) {

                    Column(

                        modifier =
                            Modifier.padding(
                                18.dp
                            )
                    ) {

                        QuizSectionLabel(
                            title = "Hint".translate(displayLanguage),
                            color = colors.accent
                        )

                        Spacer(
                            Modifier.height(8.dp)
                        )

                        Text(

                            text =
                                question.hint.translate(displayLanguage),

                            color =
                                colors.textSecondary,

                            lineHeight = 22.sp
                        )

                        Spacer(
                            Modifier.height(20.dp)
                        )

                        Text(

                            text =

                                if (
                                    selectedWords.isEmpty()
                                ) {

                                    "Tap words below...".translate(displayLanguage)

                                } else {

                                    selectedWords.joinToString(
                                        " "
                                    )
                                },

                            color =
                                colors.textPrimary,

                            fontWeight =
                                FontWeight.Bold,

                            lineHeight = 34.sp
                        )
                    }
                }
            }

            else -> {

                val questionText = when (question) {

                    is QuizQuestion.FillBlank ->
                        question.line

                    is QuizQuestion.Translation ->
                        question.line

                    is QuizQuestion.NextLine ->
                        question.currentLine

                    else -> ""
                }

                Text(

                    text =
                        questionText,

                    color =
                        colors.textPrimary,

                    fontSize = 28.sp,

                    lineHeight = 42.sp,

                    fontWeight =
                        FontWeight.ExtraBold
                )
            }
        }
    }
}
                }
            }
        }

        /*
         * ANSWERS
         */

        when (question) {

            is QuizQuestion.FillBlank -> {

                item {

                    FlowRow(

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                10.dp
                            ),

                        verticalArrangement =
                            Arrangement.spacedBy(
                                10.dp
                            )
                    ) {

                        question.chips.forEach { chip ->

                            QuizChoiceChip(

                                text = chip,

                                selected =
                                    selectedOption == chip,

                                answered =
                                    answered,

                                correct =
                                    chip ==
                                        question.answer,

                                enabled =
                                    !answered,

                                colors = colors
                            ) {

                                selectedOption =
                                    chip

                                answered = true

                                val correct =
                                    chip ==
                                        question.answer

                                answerCorrect =
                                    correct

                                if (correct) {

                                    score++

                                } else {

                                    mistakes++

                                    wrongAnswers =
                                        wrongAnswers + (
                                            question.line to
                                                question.answer
                                        )
                                }
                            }
                        }
                    }
                }
            }

            is QuizQuestion.Translation,
            is QuizQuestion.NextLine -> {

                val options = when (question) {

                    is QuizQuestion.Translation ->
                        question.options

                    is QuizQuestion.NextLine ->
                        question.options

                    else -> emptyList()
                }

                val answer = when (question) {

                    is QuizQuestion.Translation ->
                        question.answer

                    is QuizQuestion.NextLine ->
                        question.answer

                    else -> ""
                }

                itemsIndexed(options) { _, option ->

                    QuizOptionCard(

                        text = option,

                        selected =
                            selectedOption == option,

                        answered =
                            answered,

                        correct =
                            option == answer,

                        colors = colors
                    ) {

                        selectedOption =
                            option

                        answered = true

                        val correct =
                            option == answer

                        answerCorrect =
                            correct

                        if (correct) {

                            score++

                        } else {

                            mistakes++

                            wrongAnswers =
                                wrongAnswers + (
                                    "Question" to answer
                                )
                        }
                    }
                }
            }

            is QuizQuestion.TrueFalse -> {

                item {

                    Row(

                        modifier =
                            Modifier.fillMaxWidth(),

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                14.dp
                            )
                    ) {

                        listOf(true, false)
                            .forEach { value ->

                                QuizBinaryButton(

                                    modifier =
                                        Modifier.weight(1f),

                                    text =

                                        if (value)
                                            "True".translate(displayLanguage)
                                        else
                                            "False".translate(displayLanguage),

                                    selected =
                                        selectedOption ==
                                            value.toString(),

                                    answered =
                                        answered,

                                    correct =
                                        value ==
                                            question.answer,

                                    colors = colors
                                ) {

                                    answered = true

                                    selectedOption =
                                        value.toString()

                                    val correct =
                                        value ==
                                            question.answer

                                    answerCorrect =
                                        correct

                                    if (correct) {

                                        score++

                                    } else {

                                        mistakes++

                                        wrongAnswers =
                                            wrongAnswers + (
                                                question.statement to
                                                    question.answer.toString()
                                            )
                                    }
                                }
                            }
                    }
                }
            }

            is QuizQuestion.SequenceBuilder -> {

                item {

                    FlowRow(

                        horizontalArrangement =
                            Arrangement.spacedBy(
                                10.dp
                            ),

                        verticalArrangement =
                            Arrangement.spacedBy(
                                10.dp
                            )
                    ) {

                        shuffledWords.forEachIndexed { index, word ->

                            val usedCount =
                                selectedWords.count {
                                    it == word
                                }

                            val availableCount =
                                shuffledWords
                                    .take(index + 1)
                                    .count {
                                        it == word
                                    }

                            val hidden =
                                usedCount >= availableCount

                            if (!hidden) {

                                QuizChoiceChip(

                                    text = word,

                                    selected = false,

                                    answered = false,

                                    correct = false,

                                    enabled =
                                        !answered,

                                    colors = colors
                                ) {

                                    val updated =
                                        selectedWords +
                                            word

                                    selectedWords =
                                        updated

                                    if (

                                        updated.size ==
                                            question.originalWords.size

                                    ) {

                                        answered = true

                                        val correctLine =
                                            updated ==
                                                question.originalWords

                                        answerCorrect =
                                            correctLine

                                        if (correctLine) {

                                            score++

                                        } else {

                                            mistakes++

                                            wrongAnswers =
                                                wrongAnswers + (
                                                    "Arrange Sequence" to
                                                        question.originalWords
                                                            .joinToString(" ")
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

        /*
         * CONTINUE
         */

        if (answered) {

            item {

                Spacer(
                    Modifier.height(4.dp)
                )

                Button(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(24.dp),

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                if (answerCorrect)
                                    colors.success
                                else
                                    colors.error
                        ),

                    onClick = {

                        if (!isLastQuestion) {

                            questionIndex++

                        } else {

                            quizFinished = true
                        }
                    }
                ) {

                    Text(

                        text =

                            if (isLastQuestion)
                                                                "Finish Quiz".translate(displayLanguage)
                                                            else
                                                                "Continue".translate(displayLanguage),

                        color = Color.White,

                        modifier =
                            Modifier.padding(
                                vertical = 6.dp
                            ),

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}

/*
 * ─────────────────────────────────────────────────────────────
 * REUSABLE COMPONENTS
 * ─────────────────────────────────────────────────────────────
 */

@Composable
private fun QuizSectionLabel(
    title: String,
    color: Color
) {

    Text(

        text = title,

        color = color,

        fontSize = 13.sp,

        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun QuizChoiceChip(
    text: String,
    selected: Boolean,
    answered: Boolean,
    correct: Boolean,
    enabled: Boolean,
    colors: PracticeColors,
    onClick: () -> Unit
) {

    val background = when {

        answered &&
            selected &&
            correct -> {

            colors.success.copy(alpha = 0.18f)
        }

        answered &&
            selected &&
            !correct -> {

            colors.error.copy(alpha = 0.18f)
        }

        else -> {

            colors.cardSecondary.copy(
                alpha = 0.34f
            )
        }
    }

    Surface(

        shape =
            RoundedCornerShape(22.dp),

        color = background,

        border = BorderStroke(
            1.dp,
            colors.textSecondary.copy(
                alpha = 0.08f
            )
        ),

        modifier =
            Modifier.clickable(
                enabled = enabled,
                onClick = onClick
            )
    ) {

        Text(

            text = text,

            color =
                colors.textPrimary,

            fontWeight =
                FontWeight.SemiBold,

            modifier =
                Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 14.dp
                )
        )
    }
}

@Composable
private fun QuizOptionCard(
    text: String,
    selected: Boolean,
    answered: Boolean,
    correct: Boolean,
    colors: PracticeColors,
    onClick: () -> Unit
) {

    val background = when {

        answered &&
            selected &&
            correct -> {

            colors.success.copy(alpha = 0.18f)
        }

        answered &&
            selected &&
            !correct -> {

            colors.error.copy(alpha = 0.18f)
        }

        else -> {

            colors.cardSecondary.copy(
                alpha = 0.32f
            )
        }
    }

    Surface(

        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = !answered,
                onClick = onClick
            ),

        shape =
            RoundedCornerShape(28.dp),

        color = background,

        border = BorderStroke(
            1.dp,
            colors.textSecondary.copy(
                alpha = 0.08f
            )
        )
    ) {

        Text(

            text = text,

            color =
                colors.textPrimary,

            fontSize = 16.sp,

            lineHeight = 26.sp,

            fontWeight =
                FontWeight.Medium,

            modifier =
                Modifier.padding(20.dp)
        )
    }
}

@Composable
private fun QuizBinaryButton(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    answered: Boolean,
    correct: Boolean,
    colors: PracticeColors,
    onClick: () -> Unit
) {

    val background = when {

        answered &&
            selected &&
            correct -> {

            colors.success.copy(alpha = 0.18f)
        }

        answered &&
            selected &&
            !correct -> {

            colors.error.copy(alpha = 0.18f)
        }

        else -> {

            colors.cardSecondary.copy(
                alpha = 0.32f
            )
        }
    }

    Surface(

        modifier = modifier
            .clickable(
                enabled = !answered,
                onClick = onClick
            ),

        shape =
            RoundedCornerShape(26.dp),

        color = background,

        border = BorderStroke(
            1.dp,
            colors.textSecondary.copy(
                alpha = 0.08f
            )
        )
    ) {

        Text(

            text = text,

            color =
                colors.textPrimary,

            textAlign =
                TextAlign.Center,

            fontWeight =
                FontWeight.Bold,

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = 18.dp
                    )
        )
    }
}

@Composable
private fun PremiumResultStat(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    accent: Color,
    colors: PracticeColors
) {

    Surface(

        modifier = modifier,

        shape =
            RoundedCornerShape(24.dp),

        color =
            accent.copy(alpha = 0.12f)
    ) {

        Column(

            modifier =
                Modifier.padding(18.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(

                text = value,

                color = accent,

                fontSize = 28.sp,

                fontWeight =
                    FontWeight.ExtraBold
            )

            Spacer(
                Modifier.height(4.dp)
            )

            Text(

                text = title,

                color =
                    colors.textSecondary,

                fontSize = 12.sp
            )
        }
    }
}
