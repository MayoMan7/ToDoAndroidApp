package com.example.myfirstapp

import java.util.Date // Import the correct Date class

data class Entry (
    val title: String,
    var status: Status,
)

enum class Status {
    Done,
    Pending,
    All
}

