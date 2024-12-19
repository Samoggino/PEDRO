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
import com.lam.pedro.R

/**
 * Represent all Screens in the app.
 *
 * @param route The route string used for Compose navigation
 * @param titleId The ID of the string resource to display as a title
 * @param hasMenuItem Whether this Screen should be shown as a menu item in the left-hand menu (not
 *     all screens in the navigation graph are intended to be directly reached from the menu).
 */
enum class Screen(
    val route: String,
    val titleId: Int,
    val image: Int = 1,
    val color: Color = Color(0xFFfaaf5a),
    val hasMenuItem: Boolean = true,
) {
    WelcomeScreen(
        route = "welcome_screen",
        titleId = R.string.welcome_screen,
        hasMenuItem = false
    ),
    HomeScreen(
        route = "home_screen",
        titleId = R.string.home_screen,
    ),
    ProfileScreen(
        route = "profile_screen",
        titleId = R.string.profile_screen,

        hasMenuItem = false
    ),
    ExerciseSessions(
        route = "exercise_sessions",
        titleId = R.string.exercise_sessions,

        hasMenuItem = false
    ),
    ExerciseSessionDetail(
        route = "exercise_session_detail",
        titleId = R.string.exercise_session_detail,

        hasMenuItem = false
    ),
    SleepSessions(
        route = "sleep_sessions",
        titleId = R.string.sleep_sessions,
        R.drawable.sleeping_icon,
        color = Color(0xff74c9c6),
        hasMenuItem = false
    ),
    SleepSessionDetail(
        route = "sleep_session_detail",
        titleId = R.string.sleep_session_detail,
        color = Color(0xff74c9c6),
        hasMenuItem = false
    ),
    WeightScreen(
        route = "weight_screen",
        titleId = R.string.input_readings,
        R.drawable.dumbells_icon,
        color = Color(0xFF7771C9),
        hasMenuItem = false
    ),
    DriveSessionScreen(
        route = "drive_screen",
        titleId = R.string.drive_screen,
        R.drawable.car_icon,
        color = Color(0xFF61a6f1),
        hasMenuItem = false
    ),
    DifferentialChanges(
        route = "differential_changes",
        titleId = R.string.differential_changes,

        hasMenuItem = false
    ),
    PrivacyPolicy(
        route = "privacy_policy",
        titleId = R.string.privacy_policy,

        hasMenuItem = false
    ),
    HealthConnectScreen(
        route = "health_connect_screen",
        titleId = R.string.health_connect,

        hasMenuItem = false
    ),
    ActivitiesScreen(
        route = "activity_list",
        titleId = R.string.activity_list,
    ),
    MoreScreen(
        route = "more_screen",
        titleId = R.string.more_screen,
    ),
    RecordListScreen(
        route = "record_list",
        titleId = R.string.record_list,

        hasMenuItem = false
    ),
    AboutScreen(
        route = "about_screen",
        titleId = R.string.about_screen,

        hasMenuItem = false
    ),
    SettingScreen(
        route = "setting_screen",
        titleId = R.string.setting_screen,

        hasMenuItem = false
    ),
    LandingScreen(
        route = "landing_screen",
        titleId = R.string.landing_screen,

        hasMenuItem = false
    ),
    LoginScreen(
        route = "login_screen",
        titleId = R.string.login_screen,

        hasMenuItem = false
    ),
    WalkSessionScreen(
        route = "walk_session_screen",
        titleId = R.string.walk_session_screen,
        R.drawable.walking_icon,
        color = Color(0xFFfaaf5a),
        hasMenuItem = false
    ),
    RunSessionScreen(
        route = "run_session_screen",
        titleId = R.string.run_session_screen,
        R.drawable.running_icon,
        color = Color(0xFFf87757),
        hasMenuItem = false
    ),
    SitSessionScreen(
        route = "sit_session_screen",
        titleId = R.string.sit_session_screen,
        R.drawable.armchair_icon,
        color = Color(0xff71c97b),
        hasMenuItem = false
    ),
    ListenSessionScreen(
        route = "listen_session_screen",
        titleId = R.string.listen_session_screen,
        R.drawable.headphones_icon,
        color = Color(0xFF7199C9),
        hasMenuItem = false
    ),
    YogaSessionScreen(
        route = "yoga_session_screen",
        titleId = R.string.yoga_session_screen,
        R.drawable.yoga_icon,
        color = Color(0xFFad71c9),
        hasMenuItem = false
    ),
    CycleSessionScreen(
        route = "cycle_session_screen",
        titleId = R.string.cycle_session_screen,
        R.drawable.bicycling_icon,
        color = Color(0xFFC9B271),
        hasMenuItem = false
    ),
    TrainSessionScreen(
        route = "train_session_screen",
        titleId = R.string.train_session_screen,
        R.drawable.stretching_icon,
        color = Color(0xFFC97187),
        hasMenuItem = false
    ),
    NewActivityScreen(
        route = "new_activity_screen",
        titleId = R.string.new_activity_screen,

        hasMenuItem = false
    ),
    MyScreenRecords(
        route = "my_screen_records",
        titleId = R.string.my_screen_records,
        hasMenuItem = false
    ),
    FollowScreen(route = "follow_screen", titleId = R.string.follow_screen, hasMenuItem = false),
    ChartsScreen(route = "charts_screen", titleId = R.string.charts_screen, hasMenuItem = false),
}
