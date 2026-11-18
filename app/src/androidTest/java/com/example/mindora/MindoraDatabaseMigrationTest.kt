package com.example.mindora

import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.mindora.data.database.MindoraDatabase
import com.example.mindora.data.entity.StudentProfile
import com.example.mindora.data.entity.StudyActivity
import com.example.mindora.data.repository.StudentProfileRepository
import com.example.mindora.viewmodel.StudentProfileViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class MindoraDatabaseMigrationTest {
    @Test
    fun migrationFromV2PreservesTaskAndProfileAndDoesNotInventActivityHistory() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "migration-test-" + UUID.randomUUID() + ".db"
        context.deleteDatabase(name)

        val oldDb = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(name), null)
        oldDb.execSQL("CREATE TABLE subjects (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, description TEXT NOT NULL, color TEXT NOT NULL)")
        oldDb.execSQL("CREATE TABLE tasks (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL, deadline INTEGER NOT NULL, priority TEXT NOT NULL, type TEXT NOT NULL, isCompleted INTEGER NOT NULL, subjectId INTEGER NOT NULL, FOREIGN KEY(subjectId) REFERENCES subjects(id) ON UPDATE NO ACTION ON DELETE CASCADE)")
        oldDb.execSQL("CREATE INDEX index_tasks_subjectId ON tasks(subjectId)")
        oldDb.execSQL("CREATE TABLE study_goals (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, target INTEGER NOT NULL, completed INTEGER NOT NULL)")
        oldDb.execSQL("CREATE TABLE student_profiles (id INTEGER NOT NULL, fullName TEXT NOT NULL, email TEXT NOT NULL, phoneNumber TEXT NOT NULL, university TEXT NOT NULL, fieldOfStudy TEXT NOT NULL, academicYear TEXT NOT NULL, avatarUri TEXT, PRIMARY KEY(id))")
        oldDb.execSQL("CREATE TABLE study_activity_days (epochDay INTEGER NOT NULL PRIMARY KEY)")
        oldDb.execSQL("CREATE TABLE study_milestones (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, streakStartEpochDay INTEGER NOT NULL, milestoneDays INTEGER NOT NULL, celebrated INTEGER NOT NULL)")
        oldDb.execSQL("CREATE UNIQUE INDEX index_study_milestones_streakStartEpochDay_milestoneDays ON study_milestones(streakStartEpochDay, milestoneDays)")
        oldDb.execSQL("INSERT INTO subjects (id, name, description, color) VALUES (7, 'Biology', '', '#7965E8')")
        oldDb.execSQL("INSERT INTO tasks (id, title, description, deadline, priority, type, isCompleted, subjectId) VALUES (19, 'Lab report', 'Cell study', 1900000000000, 'HIGH', 'ASSIGNMENT', 1, 7)")
        oldDb.execSQL("INSERT INTO student_profiles (id, fullName, email, phoneNumber, university, fieldOfStudy, academicYear, avatarUri) VALUES (1, 'Sam Student', 'sam@example.com', '', 'North University', 'Biology', 'Year 2', NULL)")
        oldDb.execSQL("INSERT INTO study_activity_days (epochDay) VALUES (20000)")
        oldDb.version = 2
        oldDb.close()

        val db = MindoraDatabase.createDatabase(context, name)
        try {
            val task = db.taskDao().getAllTasksWithSubject().first().single()
            assertEquals(19L, task.task.id)
            assertEquals("Lab report", task.task.title)
            assertEquals("Biology", task.subject?.name)
            val migratedProfile = db.studentProfileDao().observeProfile().first()
            assertEquals("Sam Student", migratedProfile?.fullName)
            assertEquals(null, migratedProfile?.age)
            assertEquals("", migratedProfile?.classGroup)
            assertEquals(emptyList<Long>(), db.studyActivityDao().getStudyDaysSnapshot())
        } finally {
            db.close()
            context.deleteDatabase(name)
        }
    }

    @Test
    fun profileAndTimestampedActivityPersistAndIdempotencyKeyDeduplicates() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "persistence-test-" + UUID.randomUUID() + ".db"
        context.deleteDatabase(name)
        var db = MindoraDatabase.createDatabase(context, name)
        try {
            db.studentProfileDao().saveProfile(
                StudentProfile(
                    fullName = "Taylor Student",
                    email = "taylor@example.com",
                    university = "North University",
                    fieldOfStudy = "Biology",
                    academicYear = "Year 3",
                    age = 22,
                    classGroup = "Group B"
                )
            )
            val event = StudyActivity(
                activityId = "activity-1",
                occurredAtEpochMillis = 1_900_000_000_000,
                localEpochDay = 21_000,
                timeZoneId = "Africa/Tunis",
                source = "task_completion",
                sourceId = "task-9",
                idempotencyKey = "completion-9-once"
            )
            db.studyActivityDao().insertActivity(event)
            db.studyActivityDao().insertActivity(event.copy(activityId = "activity-duplicate"))
            assertEquals(1, db.studyActivityDao().observeActivities().first().size)
            db.close()

            db = MindoraDatabase.createDatabase(context, name)
            val persistedProfile = db.studentProfileDao().observeProfile().first()
            assertNotNull(persistedProfile)
            assertEquals("Taylor Student", persistedProfile?.fullName)
            assertEquals(22, persistedProfile?.age)
            assertEquals("Group B", persistedProfile?.classGroup)
            val persisted = db.studyActivityDao().observeActivities().first().single()
            assertEquals(1_900_000_000_000, persisted.occurredAtEpochMillis)
            assertEquals("Africa/Tunis", persisted.timeZoneId)

            db.studentProfileDao().saveProfile(
                persistedProfile!!.copy(fullName = "Taylor Updated", age = 23, classGroup = "Group A")
            )
            db.close()
            db = MindoraDatabase.createDatabase(context, name)
            val updatedProfile = db.studentProfileDao().observeProfile().first()
            assertEquals("Taylor Updated", updatedProfile?.fullName)
            assertEquals(23, updatedProfile?.age)
            assertEquals("Group A", updatedProfile?.classGroup)
        } finally {
            db.close()
            context.deleteDatabase(name)
        }
    }

    @Test
    fun profileViewModelValidatesCreatesAndEditsProfile() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "viewmodel-profile-" + UUID.randomUUID() + ".db"
        context.deleteDatabase(name)
        val db = MindoraDatabase.createDatabase(context, name)
        val viewModel = StudentProfileViewModel(StudentProfileRepository(db.studentProfileDao()))
        try {
            viewModel.save(
                fullName = "",
                email = "not-an-email",
                phoneNumber = "12",
                university = "",
                fieldOfStudy = "",
                academicYear = "",
                avatarUri = null,
                age = "12"
            )
            val errors = viewModel.state.value.errors
            assertNotNull(errors.fullName)
            assertNotNull(errors.email)
            assertNotNull(errors.phoneNumber)
            assertNotNull(errors.university)
            assertNotNull(errors.fieldOfStudy)
            assertNotNull(errors.academicYear)
            assertNotNull(errors.age)

            viewModel.save(
                fullName = "Taylor Student",
                email = "taylor@example.com",
                phoneNumber = "",
                university = "North University",
                fieldOfStudy = "Biology",
                academicYear = "Year 3",
                avatarUri = null,
                age = "22",
                classGroup = "Group B"
            )
            val savedState = withTimeout(5_000) {
                viewModel.state.first { it.saved || it.saveError != null }
            }
            assertEquals(null, savedState.saveError)
            val savedProfile = db.studentProfileDao().observeProfile().first()
            assertEquals("Taylor Student", savedProfile?.fullName)
            assertEquals("taylor@example.com", savedProfile?.email)
            assertEquals(22, savedProfile?.age)
            assertEquals("Group B", savedProfile?.classGroup)

            viewModel.clearSavedMessage()
            viewModel.save(
                fullName = "Taylor Updated",
                email = "taylor@example.com",
                phoneNumber = "",
                university = "North University",
                fieldOfStudy = "Biology",
                academicYear = "Year 3",
                avatarUri = null,
                age = "23",
                classGroup = "Group A"
            )
            val updatedState = withTimeout(5_000) {
                viewModel.state.first { it.saved || it.saveError != null }
            }
            assertEquals(null, updatedState.saveError)
            val updatedProfile = db.studentProfileDao().observeProfile().first()
            assertEquals("Taylor Updated", updatedProfile?.fullName)
            assertEquals(23, updatedProfile?.age)
            assertEquals("Group A", updatedProfile?.classGroup)
        } finally {
            db.close()
            context.deleteDatabase(name)
        }
    }
}
