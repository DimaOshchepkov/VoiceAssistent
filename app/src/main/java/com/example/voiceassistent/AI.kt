package com.example.voiceassistent

import com.example.voiceassistent.forecast.ForecastToString
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern

class AI {
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("ru"))

    private fun getRussianDayOfWeek(dayOfWeek: DayOfWeek): String =
        when (dayOfWeek.toString().lowercase(Locale.getDefault())) {
        "monday" -> "Понедельник"
        "tuesday" -> "Вторник"
        "wednesday" -> "Среда"
        "thursday" -> "Четверг"
        "friday" -> "Пятница"
        "saturday" -> "Суббота"
        "sunday" -> "Воскресенье"
        else -> "Непредвиденная ошибка при переводе"
    }

    private val requestsAndResponses: Map<String, () -> String> = mapOf(
        "привет" to { "привет" },
        "как дела" to { "не плохо" },
        "чем занимаешься" to { "отвечаю на вопросы" },
        "какой сегодня день" to { LocalDate.now().format(dateFormatter).toString() },
        "который час" to { LocalTime.now().format(timeFormatter).toString() },
        "какой день недели" to { getRussianDayOfWeek(DayOfWeek.from(LocalDate.now())).toString() },
        "сколько дней до зачёта" to { daysUntilExam(LocalDate.of(2024, Month.JUNE, 1)).toString() }
    )

    fun getAnswer(prompt: String, answerCallBack: Consumer<String?>): Unit {
        val promptLower = prompt.lowercase()

        requestsAndResponses[promptLower]?.invoke()?.let {answerCallBack.accept(it) }

        val cityPattern = Pattern.compile("погода в городе (\\p{L}+)", Pattern.CASE_INSENSITIVE)
        val matcher = cityPattern.matcher(promptLower)

        if (matcher.find()) {
            val cityName = matcher.group(1)
            ForecastToString().getForecast(cityName, Consumer { forecast ->
                answerCallBack.accept(forecast)
            })
        } else {

        }
    }

    private fun daysUntilExam(examDate: LocalDate): Long {
        val currentDate = LocalDate.now()
        return ChronoUnit.DAYS.between(currentDate, examDate)
    }
}