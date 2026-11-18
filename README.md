# Mindora 🐼

**A smart student productivity and AI-powered study management application.**

Mindora is an Android application designed to help students organize their academic life, manage tasks, track their progress, and build better study habits. It also introduces a local AI assistant to provide intelligent assistance and support students directly from their devices.

## ✨ Features

- **Dashboard:** Overview of tasks, study activities, and progress.
- **Task Management:** Create, organize, and track academic tasks.
- **Subject Management:** Manage subjects and their related tasks.
- **Study Goals:** Set and monitor personal study goals.
- **Progress Tracking:** Visualize study activity and achievements.
- **Study Streak:** Track consistency and maintain daily study habits.
- **Smart Priorities:** Identify tasks that need attention.
- **Notifications:** Receive study reminders.
- **Local AI Assistant:** An integrated AI assistant powered by Qwen3 0.6B, designed to answer questions and help students with their learning activities.
- **Offline AI Processing:** Run AI conversations locally on the device using LiteRT-LM, without relying on cloud AI APIs.
- **Modern UI:** A clean interface with a friendly panda mascot.

## 🤖 Local AI Assistant

Mindora integrates a local AI engine to provide AI-powered assistance directly on Android devices.

**AI Technologies:**
- **Qwen3 0.6B:** Lightweight language model developed by Alibaba.
- **LiteRT-LM:** Runtime for executing the AI model locally.
- **CPU Inference:** Initial execution backend for Android devices.
- **Local Model Storage:** The model is imported and stored on the device.

The assistant is designed to:
- Answer general questions.
- Explain academic concepts.
- Help with programming and technical subjects.
- Support learning and problem-solving.
- Maintain conversation context.

**Note:** The AI model must be installed separately on the device before real AI responses can be generated. Offline inference requires a compatible model file and sufficient device memory.

## 🛠️ Technologies

- Kotlin
- Jetpack Compose
- Android Studio
- Room Database
- MVVM Architecture
- Gradle
- LiteRT-LM
- Qwen3 0.6B

## 🏗️ Architecture

The project follows the MVVM architecture:

- **Data:** Room database, DAOs, entities, and repositories.
- **Domain:** Business logic and use cases.
- **UI:** Jetpack Compose screens, components, and navigation.
- **ViewModel:** Manages UI state and communicates with the data and domain layers.
- **AI Engine:** Handles local model initialization, inference, and conversation management.
- **Model Manager:** Handles importing, validating, and storing the local AI model.

## 🚀 Getting Started

1. Clone this repository:

   ```bash
   git clone https://github.com/nessrineounallah/Mindora.git
   ```

2. Open the project in Android Studio.
3. Allow Gradle to sync.
4. Run the application on an Android emulator or physical device.
5. Open the AI Assistant section.
6. Import a compatible `Qwen3-0.6B.litertlm` model file.
7. Wait for the model to initialize.
8. Start chatting with Mindora AI.

## 📱 Project

**Mindora – Smart Student Productivity & AI-Powered Study Management**

Developed as an Android application project.

## 📱 Project

**Mindora – Smart Student Productivity & Study Management**

Developed as an Android application project.
