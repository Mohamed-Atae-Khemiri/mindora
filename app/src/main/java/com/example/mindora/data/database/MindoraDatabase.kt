package com.example.mindora.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mindora.data.dao.StudyActivityDao
import com.example.mindora.data.dao.StudyGoalDao
import com.example.mindora.data.dao.StudentProfileDao
import com.example.mindora.data.dao.SubjectDao
import com.example.mindora.data.dao.TaskDao
import com.example.mindora.data.entity.StudyActivity
import com.example.mindora.data.entity.StudyActivityDay
import com.example.mindora.data.entity.StudyGoal
import com.example.mindora.data.entity.StudyMilestone
import com.example.mindora.data.entity.StudentProfile
import com.example.mindora.data.entity.Subject
import com.example.mindora.data.entity.Task

@Database(
    entities = [
        Subject::class,
        Task::class,
        StudyGoal::class,
        StudentProfile::class,
        StudyActivityDay::class,
        StudyActivity::class,
        StudyMilestone::class
    ],
    version = 4,
    exportSchema = false
)
abstract class MindoraDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun studyGoalDao(): StudyGoalDao
    abstract fun studentProfileDao(): StudentProfileDao
    abstract fun studyActivityDao(): StudyActivityDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS student_profiles (" +
                        "id INTEGER NOT NULL, fullName TEXT NOT NULL, email TEXT NOT NULL, " +
                        "phoneNumber TEXT NOT NULL, university TEXT NOT NULL, fieldOfStudy TEXT NOT NULL, " +
                        "academicYear TEXT NOT NULL, avatarUri TEXT, PRIMARY KEY(id))"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS study_activity_days (" +
                        "epochDay INTEGER NOT NULL, PRIMARY KEY(epochDay))"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS study_milestones (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "streakStartEpochDay INTEGER NOT NULL, milestoneDays INTEGER NOT NULL, " +
                        "celebrated INTEGER NOT NULL)"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_study_milestones_streakStartEpochDay_milestoneDays " +
                        "ON study_milestones(streakStartEpochDay, milestoneDays)"
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS study_activities (" +
                        "activityId TEXT NOT NULL, occurredAtEpochMillis INTEGER NOT NULL, " +
                        "localEpochDay INTEGER NOT NULL, timeZoneId TEXT NOT NULL, source TEXT NOT NULL, " +
                        "sourceId TEXT, idempotencyKey TEXT NOT NULL, PRIMARY KEY(activityId))"
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_study_activities_idempotencyKey " +
                        "ON study_activities(idempotencyKey)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_study_activities_localEpochDay " +
                        "ON study_activities(localEpochDay)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_study_activities_occurredAtEpochMillis " +
                        "ON study_activities(occurredAtEpochMillis)"
                )
                // The legacy day-only table is intentionally retained and never converted
                // into timestamped history because it has no reliable event time.
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE student_profiles ADD COLUMN age INTEGER")
                db.execSQL("ALTER TABLE student_profiles ADD COLUMN classGroup TEXT NOT NULL DEFAULT ''")
            }
        }

        @Volatile
        private var INSTANCE: MindoraDatabase? = null

        fun createDatabase(context: Context, name: String): MindoraDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                MindoraDatabase::class.java,
                name
            )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build()

        fun getDatabase(context: Context): MindoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = createDatabase(context, "mindora_database")
                INSTANCE = instance
                instance
            }
        }
    }
}
