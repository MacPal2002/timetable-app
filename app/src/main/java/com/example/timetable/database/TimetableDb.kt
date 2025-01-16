package com.example.timetable.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MessageEntity::class, ScheduleEntity::class], version = 1)
abstract class TimetableDatabase : RoomDatabase() {
    abstract fun timetableDao(): TimetableDao
}
