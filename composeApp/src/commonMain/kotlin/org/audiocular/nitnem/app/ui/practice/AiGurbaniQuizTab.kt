package org.audiocular.nitnem.app.ui.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.audiocular.nitnem.models.Bani
import org.audiocular.nitnem.app.models.DisplayLanguage
import org.audiocular.nitnem.utils.translate
import org.audiocular.nitnem.utils.translateDigits
import org.audiocular.nitnem.utils.translateHindiTransliteration
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import org.audiocular.nitnem.models.NitnemUiState
import org.audiocular.nitnem.viewmodel.NitnemViewModel
import org.audiocular.nitnem.app.ui.components.MascotBani
import org.audiocular.nitnem.app.ui.components.MascotState
import org.audiocular.nitnem.models.QuizQuestion
import org.audiocular.nitnem.models.QuizSession

/*
 * ─────────────────────────────────────────────────────────────
 * QUESTION GENERATOR
 * ─────────────────────────────────────────────────────────────
 */

private fun buildQuestions(
    bani: Bani,
    displayLanguage: DisplayLanguage
): List<QuizQuestion> {

    val lines = bani.lines
    val usable =
        lines.filter {
            it.gurmukhi.isNotBlank()
        }

    val questions =
        mutableListOf<QuizQuestion>()

    // 1. Author Question
    if (bani.author.isNotBlank()) {
        val commonAuthors = listOf(
            "Guru Nanak Dev Ji",
            "Guru Angad Dev Ji",
            "Guru Amar Das Ji",
            "Guru Ram Das Ji",
            "Guru Arjan Dev Ji",
            "Guru Teg Bahadur Ji",
            "Guru Gobind Singh Ji",
            "Bhagat Kabir Ji",
            "Bhagat Ravidas Ji",
            "Baba Farid Ji"
        ).map { it.translate(displayLanguage) }

        val actualAuthor = bani.author.translate(displayLanguage)
        val distractors = commonAuthors
            .filter { it != actualAuthor }
            .shuffled()
            .take(3)

        questions.add(
            QuizQuestion.BaniAuthor(
                baniName = bani.title,
                answer = actualAuthor,
                options = (listOf(actualAuthor) + distractors).shuffled()
            )
        )
    }

    // 2. Ang Question
    if (bani.ang.isNotBlank()) {
        val actualAng = bani.ang.translate(displayLanguage)
        val angNum = bani.ang.toIntOrNull()
        val distractors = if (angNum != null) {
            listOf(angNum + 5, angNum - 4, angNum + 12)
                .map { it.coerceAtLeast(1).toString().translate(displayLanguage) }
        } else {
            listOf("1", "110", "1429").map { it.translate(displayLanguage) }
        }

        questions.add(
            QuizQuestion.BaniAng(
                baniName = bani.title,
                answer = actualAng,
                options = (listOf(actualAng) + distractors).shuffled()
            )
        )
    }

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
            val distractors = usable
                .filter { it.id != line.id }
                .map {
                    val dt = when (displayLanguage) {
                        DisplayLanguage.Punjabi -> it.punjabi.ifBlank { it.translation }
                        DisplayLanguage.Hindi -> it.hindi.translateHindiTransliteration().ifBlank { it.translation }
                        DisplayLanguage.English -> it.translation
                    }
                    dt.trim()
                }
                .filter { it.isNotBlank() && it.take(90) != translationText.trim().take(90) }
                .distinct()
                .shuffled()
                .take(3)
                .map { it.take(90) }
                .toMutableList()

            while (distractors.size < 3) {
                val fallback = usable
                    .filter { it.id != line.id && !distractors.contains(it.translation.take(90)) }
                    .shuffled()
                    .firstOrNull()
                    ?.translation
                    ?.take(90) ?: "Gurbani Translation Detail"
                distractors.add(fallback)
            }

            questions.add(
                QuizQuestion.Translation(
                    line = line.gurmukhi,
                    answer = translationText.take(90),
                    options = (
                        listOf(
                            translationText.take(90)
                        ) + distractors
                    ).shuffled()
                )
            )
        }

        /*
         * WORD MEANING
         */
        if (line.wordMeaning.isNotBlank()) {
            val distractors = usable
                .filter { it.id != line.id && it.wordMeaning.isNotBlank() }
                .shuffled()
                .take(3)
                .map { it.wordMeaning.take(95) }
                .toMutableList()

            while (distractors.size < 3) {
                val fallback = usable
                    .filter { it.id != line.id }
                    .shuffled()
                    .firstOrNull()
                    ?.let {
                        if (it.wordMeaning.isNotBlank()) it.wordMeaning.take(95)
                        else it.translation.take(95)
                    } ?: "Gurbani Word Meaning Explanation"
                distractors.add(fallback)
            }

            questions.add(
                QuizQuestion.WordMeaning(
                    line = line.gurmukhi,
                    answer = line.wordMeaning.take(95),
                    options = (listOf(line.wordMeaning.take(95)) + distractors).shuffled()
                )
            )
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
                    .toMutableList()

            while (distractors.size < 3) {
                val fallback = usable
                    .filter { it.id != nextLine.id && !distractors.contains(it.gurmukhi.take(70)) }
                    .shuffled()
                    .firstOrNull()
                    ?.gurmukhi
                    ?.take(70) ?: "Gurbani Paath Line"
                distractors.add(fallback)
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

            val otherLines = usable.filter { 
                it.id != line.id && 
                when (displayLanguage) {
                    DisplayLanguage.Punjabi -> it.punjabi.isNotBlank() || it.translation.isNotBlank()
                    DisplayLanguage.Hindi -> it.hindi.isNotBlank() || it.translation.isNotBlank()
                    DisplayLanguage.English -> it.translation.isNotBlank()
                }
            }
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
    displayLanguage: DisplayLanguage,
    uiState: NitnemUiState,
    viewModel: NitnemViewModel
) {

    val activeSession = uiState.activeQuizSession
    var restartTrigger by remember { mutableStateOf(0) }

    val questions = remember(bani.id, displayLanguage, restartTrigger) {
        if (activeSession != null && activeSession.baniId == bani.id && restartTrigger == 0) {
            activeSession.questions
        } else {
            buildQuestions(bani, displayLanguage)
        }
    }

    if (questions.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

           Column(
    horizontalAlignment = Alignment.CenterHorizontally
) {

    MascotBani(

        state = MascotState.Oops,

        modifier = Modifier.size(120.dp)
    )

    Spacer(
        Modifier.height(12.dp)
    )

    Text(
        text = "No quiz available"
            .translate(displayLanguage),

        color = colors.textSecondary,

        fontSize = 15.sp
    )
}
        }

        return
    }

    /*
     * STATES
     */

    val initialIndex = if (activeSession != null && activeSession.baniId == bani.id && restartTrigger == 0) activeSession.questionIndex else 0
    val initialScore = if (activeSession != null && activeSession.baniId == bani.id && restartTrigger == 0) activeSession.score else 0
    val initialMistakes = if (activeSession != null && activeSession.baniId == bani.id && restartTrigger == 0) activeSession.mistakes else 0
    val initialHearts = if (activeSession != null && activeSession.baniId == bani.id && restartTrigger == 0) activeSession.hearts else 5
    val initialFinished = if (activeSession != null && activeSession.baniId == bani.id && restartTrigger == 0) activeSession.quizFinished else false
    val initialWrongs = if (activeSession != null && activeSession.baniId == bani.id && restartTrigger == 0) activeSession.wrongAnswers else emptyList()
    val initialNoHearts = if (activeSession != null && activeSession.baniId == bani.id && restartTrigger == 0) activeSession.continueNoHeartsMode else false

    var questionIndex by rememberSaveable(bani.id, restartTrigger) {
        mutableStateOf(initialIndex)
    }

    var score by rememberSaveable(bani.id, restartTrigger) {
        mutableStateOf(initialScore)
    }

    var mistakes by rememberSaveable(bani.id, restartTrigger) {
        mutableStateOf(initialMistakes)
    }

    var hearts by rememberSaveable(bani.id, restartTrigger) {
        mutableStateOf(initialHearts)
    }

    var showContinueQuizDialog by remember { mutableStateOf(false) }
    var continueNoHeartsMode by rememberSaveable(bani.id, restartTrigger) {
        mutableStateOf(initialNoHearts)
    }

    var quizFinished by rememberSaveable(bani.id, restartTrigger) {
        mutableStateOf(initialFinished)
    }

    var wrongAnswers by rememberSaveable(bani.id, restartTrigger) {
        mutableStateOf<List<String>>(
            initialWrongs
        )
    }

    LaunchedEffect(
        bani.id,
        questions,
        questionIndex,
        score,
        mistakes,
        hearts,
        quizFinished,
        wrongAnswers,
        continueNoHeartsMode
    ) {
        viewModel.setQuizSession(
            QuizSession(
                baniId = bani.id,
                questions = questions,
                questionIndex = questionIndex,
                score = score,
                mistakes = mistakes,
                hearts = hearts,
                quizFinished = quizFinished,
                wrongAnswers = wrongAnswers,
                continueNoHeartsMode = continueNoHeartsMode
            )
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

     LaunchedEffect(questionIndex, answered) {
        if (answered) {
            delay(1500L)
            if (hearts <= 0 && !continueNoHeartsMode) {
                showContinueQuizDialog = true
            } else if (!isLastQuestion) {
                questionIndex++
            } else {
                quizFinished = true
            }
        }
    }

    LaunchedEffect(quizFinished) {
        if (quizFinished) {
            val earnedXp = score * 10 + (if (score == questions.size && hearts == 5) 50 else 0)
            viewModel.awardXp(earnedXp)
            viewModel.updateQuizHighscore(score)
        }
    }

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
                top = 12.dp,
                bottom = 80.dp
            ),

            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            item {

    val xpEarned =
        score * 10 +
                if (score == questions.size && hearts == 5) 50 else 0

    Surface(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(28.dp),

        color = glassCard,

        border = BorderStroke(
            1.dp,
            subtleBorder
        )
    ) {

        Column(

            modifier = Modifier.padding(18.dp)
        ) {

            Row(

                modifier = Modifier.fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically
            ) {

                MascotBani(

                    state = when {

                        hearts <= 0 ->
                            MascotState.Oops

                        score == questions.size ->
                            MascotState.Celebrating

                        else ->
                            MascotState.Happy
                    },

                    modifier = Modifier.size(64.dp)
                )

                Spacer(
                    Modifier.width(12.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(

                        text = when {

                            hearts <= 0 ->
                                "Out of Lives!"
                                    .translate(displayLanguage)

                            score == questions.size ->
                                "Perfect Score!"
                                    .translate(displayLanguage)

                            else ->
                                "Quiz Complete"
                                    .translate(displayLanguage)
                        },

                        color = colors.textPrimary,

                        fontSize = 22.sp,

                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        Modifier.height(2.dp)
                    )

                   Row(
    horizontalArrangement = Arrangement.spacedBy(8.dp)
) {

    Surface(
        shape = RoundedCornerShape(100.dp),
        color = colors.accent.copy(alpha = 0.15f)
    ) {

        Text(
            text = "${score}/${questions.size}"
                .translateDigits(displayLanguage),

            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 4.dp
            ),

            color = colors.accent,

            fontSize = 12.sp,

            fontWeight = FontWeight.Bold
        )
    }

    Surface(
        shape = RoundedCornerShape(100.dp),
        color = colors.success.copy(alpha = 0.15f)
    ) {

        Text(
            text = "+$xpEarned XP"
                .translateDigits(displayLanguage),

            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 4.dp
            ),

            color = colors.success,

            fontSize = 12.sp,

            fontWeight = FontWeight.Bold
        )
    }
}
                }
            }

            Spacer(
                Modifier.height(14.dp)
            )

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                PremiumResultStat(

                    modifier = Modifier.weight(1f),

                    title =
                        "Correct"
                            .translate(displayLanguage),

                    value =
                        "$score"
                            .translateDigits(displayLanguage),

                    accent =
                        colors.success,

                    colors = colors
                )

                PremiumResultStat(

                    modifier = Modifier.weight(1f),

                    title =
                        "Mistakes"
                            .translate(displayLanguage),

                    value =
                        "$mistakes"
                            .translateDigits(displayLanguage),

                    accent =
                        colors.error,

                    colors = colors
                )
            }

            Spacer(
                Modifier.height(14.dp)
            )

            Button(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(18.dp),

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            colors.accent
                    ),

                onClick = {

                    restartTrigger++

                    questionIndex = 0

                    score = 0

                    mistakes = 0

                    hearts = 5

                    continueNoHeartsMode = false

                    quizFinished = false

                    wrongAnswers = emptyList()
                }
            ) {

                Text(

                    text =
                        "Restart Quiz"
                            .translate(displayLanguage),

                    color = Color.White,

                    fontWeight = FontWeight.Bold
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

             Row(
    verticalAlignment = Alignment.CenterVertically
) {

 

    Text(

        text = "Review Mistakes"
            .translate(displayLanguage),

        color = colors.error,

        fontSize = 22.sp,

        fontWeight = FontWeight.Bold
    )
}
                }

                itemsIndexed(wrongAnswers) { _, entry ->
                    val parts = entry.split(":::")
                    val questionText = parts.getOrNull(0).orEmpty()
                    val correctAnswerText = parts.getOrNull(1).orEmpty()

                    Surface(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(20.dp),
    color = glassCard,
    border = BorderStroke(1.dp, subtleBorder)
) {

    Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        // Question Row

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {

            Text(
                text = "Q: ".translate(displayLanguage),
                color = colors.error,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Text(
                text = questionText,
                modifier = Modifier.weight(1f),
                color = colors.textPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        HorizontalDivider(
            color = colors.textSecondary.copy(alpha = 0.1f)
        )

        // Answer Row

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {

            Text(
                text = "A: ".translate(displayLanguage),
                color = colors.success,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Text(
                text = correctAnswerText,
                modifier = Modifier.weight(1f),
                color = colors.textPrimary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
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
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 14.dp,
            bottom = 30.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
           val questionTitleText = when (question) {
    is QuizQuestion.FillBlank ->
        "Fill Missing Word".translate(displayLanguage)

    is QuizQuestion.Translation ->
        "Translation Match".translate(displayLanguage)

    is QuizQuestion.NextLine ->
        "Next Line".translate(displayLanguage)

    is QuizQuestion.TrueFalse ->
        "True or False".translate(displayLanguage)

    is QuizQuestion.SequenceBuilder ->
        "Arrange Sequence".translate(displayLanguage)

    is QuizQuestion.WordMeaning ->
        "Word Meaning".translate(displayLanguage)

    is QuizQuestion.BaniAuthor ->
        "Bani Author".translate(displayLanguage)

    is QuizQuestion.BaniAng ->
        "Bani Ang (Page)".translate(displayLanguage)
}

            val questionText = when (question) {
                is QuizQuestion.FillBlank -> question.line
                is QuizQuestion.Translation -> question.line
                is QuizQuestion.NextLine -> question.currentLine
                is QuizQuestion.TrueFalse -> {
                    val parts = question.statement.split("\n\n")
                    parts.getOrNull(0).orEmpty()
                }
                is QuizQuestion.SequenceBuilder -> "Arrange Gurbani Line".translate(displayLanguage)
                is QuizQuestion.WordMeaning -> question.line
                is QuizQuestion.BaniAuthor -> question.baniName
                is QuizQuestion.BaniAng -> question.baniName
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(34.dp),
                color = glassCard,
                border = BorderStroke(1.dp, subtleBorder)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val currentMascotState = remember(answered, answerCorrect, question, hearts) {
                            when {
                                hearts <= 0 -> MascotState.Oops
                                answered && answerCorrect -> MascotState.Celebrating
                                answered && !answerCorrect -> MascotState.Oops
                                question is QuizQuestion.SequenceBuilder -> MascotState.Thinking
                                question is QuizQuestion.FillBlank -> MascotState.Thinking
                                question is QuizQuestion.NextLine -> MascotState.Reading
                                question is QuizQuestion.Translation -> MascotState.Listening
                                question is QuizQuestion.WordMeaning -> MascotState.Reading
                                question is QuizQuestion.BaniAuthor -> MascotState.Reading
                                question is QuizQuestion.BaniAng -> MascotState.Reading
                                else -> MascotState.Happy
                            }
                        }

                        MascotBani(
                            state = currentMascotState,
                            modifier = Modifier.size(78.dp)
                        )

                        Spacer(Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = questionText,
                                color = colors.textPrimary,
                                fontSize = 20.sp,
                                lineHeight = 30.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Spacer(Modifier.height(4.dp))

                         Row(
    verticalAlignment = Alignment.CenterVertically
) {

    when {

        answered && answerCorrect -> {

            Text(
                text = "Great job!"
                    .translate(displayLanguage),

                color = colors.success,

                fontSize = 12.sp,

                fontWeight = FontWeight.Bold
            )
        }

        answered && !answerCorrect -> {

            Text(
                text = "Keep trying"
                    .translate(displayLanguage),

                color = colors.error,

                fontSize = 12.sp,

                fontWeight = FontWeight.Bold
            )
        }

        hearts <= 0 -> {

            Text(
                text = "Out of hearts"
                    .translate(displayLanguage),

                color = colors.error,

                fontSize = 12.sp,

                fontWeight = FontWeight.Bold
            )
        }

        else -> {

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            shape = RoundedCornerShape(100.dp),

            color = Color.Transparent
        ) {

            Text(
                text = "Q: "
                    .translate(displayLanguage),

                modifier = Modifier.padding(
                    horizontal = 2.dp,
                    vertical = 3.dp
                ),

                color = colors.accent,

                fontSize = 13.sp,

                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "${questionIndex + 1}/${questions.size}"
                .translateDigits(displayLanguage),

           color = colors.textSecondary,

            fontSize = 10.sp,

            fontWeight = FontWeight.SemiBold
        )
    }
}
    }

    Spacer(
        Modifier.width(6.dp)
    )

    Text(
        text = "•",

        color = colors.textSecondary.copy(alpha = 0.5f),

        fontSize = 12.sp
    )

    Spacer(
        Modifier.width(6.dp)
    )

    Text(
        text = questionTitleText,

        color = colors.textSecondary,

        fontSize = 12.sp
    )
}
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { (questionIndex + 1f) / questions.size },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(CircleShape),
                        color = colors.accent,
                        trackColor = colors.cardSecondary.copy(alpha = 0.34f)
                    )

                    Spacer(Modifier.height(18.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        color = colors.cardSecondary.copy(alpha = 0.22f),
                        border = BorderStroke(1.dp, colors.textSecondary.copy(alpha = 0.10f))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            when (question) {
                                is QuizQuestion.TrueFalse -> {
                                    val parts = question.statement.split("\n\n")
                                    val translation = parts.getOrNull(1).orEmpty()
                                    if (translation.isNotBlank()) {
                                        Text(
                                            text = "Translation:".translate(displayLanguage),
                                            color = colors.accent,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Text(
                                            text = translation,
                                            color = colors.textPrimary,
                                            fontSize = 16.sp,
                                            lineHeight = 24.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    } else {
                                        Text(
                                            text = "Is this Gurbani line correct?".translate(displayLanguage),
                                            color = colors.textPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                is QuizQuestion.SequenceBuilder -> {
                                    QuizSectionLabel(
                                        title = "Hint / Translation".translate(displayLanguage),
                                        color = colors.accent
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = question.hint.translate(displayLanguage),
                                        color = colors.textSecondary,
                                        lineHeight = 22.sp
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Text(
                                        text = if (selectedWords.isEmpty()) {
                                            "Tap words below to arrange...".translate(displayLanguage)
                                        } else {
                                            selectedWords.joinToString(" ")
                                        },
                                        color = colors.textPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        lineHeight = 28.sp
                                    )
                                }

                                is QuizQuestion.WordMeaning -> {
                                    Text(
                                        text = "What is the meaning / explanation of this line?".translate(displayLanguage),
                                        color = colors.accent,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                is QuizQuestion.BaniAuthor -> {
                                    Text(
                                        text = "Who is the author / writer of this Bani?".translate(displayLanguage),
                                        color = colors.accent,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                is QuizQuestion.BaniAng -> {
                                    Text(
                                        text = "What is the starting Ang (Page) of this Bani?".translate(displayLanguage),
                                        color = colors.accent,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                is QuizQuestion.FillBlank -> {
                                    Text(
                                        text = "Fill the missing word in the line above".translate(displayLanguage),
                                        color = colors.accent,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                is QuizQuestion.Translation -> {
                                    Text(
                                        text = "Match the correct translation for the line above".translate(displayLanguage),
                                        color = colors.accent,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                is QuizQuestion.NextLine -> {
                                    Text(
                                        text = "What is the next line of this Gurbani Paath?".translate(displayLanguage),
                                        color = colors.accent,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
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
                                    hearts--
                                   wrongAnswers =
    wrongAnswers +
        "Fill Missing Word\n\n${question.line}:::${question.answer}"
                                }
                            }
                        }
                    }
                }
            }

            is QuizQuestion.Translation,
            is QuizQuestion.NextLine,
            is QuizQuestion.WordMeaning,
            is QuizQuestion.BaniAuthor,
            is QuizQuestion.BaniAng -> {

                val options = when (question) {
                    is QuizQuestion.Translation -> question.options
                    is QuizQuestion.NextLine -> question.options
                    is QuizQuestion.WordMeaning -> question.options
                    is QuizQuestion.BaniAuthor -> question.options
                    is QuizQuestion.BaniAng -> question.options
                    else -> emptyList()
                }

                val answer = when (question) {
                    is QuizQuestion.Translation -> question.answer
                    is QuizQuestion.NextLine -> question.answer
                    is QuizQuestion.WordMeaning -> question.answer
                    is QuizQuestion.BaniAuthor -> question.answer
                    is QuizQuestion.BaniAng -> question.answer
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
                            hearts--
                            val questionText = when (question) {
                                is QuizQuestion.Translation -> question.line
                                is QuizQuestion.NextLine -> question.currentLine
                                is QuizQuestion.WordMeaning -> "Meaning of: ${question.line}"
                                is QuizQuestion.BaniAuthor -> "Author of: ${question.baniName}"
                                is QuizQuestion.BaniAng -> "Starting Ang of: ${question.baniName}"
                                else -> "Q: ".translate(displayLanguage)
                            }
                            val reviewQuestion = when (question) {

                            is QuizQuestion.Translation ->
                                "Translation Match\n\n${question.line}"

                            is QuizQuestion.NextLine ->
                                "Next Line\n\n${question.currentLine}"

                            is QuizQuestion.WordMeaning ->
                                "Word Meaning\n\n${question.line}"

                            is QuizQuestion.BaniAuthor ->
                                "Author Question\n\n${question.baniName}"

                            is QuizQuestion.BaniAng ->
                                "Ang Question\n\n${question.baniName}"

                            else ->
                                questionText
}

wrongAnswers =
    wrongAnswers + "$reviewQuestion:::$answer"
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
                                        hearts--
                                        wrongAnswers =
    wrongAnswers +
        "True or False\n\n${question.statement}:::${
            if (question.answer)
                "True".translate(displayLanguage)
            else
                "False".translate(displayLanguage)
        }"
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
                                            hearts--

                                            wrongAnswers =
    wrongAnswers +
        "Arrange Sequence\n\n${question.hint}\n\n:::${question.originalWords.joinToString(" ")}"
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
    }
}

    if (showContinueQuizDialog) {
        AlertDialog(
            onDismissRequest = {
                showContinueQuizDialog = false
                questionIndex = 0
                score = 0
                mistakes = 0
                hearts = 5
                continueNoHeartsMode = false
                quizFinished = false
                wrongAnswers = emptyList()
            },
            title = {
                Text(
                    text = "Out of Lives!".translate(displayLanguage),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
               Column(

    modifier = Modifier.fillMaxWidth(),

    horizontalAlignment =
        Alignment.CenterHorizontally,

    verticalArrangement =
        Arrangement.spacedBy(8.dp)
) {

    MascotBani(

        state = MascotState.Oops,

        modifier = Modifier.size(120.dp)
    )

    Text(

        text =
            "Out of hearts"
                .translate(displayLanguage),

        color =
            colors.textPrimary,

        fontSize = 20.sp,

        fontWeight =
            FontWeight.Bold,

        textAlign =
            TextAlign.Center
    )

    Text(

        text =
            "You have no hearts remaining."
                .translate(displayLanguage),

        color =
            colors.textSecondary,

        fontSize = 14.sp,

        textAlign =
            TextAlign.Center
    )

    Text(

        text =
            "Restart the quiz or continue in No-Hearts mode."
                .translate(displayLanguage),

        color =
            colors.textSecondary,

        fontSize = 13.sp,

        lineHeight = 20.sp,

        textAlign =
            TextAlign.Center,

        modifier = Modifier
            .padding(horizontal = 12.dp)
    )
}
            },
            confirmButton = {
                Button(
                    onClick = {
                        showContinueQuizDialog = false
                        quizFinished = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("View Score".translate(displayLanguage), color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        continueNoHeartsMode = true
                        showContinueQuizDialog = false
                        if (!isLastQuestion) {
                            questionIndex++
                        } else {
                            quizFinished = true
                        }
                    }
                ) {
                    Text("Continue".translate(displayLanguage), color = colors.textSecondary, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = colors.card
        )
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

        answered && correct -> {
            colors.success.copy(alpha = 0.24f)
        }

        answered && selected && !correct -> {
            colors.error.copy(alpha = 0.24f)
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

        answered && correct -> {
            colors.success.copy(alpha = 0.24f)
        }

        answered && selected && !correct -> {
            colors.error.copy(alpha = 0.24f)
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

        answered && correct -> {
            colors.success.copy(alpha = 0.24f)
        }

        answered && selected && !correct -> {
            colors.error.copy(alpha = 0.24f)
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
