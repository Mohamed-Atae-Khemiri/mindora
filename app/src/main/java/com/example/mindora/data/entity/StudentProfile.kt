package com.example.mindora.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_profiles")
data class StudentProfile(
    @PrimaryKey val id: Int = 1,
    val fullName: String,
    val email: String,
    val phoneNumber: String = "",
    val university: String,
    val fieldOfStudy: String,
    val academicYear: String,
    val avatarUri: String? = null,
    val age: Int? = null,
    @ColumnInfo(defaultValue = "''") val classGroup: String = ""
)
