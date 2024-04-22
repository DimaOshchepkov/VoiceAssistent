package com.example.voiceassistent.holidays

import android.os.Build
import androidx.annotation.RequiresApi
import okhttp3.internal.toImmutableList
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

class ParsingHtmlService {
    private var URL = "http:/mirkosmosa.ru/holiday/2024"

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHoliday(date: String): String {
        var holidays: String = ""
        val days: List<String> = getDate(date)

        val document: Document = Jsoup.connect(URL).get()

        val body: Element = document.body()
        val daysOfMonth: Elements = body.select("#holiday_calend > div > div > div")
        for (dayInMonth in daysOfMonth) {
            for (day in days) {
                if (dayInMonth.getElementsByTag("span")[0].text() == day) {
                    val holiday = dayInMonth.getElementsByTag("a")
                    holidays = holidays.plus("$day : ")
                    for (h in holiday) {
                        holidays = holidays.plus(h.text() + ", ")
                    }
                    holidays = holidays.dropLast(2)
                    holidays = holidays.plus("\n")
                }
            }

        }
        return if (holidays == "")
            "хз"
        else holidays.dropLast(1)

    }


    private val digitDateFormat = "\\b(\\d{1,2}[.\\/]\\d{1,2}[.\\/]\\d{4})\\b"
    private val wordDateFormat =
        "\\b(\\d{1,2}\\s+(?:января|февраля|марта|апреля|мая|июня|июля|августа|сентября|октября|ноября|декабря)\\s+\\d{4})\\b"
    private val relativeDateFormat = "(завтра|вчера|сегодня)"

    private val patternDate: Pattern = Pattern.compile(
        "$digitDateFormat|$wordDateFormat|$relativeDateFormat",
        Pattern.CASE_INSENSITIVE
    )
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDate(prompt: String): List<String> {


        val matcherDate = patternDate.matcher(prompt)

        val dates = mutableListOf<String>()
        val currentDate = LocalDate.now()
        val formatterLongDateFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy")

        while (matcherDate.find()) {
            when (val dateMatch = matcherDate.group()) {
                "вчера" -> dates.add(currentDate.minusDays(1).format(formatterLongDateFormat))
                "сегодня" -> dates.add(currentDate.format(formatterLongDateFormat))
                "завтра" -> dates.add(currentDate.plusDays(1).format(formatterLongDateFormat))
                else -> {
                    val formattedDate = if (dateMatch.matches(Regex(digitDateFormat))) {
                        if (dateMatch[0] == '0')
                            dateMatch.substring(1).trim().format(formatterLongDateFormat)
                        else
                            dateMatch.trim().format(formatterLongDateFormat)
                    } else {
                        if (dateMatch[0] == '0')
                            dateMatch.substring(1).trim()
                        else
                            dateMatch
                    }

                    dates.add(formattedDate)
                }
            }
        }

        return dates.toImmutableList()
    }
}