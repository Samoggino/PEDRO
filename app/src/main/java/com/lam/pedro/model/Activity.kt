package com.lam.pedro.model

// Classe astratta per le attività
open class Activity(
    open val name: String,
    open val id: String,
    open val startTime: String = "",
    open val endTime: String = "",
    open val duration: String = ""
)

// Sottoclasse per le attività con movimento
open class MovingActivity(
    name: String,
    id: String,
    startTime: String = "",
    endTime: String = "",
    duration: String = ""
) : Activity(name, id, startTime, endTime, duration)

// Sottoclasse per le attività statiche
open class StaticActivity(
    name: String,
    id: String,
    startTime: String = "",
    endTime: String = "",
    duration: String = ""
) : Activity(name, id, startTime, endTime, duration)

// Sottoclasse per Walking
data class Walking(
    val steps: Int,
    override val name: String = "Walking",
    override val id: String = "1",
    override val startTime: String = "",
    override val endTime: String = "",
    override val duration: String = ""
) : MovingActivity(name, id, startTime, endTime, duration)

// Sottoclasse per Driving
data class Driving(
    val distance: Double, // distanza percorsa in km
    override val name: String = "Driving",
    override val id: String = "2",
    override val startTime: String = "",
    override val endTime: String = "",
    override val duration: String = ""
) : MovingActivity(name, id, startTime, endTime, duration)

// Sottoclasse per Sitting
data class Sitting(
    val durationSitting: String, // durata in cui si è stati seduti
    override val name: String = "Sitting",
    override val id: String = "3",
    override val startTime: String = "",
    override val endTime: String = "",
    override val duration: String = ""
) : StaticActivity(name, id, startTime, endTime, duration)

// Sottoclasse per Jumping Rope
data class JumpRope(
    val jumps: Int, // numero di salti
    override val name: String = "Jumping Rope",
    override val id: String = "4",
    override val startTime: String = "",
    override val endTime: String = "",
    override val duration: String = ""
) : StaticActivity(name, id, startTime, endTime, duration)

// Sottoclasse per Yoga
data class Yoga(
    val poses: Int, // numero di pose praticate
    override val name: String = "Yoga",
    override val id: String = "5",
    override val startTime: String = "",
    override val endTime: String = "",
    override val duration: String = ""
) : StaticActivity(name, id, startTime, endTime, duration)

// Sottoclasse per Running
data class Running(
    val distance: Double, // distanza percorsa in km
    val steps: Int, // numero di passi
    override val name: String = "Running",
    override val id: String = "6",
    override val startTime: String = "",
    override val endTime: String = "",
    override val duration: String = ""
) : MovingActivity(name, id, startTime, endTime, duration)

