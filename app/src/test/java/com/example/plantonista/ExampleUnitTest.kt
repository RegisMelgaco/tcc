package com.example.plantonista

import com.example.plantonista.state.Event
import com.example.plantonista.state.GivePositionEvent
import com.google.gson.Gson
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        val body = Gson().toJson(GivePositionEvent("eu", 123))
        assertEquals(body, """{"author":"eu","createdAt":123}""")
        assertEquals(4, 2 + 2)
    }
}