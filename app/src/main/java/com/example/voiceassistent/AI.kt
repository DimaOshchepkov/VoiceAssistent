package com.example.voiceassistent

import android.annotation.SuppressLint
import android.os.Build
import io.reactivex.rxjava3.core.Observable
import androidx.annotation.RequiresApi
import com.example.voiceassistent.cityinformation.CityToString
import com.example.voiceassistent.citytips.BeginningToString
import com.example.voiceassistent.forecast.ForecastToString
import com.example.voiceassistent.holidays.ParsingHtmlService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
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


    @SuppressLint("CheckResult")
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAnswer(prompt: String, answerCallBack: Consumer<String?>): Unit {
        val promptLower = prompt.lowercase()

        requestsAndResponses[promptLower]?.invoke()?.let {answerCallBack.accept(it) }

        val weatherInCity = Pattern.compile("погода в городе (\\p{L}+)", Pattern.CASE_INSENSITIVE)
        val matcherCity = weatherInCity.matcher(promptLower)

        val infoCity = Pattern.compile("информация о городе (\\p{L}+)", Pattern.CASE_INSENSITIVE)
        val matcherInfoCity  = infoCity.matcher(promptLower)

        val beginningCity = Pattern.compile("город на (\\p{L}+)", Pattern.CASE_INSENSITIVE)
        val matcherBeginningCity = beginningCity.matcher(promptLower)

        val holidayPrompt = Pattern.compile("какой праздник (.*?)[.!?]", Pattern.CASE_INSENSITIVE)
        val matcherDateHolidays = holidayPrompt.matcher("$promptLower.")

        if (matcherCity.find()) {
            val cityName = matcherCity.group(1)
            ForecastToString().getForecast(cityName, Consumer { forecast ->
                answerCallBack.accept(forecast)
            })
        }
        else if (matcherInfoCity.find()) {
            val cityName = matcherInfoCity.group(1)
            CityToString().getCityInformaion(cityName, Consumer { info ->
                answerCallBack.accept(info)
            })
        }
        else if (matcherBeginningCity.find()) {
            val cityBeginning = matcherBeginningCity.group(1)
            BeginningToString().getCityInformation(cityBeginning, Consumer { info ->
                if (info != null) {
                    answerCallBack.accept(info.joinToString ("\n"))
                }
            })
        }
        else if (matcherDateHolidays.find()) {
            val dateHolidays = matcherDateHolidays.group(1)
            var answer= ""
            Observable.fromCallable {
                answer = ParsingHtmlService().getHoliday(dateHolidays)
                return@fromCallable answer
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    answerCallBack.accept(answer)
                }


        }
        else {
            answerCallBack.accept("Не понимаю вас")
        }


    }

    private fun daysUntilExam(examDate: LocalDate): Long {
        val currentDate = LocalDate.now()
        return ChronoUnit.DAYS.between(currentDate, examDate)
    }
}