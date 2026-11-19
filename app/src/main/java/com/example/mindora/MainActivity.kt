package com.example.mindora

import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.mindora.ai.data.FirebaseGeminiAiRepository
import com.example.mindora.ai.viewmodel.AiViewModel
import com.example.mindora.data.database.MindoraDatabase
import com.example.mindora.data.repository.StudentProfileRepository
import com.example.mindora.data.repository.StudyActivityRepository
import com.example.mindora.data.repository.StudyGoalRepository
import com.example.mindora.data.repository.SubjectRepository
import com.example.mindora.data.repository.TaskRepository
import com.example.mindora.ui.MindoraMainScreen
import com.example.mindora.viewmodel.HomeViewModel
import com.example.mindora.viewmodel.ProgressViewModel
import com.example.mindora.viewmodel.StudentProfileViewModel
import com.example.mindora.viewmodel.SubjectViewModel
import com.example.mindora.viewmodel.TaskViewModel
import com.example.mindora.viewmodel.ViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Firebase
        Firebase.initialize(context = this)

        // Configure App Check: Debug provider for development builds, Play Integrity for production releases
        val isDebuggable = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        val appCheckProviderFactory = if (isDebuggable) {
            DebugAppCheckProviderFactory.getInstance()
        } else {
            PlayIntegrityAppCheckProviderFactory.getInstance()
        }
        Firebase.appCheck.installAppCheckProviderFactory(appCheckProviderFactory)

        // Request POST_NOTIFICATIONS permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        val database = MindoraDatabase.getDatabase(applicationContext)
        val subjectRepository = SubjectRepository(database.subjectDao())
        val studyActivityRepository = StudyActivityRepository(database.studyActivityDao())
        val taskRepository = TaskRepository(database.taskDao(), studyActivityRepository)
        val studentProfileRepository = StudentProfileRepository(database.studentProfileDao())
        val studyGoalRepository = StudyGoalRepository(database.studyGoalDao())
        val aiRepository = FirebaseGeminiAiRepository(applicationContext)

        val factory = ViewModelFactory(
            application,
            subjectRepository,
            taskRepository,
            studyGoalRepository,
            studentProfileRepository,
            studyActivityRepository,
            aiRepository,
        )

        val homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
        val subjectViewModel = ViewModelProvider(this, factory)[SubjectViewModel::class.java]
        val taskViewModel = ViewModelProvider(this, factory)[TaskViewModel::class.java]
        val progressViewModel = ViewModelProvider(this, factory)[ProgressViewModel::class.java]
        val studentProfileViewModel = ViewModelProvider(this, factory)[StudentProfileViewModel::class.java]
        val aiViewModel = ViewModelProvider(this, factory)[AiViewModel::class.java]

        setContent {
            MindoraMainScreen(
                homeViewModel = homeViewModel,
                subjectViewModel = subjectViewModel,
                taskViewModel = taskViewModel,
                progressViewModel = progressViewModel,
                studentProfileViewModel = studentProfileViewModel,
                aiViewModel = aiViewModel
            )
        }
    }
}
