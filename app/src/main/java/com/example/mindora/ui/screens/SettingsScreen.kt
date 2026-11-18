package com.example.mindora.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.mindora.data.entity.StudentProfile
import com.example.mindora.ui.components.PandaMood
import com.example.mindora.ui.components.PandaStudyMascot
import com.example.mindora.ui.components.MindoraFeatureShape
import com.example.mindora.ui.components.MindoraPage
import com.example.mindora.ui.components.MindoraSectionHeading
import com.example.mindora.ui.components.MindoraSurfaceShape
import com.example.mindora.ui.components.StudentAvatar
import com.example.mindora.ui.theme.AppSurface
import com.example.mindora.ui.theme.CoralAccent
import com.example.mindora.ui.theme.DeepMidnightPurple
import com.example.mindora.ui.theme.ElectricLavender
import com.example.mindora.ui.theme.LimeAccent
import com.example.mindora.ui.theme.TextPrimary
import com.example.mindora.ui.theme.TextSecondary
import com.example.mindora.viewmodel.StudentProfileViewModel

private val academicYears = listOf("Year 1", "Year 2", "Year 3", "Year 4", "Year 5+", "Graduate")

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    viewModel: StudentProfileViewModel
) {
    val context = LocalContext.current
    val profile by viewModel.profile.collectAsState()
    val saveState by viewModel.state.collectAsState()
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf("") }
    var university by rememberSaveable { mutableStateOf("") }
    var fieldOfStudy by rememberSaveable { mutableStateOf("") }
    var academicYear by rememberSaveable { mutableStateOf("") }
    var classGroup by rememberSaveable { mutableStateOf("") }
    var isEditing by rememberSaveable { mutableStateOf(true) }
    var avatarUri by rememberSaveable { mutableStateOf<String?>(null) }
    var avatarPickerError by rememberSaveable { mutableStateOf<String?>(null) }
    var yearMenuExpanded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(profile) {
        fullName = profile?.fullName.orEmpty()
        email = profile?.email.orEmpty()
        phone = profile?.phoneNumber.orEmpty()
        age = profile?.age?.toString().orEmpty()
        university = profile?.university.orEmpty()
        fieldOfStudy = profile?.fieldOfStudy.orEmpty()
        academicYear = profile?.academicYear.orEmpty()
        classGroup = profile?.classGroup.orEmpty()
        avatarUri = profile?.avatarUri
        isEditing = profile == null
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { selected ->
            val permissionSaved = runCatching {
                context.contentResolver.takePersistableUriPermission(
                    selected,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }.isSuccess
            if (permissionSaved) {
                avatarUri = selected.toString()
                avatarPickerError = null
            } else {
                avatarPickerError = "Mindora couldn't keep access to that photo. Please choose another."
            }
        }
    }

    val iconAccent by animateColorAsState(
        targetValue = if (isDarkMode) LimeAccent else CoralAccent,
        animationSpec = tween(260),
        label = "appearanceAccent"
    )

    MindoraPage(
        eyebrow = "Your profile",
        title = if (profile == null) "Make it yours." else "Your space.",
        description = "Set up your student identity and the way Mindora feels."
    ) {
        if (profile == null || isEditing) {
        item(key = "profile-identity") {
            Column(
                Modifier.fillMaxWidth()
                    .clip(MindoraFeatureShape)
                    .background(DeepMidnightPurple)
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StudentAvatar(profile, size = 76.dp, avatarUriOverride = avatarUri)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            if (fullName.isBlank()) "YOUR STUDY ID" else fullName.uppercase(),
                            color = ElectricLavender,
                            fontSize = 10.sp,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            if (profile == null) "Start with your name." else "A little more you.",
                            color = Color.White,
                            fontFamily = FontFamily.Serif,
                            fontSize = 22.sp,
                            lineHeight = 27.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { imagePicker.launch(arrayOf("image/*")) },
                        modifier = Modifier.height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricLavender,
                            contentColor = DeepMidnightPurple
                        )
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Choose a photo", fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.width(12.dp))
                    PandaStudyMascot(size = 50.dp, mood = PandaMood.STUDYING)
                    Text("Panda Study is here to cheer you on.", color = Color.White.copy(alpha = .8f), fontSize = 11.sp)
                }
                if (avatarUri != null) {
                    TextButton(
                        onClick = {
                            avatarUri = null
                            avatarPickerError = null
                        },
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Remove photo", color = Color.White)
                    }
                }
                avatarPickerError?.let { error ->
                    Text(error, color = CoralAccent, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }

        item(key = "identity-heading") {
            MindoraSectionHeading("A little about you", "Your name and email are required.")
        }
        item(key = "identity-fields") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = "Full name",
                    error = saveState.errors.fullName,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )
                ProfileField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email address",
                    error = saveState.errors.email,
                    keyboardType = KeyboardType.Email,
                    modifier = Modifier.fillMaxWidth()
                )
                ProfileField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone (optional)",
                    error = saveState.errors.phoneNumber,
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier.fillMaxWidth()
                )
                ProfileField(
                    value = age,
                    onValueChange = { age = it.filter(Char::isDigit).take(3) },
                    label = "Age (optional)",
                    error = saveState.errors.age,
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        item(key = "campus-heading") {
            MindoraSectionHeading("Your campus", "A few details help personalize your study space.")
        }
        item(key = "campus-fields") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileField(
                    value = university,
                    onValueChange = { university = it },
                    label = "University",
                    error = saveState.errors.university,
                    modifier = Modifier.fillMaxWidth()
                )
                ProfileField(
                    value = fieldOfStudy,
                    onValueChange = { fieldOfStudy = it },
                    label = "Field of study",
                    error = saveState.errors.fieldOfStudy,
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(
                    expanded = yearMenuExpanded,
                    onExpandedChange = { yearMenuExpanded = !yearMenuExpanded }
                ) {
                    OutlinedTextField(
                        value = academicYear,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Academic year") },
                        isError = saveState.errors.academicYear != null,
                        supportingText = saveState.errors.academicYear?.let { error -> { Text(error) } },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearMenuExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        singleLine = true
                    )
                    ExposedDropdownMenu(
                        expanded = yearMenuExpanded,
                        onDismissRequest = { yearMenuExpanded = false }
                    ) {
                        academicYears.forEach { year ->
                            DropdownMenuItem(
                                text = { Text(year) },
                                onClick = {
                                    academicYear = year
                                    yearMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                ProfileField(
                    value = classGroup,
                    onValueChange = { classGroup = it },
                    label = "Class / group (optional)",
                    error = null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        item(key = "profile-save") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        viewModel.save(
                            fullName = fullName,
                            email = email,
                            phoneNumber = phone,
                            university = university,
                            fieldOfStudy = fieldOfStudy,
                            academicYear = academicYear,
                            avatarUri = avatarUri,
                            age = age,
                            classGroup = classGroup
                        )
                    },
                    enabled = !saveState.isSaving,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepMidnightPurple),
                    shape = MindoraSurfaceShape
                ) {
                    Text(
                        when {
                            saveState.isSaving -> "Saving profile..."
                            profile == null -> "Save my profile"
                            else -> "Save changes"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (saveState.saved) {
                    Text("Your student profile is saved on this device.", color = TextSecondary, fontSize = 13.sp)
                }
                saveState.saveError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp) }
            }
        }
        item(key = "local-profile-note") {
            Row(
                Modifier.fillMaxWidth().clip(MindoraSurfaceShape).background(AppSurface).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PandaStudyMascot(size = 42.dp, mood = PandaMood.ENCOURAGING, animate = false)
                Spacer(Modifier.width(12.dp))
                Text(
                    "Profile details stay on this device. Mindora has no sign-in provider configured, so this email is not an authenticated account.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
        } else {
            item(key = "profile-summary") {
                ProfileSummary(
                    profile = profile!!,
                    showSavedMessage = saveState.saved,
                    onEdit = {
                        isEditing = true
                        viewModel.clearSavedMessage()
                    }
                )
            }
        }
        item(key = "appearance-heading") {
            MindoraSectionHeading("Make Mindora yours", "Your appearance choice is saved on this device.")
        }
        item(key = "appearance-feature") {
            Row(
                modifier = Modifier.fillMaxWidth().clip(MindoraFeatureShape)
                    .background(DeepMidnightPurple).padding(horizontal = 18.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(Modifier.size(48.dp).clip(CircleShape).background(iconAccent), contentAlignment = Alignment.Center) {
                    Icon(if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode, null, tint = DeepMidnightPurple)
                }
                Column(Modifier.weight(1f)) {
                    Text(if (isDarkMode) "Night, made gentle." else "A little more daylight.", color = Color.White, fontFamily = FontFamily.Serif, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                    Text("Night mode", color = Color.White.copy(alpha = .8f), fontSize = 13.sp)
                }
                Switch(checked = isDarkMode, onCheckedChange = onDarkModeChange)
            }
        }
    }
}

@Composable
private fun ProfileSummary(
    profile: StudentProfile,
    showSavedMessage: Boolean,
    onEdit: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(
            Modifier.fillMaxWidth()
                .clip(MindoraFeatureShape)
                .background(DeepMidnightPurple)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StudentAvatar(profile, size = 92.dp)
            Spacer(Modifier.height(12.dp))
            Text(
                profile.fullName,
                color = Color.White,
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                lineHeight = 29.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(profile.email, color = ElectricLavender, fontSize = 14.sp)
        }

        Column(
            Modifier.fillMaxWidth()
                .clip(MindoraSurfaceShape)
                .background(AppSurface)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MindoraSectionHeading("Student details", "Saved on this device")
            ProfileDetailRow("University / school", profile.university)
            ProfileDetailRow("Field of study", profile.fieldOfStudy)
            ProfileDetailRow("Academic year", profile.academicYear)
            profile.phoneNumber.takeIf(String::isNotBlank)?.let {
                ProfileDetailRow("Phone", it)
            }
            profile.age?.let { ProfileDetailRow("Age", it.toString()) }
            profile.classGroup.takeIf(String::isNotBlank)?.let {
                ProfileDetailRow("Class / group", it)
            }
        }

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepMidnightPurple),
            shape = MindoraSurfaceShape
        ) {
            Text("Edit profile", color = Color.White, fontWeight = FontWeight.Bold)
        }

        if (showSavedMessage) {
            Text(
                "Your profile has been saved on this device.",
                color = TextSecondary,
                fontSize = 13.sp
            )
        }

        Row(
            Modifier.fillMaxWidth().clip(MindoraSurfaceShape).background(AppSurface).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PandaStudyMascot(size = 42.dp, mood = PandaMood.ENCOURAGING, animate = false)
            Spacer(Modifier.width(12.dp))
            Text(
                "This is a local profile, not an authenticated account. Sign-in and cross-device sync require Firebase setup.",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        Text(value, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ProfileField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = error?.let { message -> { Text(message) } },
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        modifier = modifier
    )
}
