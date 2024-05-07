package org.hyperskill.phrases

import kotlin.random.Random

class Book {

    private val random = Random
    fun generatePhrases(): MutableList<Phrase>{
        val list = mutableListOf<Phrase>()
        for (i in phrases.indices) {
            list.add(
                Phrase(phrases[i])
            )
        }
        return list
    }

    companion object {
        val phrases = listOf("Yes you can", "Just do it", "Do it", "Nothing is impossible", "Make you dream come true")
    }

}