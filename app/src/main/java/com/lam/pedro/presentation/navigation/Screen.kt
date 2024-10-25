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
enum class Screen(val route: String, val titleId: Int, val hasMenuItem: Boolean = true) {
    WelcomeScreen("welcome_screen", R.string.welcome_screen, false),
    HomeScreen("home_screen", R.string.home_screen),
    ExerciseSessions("exercise_sessions", R.string.exercise_sessions, false),
    ExerciseSessionDetail("exercise_session_detail", R.string.exercise_session_detail, false),
    SleepSessions("sleep_sessions", R.string.sleep_sessions, false),
    SleepSessionDetail("sleep_session_detail", R.string.sleep_session_detail, false),
    WeightScreen("weight_screen", R.string.input_readings, false),
    DifferentialChanges("differential_changes", R.string.differential_changes, false),
    PrivacyPolicy("privacy_policy", R.string.privacy_policy, false),
    HealthConnectScreen("health_connect_screen", R.string.health_connect, false),
    ActivitiesScreen("activity_list", R.string.activity_list),
    MoreScreen("more_screen", R.string.more_screen),
    RecordListScreen("record_list", R.string.record_list, false),
    AboutScreen("about_screen", R.string.about_screen, false),
    SettingScreen("setting_screen", R.string.setting_screen, false),
    LandingScreen("landing_screen", R.string.landing_screen, false),
    LoginScreen("login_screen", R.string.login_screen, false)
}
