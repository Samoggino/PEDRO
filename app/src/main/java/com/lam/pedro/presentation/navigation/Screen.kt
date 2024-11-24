/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lam.pedro.presentation.navigation

import androidx.compose.ui.graphics.Color
import androidx.health.connect.client.records.ExerciseSessionRecord
import com.lam.pedro.R


const val UID_NAV_ARGUMENT = "uid"
const val RECORD_TYPE = "recordType"
const val SERIES_RECORDS_TYPE = "seriesRecordsType"

/**
 * Represent all Screens in the app.
 *
 * @param route The route string used for Compose navigation
 * @param titleId The ID of the string resource to display as a title
 * @param hasMenuItem Whether this Screen should be shown as a menu item in the left-hand menu (not
 *     all screens in the navigation graph are intended to be directly reached from the menu).
 */
enum class Screen(val route: String, val titleId: Int, val image: Int, val color: Color, val activityType: Int, val hasMenuItem: Boolean = true, ) {
    WelcomeScreen("welcome_screen", R.string.welcome_screen, 1, Color(0xFFfaaf5a), 1, false),
    HomeScreen("home_screen", R.string.home_screen, 1, Color(0xFFfaaf5a), 1),
    ProfileScreen("profile_screen", R.string.profile_screen, 1, Color(0xFFfaaf5a), 1, false),
    ExerciseSessions("exercise_sessions", R.string.exercise_sessions, 1, Color(0xFFfaaf5a), 1, false),
    ExerciseSessionDetail("exercise_session_detail", R.string.exercise_session_detail, 1, Color(0xFFfaaf5a), 1, false),
    SleepSessions("sleep_sessions", R.string.sleep_sessions, R.drawable.sleeping_icon, Color(0xff74c9c6), ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT, false),
    SleepSessionDetail("sleep_session_detail", R.string.sleep_session_detail, 1, Color(0xff74c9c6), 1, false),
    WeightScreen("weight_screen", R.string.input_readings, R.drawable.dumbells_icon, Color(0xFF7771C9), ExerciseSessionRecord.EXERCISE_TYPE_WEIGHTLIFTING, false),
    DriveSessionScreen("drive_screen", R.string.drive_screen, R.drawable.car_icon, Color(0xFF61a6f1), ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT,false),
    DifferentialChanges("differential_changes", R.string.differential_changes, 1, Color(0xFFfaaf5a), 1, false),
    PrivacyPolicy("privacy_policy", R.string.privacy_policy, 1, Color(0xFFfaaf5a), 1, false),
    HealthConnectScreen("health_connect_screen", R.string.health_connect, 1, Color(0xFFfaaf5a), 1, false),
    ActivitiesScreen("activity_list", R.string.activity_list, 1, Color(0xFFfaaf5a), 1),
    MoreScreen("more_screen", R.string.more_screen, 1, Color(0xFFfaaf5a), 1),
    RecordListScreen("record_list", R.string.record_list, 1, Color(0xFFfaaf5a), 1, false),
    AboutScreen("about_screen", R.string.about_screen, 1, Color(0xFFfaaf5a), 1, false),
    SettingScreen("setting_screen", R.string.setting_screen, 1, Color(0xFFfaaf5a), 1, false),
    LandingScreen("landing_screen", R.string.landing_screen, 1, Color(0xFFfaaf5a), 1, false),
    LoginScreen("login_screen", R.string.login_screen, 1, Color(0xFFfaaf5a), 1, false),
    WalkSessionScreen("walk_session_screen", R.string.walk_session_screen, R.drawable.walking_icon, Color(0xFFfaaf5a), ExerciseSessionRecord.EXERCISE_TYPE_WALKING, false),
    RunSessionScreen("run_session_screen", R.string.run_session_screen, R.drawable.running_icon, Color(0xFFf87757), ExerciseSessionRecord.EXERCISE_TYPE_RUNNING, false),
    SitSessionScreen("sit_session_screen", R.string.sit_session_screen, R.drawable.armchair_icon, Color(0xff71c97b), ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT, false),
    ListenSessionScreen("listen_session_screen", R.string.listen_session_screen, R.drawable.headphones_icon, Color(0xFF7199C9), ExerciseSessionRecord.EXERCISE_TYPE_OTHER_WORKOUT, false),
    YogaSessionScreen("yoga_session_screen", R.string.yoga_session_screen, R.drawable.yoga_icon, Color(0xFFad71c9), ExerciseSessionRecord.EXERCISE_TYPE_YOGA, false),
    CycleSessionScreen("cycle_session_screen", R.string.cycle_session_screen, R.drawable.bicycling_icon, Color(0xFFC9B271), ExerciseSessionRecord.EXERCISE_TYPE_BIKING, false),
    TrainSessionScreen("train_session_screen", R.string.train_session_screen, R.drawable.stretching_icon, Color(0xFFC97187), ExerciseSessionRecord.EXERCISE_TYPE_EXERCISE_CLASS, false),
    NewActivityScreen("new_activity_screen", R.string.new_activity_screen, 1, Color(0xFFfaaf5a), 1, false)
}
