package io.bloco.circles.shared

import com.tfcporciuncula.flow.Preference
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat

fun <T> (() -> Preference<T>).toFlow() =
    asFlow().flatMapConcat { it.asFlow() }
