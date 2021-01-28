package org.stacks.app.shared

import java.text.SimpleDateFormat
import java.util.*

fun Date.toZuluTime(): String {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ", Locale.US).format(this)
}

fun nextYear(): Date = GregorianCalendar.getInstance().apply {
    add(Calendar.YEAR, 1)
}.time