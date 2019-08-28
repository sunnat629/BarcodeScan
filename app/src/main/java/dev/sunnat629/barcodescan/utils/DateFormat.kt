package dev.sunnat629.barcodescan.utils

import java.text.ParseException
import java.text.SimpleDateFormat

object DateFormat {
    private val simpleDateFormat = SimpleDateFormat("MMM dd yyyy HH:mm")
    private val smallFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    fun smallDateFormat(date: String): String?  {
        return try {
            simpleDateFormat.format(smallFormat.parse(date))
        } catch (ex: ParseException){
            null
        }
    }
}