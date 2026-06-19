package org.audiocular.nitnem.models

sealed class QuizQuestion {

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

    data class WordMeaning(
        val line: String,
        val answer: String,
        val options: List<String>
    ) : QuizQuestion()

    data class BaniAuthor(
        val baniName: String,
        val answer: String,
        val options: List<String>
    ) : QuizQuestion()

    data class BaniAng(
        val baniName: String,
        val answer: String,
        val options: List<String>
    ) : QuizQuestion()
}

data class QuizSession(
    val baniId: String,
    val questions: List<QuizQuestion>,
    val questionIndex: Int = 0,
    val score: Int = 0,
    val mistakes: Int = 0,
    val hearts: Int = 5,
    val quizFinished: Boolean = false,
    val wrongAnswers: List<String> = emptyList(),
    val continueNoHeartsMode: Boolean = false
)
