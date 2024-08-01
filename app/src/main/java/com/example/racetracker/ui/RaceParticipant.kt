/*
 * Copyright (C) 2023 The Android Open Source Project
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
package com.example.racetracker.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException

/**
 * This class represents a state holder for race participant.
 * 这个类代表比赛参与者所持有的状态
 * 这里定义的是一个类
 */
class RaceParticipant(
    val name: String,
    // 最大进度
    val maxProgress: Int = 100,
    // 进度延迟毫秒
    val progressDelayMillis: Long = 500L,
    // 进度
    private val progressIncrement: Int = 1,
    // 初始进度
    private val initialProgress: Int = 0
) {
    // 这个初始化函数做什么？
    init {
        // 如果最大进度小于0，则抛出异常
        require(maxProgress > 0) {
            "maxProgress=$maxProgress; must be > 0"
        }
        // 如果进度增量小于0，则抛出异常
        require(progressIncrement > 0) {
            "progressIncrement=$progressIncrement; must be > 0"
        }
    }

    /**
     * Indicates the race participant's current progress
     * 当前进度定义为可观察状态，当状态改变，Compose组件会自动更新
     */
    var currentProgress by mutableStateOf(initialProgress)
        //  使用 private说明改变进度是私有方法
        private set

    /**
     * Updates the value of [currentProgress] by value [progressIncrement] until it reaches
     * [maxProgress]. There is a delay of [progressDelayMillis] between each update.
     * 比赛进行
     */
    suspend fun run() {
        try {
            while (currentProgress < maxProgress) {
                // 模拟进度，延迟多少毫秒
                // 当调用delay时，就会挂起 suspend,当时间到时就会恢复resume
                // 这是kotlin提供的简单的并发模型，叫协程,
                // 它允许程序的执行在多个点暂停和恢复，而无需创建新的线程
                delay(progressDelayMillis)
                currentProgress += progressIncrement
                Log.d("RaceTrackerApp TAG", "run: ")
            }
        }
        catch (e:CancellationException){
            Log.e("RaceParticipant", "$name: ${e.message}")
            throw e // Always re-throw CancellationException.
        }
    }

    /**
     * Regardless of the value of [initialProgress] the reset function will reset the
     * [currentProgress] to 0
     */
    fun reset() {
        currentProgress = 0
    }
}

/**
 * The Linear progress indicator expects progress value in the range of 0-1. This property
 * calculate the progress factor to satisfy the indicator requirements.
 * 类扩展属性线性进度指示器
 */
val RaceParticipant.progressFactor: Float
    get() = currentProgress / maxProgress.toFloat()
