package com.example.timetable.utils

import com.example.timetable.database.MessageEntity
import com.example.timetable.database.ScheduleEntity
import com.example.timetable.model.Message
import com.example.timetable.model.Schedule

fun Schedule.toEntity(): ScheduleEntity {
    return ScheduleEntity(
        id = this.id,
        day = this.day,
        startTime = this.startTime,
        endTime = this.endTime,
        subject = this.subject,
        classroom = this.classroom,
        teacher = this.teacher,
        course = this.course,
        group = this.group


    )
}

fun ScheduleEntity.toModel(): Schedule {
    return Schedule(
        id = this.id,
        day = this.day,
        startTime = this.startTime,
        endTime = this.endTime,
        subject = this.subject,
        classroom = this.classroom,
        teacher = this.teacher,
        course = this.course,
        group = this.group
    )
}

fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        id = this.id,
        subject = this.subject,
        body = this.body,
        from = this.from,
        date = this.date,
        time = this.time,
        to = this.to,
        read = this.read
    )
}

fun MessageEntity.toModel(): Message {
    return Message(
        id = this.id,
        subject = this.subject,
        body = this.body,
        from = this.from,
        date = this.date,
        time = this.time,
        to = this.to,
        read = this.read
    )
}

