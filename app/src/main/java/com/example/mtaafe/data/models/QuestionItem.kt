package com.example.mtaafe.data.models

import java.time.LocalDateTime

data class QuestionItem (
    var id: Long,
    var title: String,
    var body: String,
    var createdAt: LocalDateTime,
    var author: Author,
    var tags: List<Tag>,
    var answersCount: Int
)