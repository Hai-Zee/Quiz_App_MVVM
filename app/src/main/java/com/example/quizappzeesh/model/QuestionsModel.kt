package com.example.quizappzeesh.model

data class QuestionsModel(

//        val quesID: String = "",
        var question: String = "",
        var option_a: String = "",
        var option_b: String = "",
        var option_c: String = "",
        var option_d: String = "",
        var answer: String = "",
        var timer: Int = 0
)