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
    val hasMenuItem: Boolean = false,
) {
    WelcomeScreen(route = "welcome_screen", titleId = R.string.welcome_screen),
    HomeScreen(route = "home_screen", titleId = R.string.home_screen, hasMenuItem = true),
    ActivitiesScreen(route = "activity_list", titleId = R.string.activity_list, hasMenuItem = true),
    CommunityScreen(
        route = "community_screen",
        titleId = R.string.community_screen,
        hasMenuItem = true
    ),
    MoreScreen(route = "more_screen", titleId = R.string.more_screen, hasMenuItem = true),
    ProfileScreen(route = "profile_screen", titleId = R.string.profile_screen),
    ExerciseSessions(route = "exercise_sessions", titleId = R.string.exercise_sessions),
    ExerciseSessionDetail(
        route = "exercise_session_detail",
        titleId = R.string.exercise_session_detail
    ),
    SleepSessions(route = "sleep_sessions", titleId = R.string.sleep_sessions),
    SleepSessionDetail(route = "sleep_session_detail", titleId = R.string.sleep_session_detail),
    WeightScreen(route = "weight_screen", titleId = R.string.input_readings),
    DriveSessionScreen(route = "drive_screen", titleId = R.string.drive_screen),
    DifferentialChanges(route = "differential_changes", titleId = R.string.differential_changes),
    PrivacyPolicy(route = "privacy_policy", titleId = R.string.privacy_policy),
    HealthConnectScreen(route = "health_connect_screen", titleId = R.string.health_connect),
    RecordListScreen(route = "record_list", titleId = R.string.record_list),
    AboutScreen(route = "about_screen", titleId = R.string.about_screen),
    SettingScreen(route = "setting_screen", titleId = R.string.setting_screen),
    LoginScreen(route = "login_screen", titleId = R.string.login_screen),
    WalkSessionScreen(route = "walk_session_screen", titleId = R.string.walk_session_screen),
    RunSessionScreen(route = "run_session_screen", titleId = R.string.run_session_screen),
    SitSessionScreen(route = "sit_session_screen", titleId = R.string.sit_session_screen),
    ListenSessionScreen(route = "listen_session_screen", titleId = R.string.listen_session_screen),
    YogaSessionScreen(route = "yoga_session_screen", titleId = R.string.yoga_session_screen),
    CycleSessionScreen(route = "cycle_session_screen", titleId = R.string.cycle_session_screen),
    TrainSessionScreen(route = "train_session_screen", titleId = R.string.train_session_screen),
    NewActivityScreen(route = "new_activity_screen", titleId = R.string.new_activity_screen),
    MyScreenRecords(route = "my_screen_records", titleId = R.string.my_screen_records),
    FollowScreen(route = "follow_screen", titleId = R.string.follow_screen),
    ChartsScreen(route = "charts_screen", titleId = R.string.charts_screen),
    RegisterScreen(route = "register_screen", titleId = R.string.register_screen)

}
