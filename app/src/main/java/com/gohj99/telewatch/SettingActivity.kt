/*
 * Copyright (c) 2024 gohj99. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.gohj99.telewatch

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import com.gohj99.telewatch.ui.setting.SplashSettingScreen
import com.gohj99.telewatch.ui.theme.TelewatchTheme
import java.io.File

sealed class SettingItem(val name: String) {
    data class Click(val itemName: String, val onClick: () -> Unit) : SettingItem(itemName)
    data class Switch(
        val itemName: String,
        var isSelected: Boolean,
        val onSelect: (Boolean) -> Unit
    ) : SettingItem(itemName)

    data class ProgressBar(
        val itemName: String,
        var progress: Float,
        val maxValue: Float,
        val minValue: Float,
        val base: Float,
        val onProgressChange: (Float) -> Unit
    ) : SettingItem(itemName)
}

class SettingActivity : ComponentActivity() {
    private var settingsList = mutableStateOf(listOf<SettingItem>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val externalDir: File = getExternalFilesDir(null)
            ?: throw IllegalStateException("Failed to get external directory.")

        // 获取传入页面数
        val page: Int = intent.getIntExtra("page", 0)

        // 初始标题
        var title = getString(R.string.Settings)

        when (page) {
            0 -> {
                settingsList.value = listOf(
                    // 界面调节
                    SettingItem.Click(
                        itemName = getString(R.string.UI_Edit),
                        onClick = {
                            startActivity(
                                Intent(
                                    this,
                                    SettingActivity::class.java
                                ).putExtra("page", 1)
                            )
                        }
                    ),
                    // 应用设置
                    SettingItem.Click(
                        itemName = getString(R.string.App_setting),
                        onClick = {
                            startActivity(
                                Intent(
                                    this,
                                    SettingActivity::class.java
                                ).putExtra("page", 2)
                            )
                        }
                    ),
                    // 关于
                    SettingItem.Click(
                        itemName = getString(R.string.About),
                        onClick = {
                            startActivity(
                                Intent(
                                    this,
                                    AboutActivity::class.java
                                )
                            )
                        }
                    ),
                )
            }

            1 -> {
                title = getString(R.string.UI_Edit)
            }

            2 -> {
                title = getString(R.string.App_setting)
                settingsList.value = listOf(
                    SettingItem.Click(
                        itemName = getString(R.string.Clearing_cache),
                        onClick = {
                            cacheDir.deleteRecursively()
                            Toast.makeText(
                                this,
                                getString(R.string.Successful),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ),
                    SettingItem.Click(
                        itemName = getString(R.string.Restart),
                        onClick = {
                            // 重启软件
                            Handler(Looper.getMainLooper()).postDelayed({
                                val intent = packageManager.getLaunchIntentForPackage(packageName)
                                intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                android.os.Process.killProcess(android.os.Process.myPid())
                            }, 1000)
                        }
                    ),
                    SettingItem.Click(
                        itemName = getString(R.string.Clear_thumbnails),
                        onClick = {
                            val dir = File(externalDir.absolutePath + "/tdlib")
                            dir.listFiles()?.find { it.name == "thumbnails" && it.isDirectory }
                                ?.deleteRecursively()
                            cacheDir.deleteRecursively()
                            Toast.makeText(
                                this,
                                getString(R.string.Successful),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ),
                    SettingItem.Click(
                        itemName = getString(R.string.Clear_photos),
                        onClick = {
                            val dir = File(externalDir.absolutePath + "/tdlib")
                            dir.listFiles()?.find { it.name == "photos" && it.isDirectory }
                                ?.deleteRecursively()
                            cacheDir.deleteRecursively()
                            Toast.makeText(
                                this,
                                getString(R.string.Successful),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ),
                    SettingItem.Click(
                        itemName = getString(R.string.Clear_videos),
                        onClick = {
                            val dir = File(externalDir.absolutePath + "/tdlib")
                            dir.listFiles()?.find { it.name == "videos" && it.isDirectory }
                                ?.deleteRecursively()
                            cacheDir.deleteRecursively()
                            Toast.makeText(
                                this,
                                getString(R.string.Successful),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ),
                    SettingItem.Click(
                        itemName = getString(R.string.Clear_cache),
                        onClick = {
                            val dir = File(externalDir.absolutePath + "/tdlib")
                            dir.listFiles()?.find { it.name == "temp" && it.isDirectory }
                                ?.deleteRecursively()
                            cacheDir.deleteRecursively()
                            Toast.makeText(
                                this,
                                getString(R.string.Successful),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ),
                    SettingItem.Click(
                        itemName = getString(R.string.Reset_self),
                        onClick = { resetSelf() }
                    ),
                    SettingItem.Click(
                        itemName = getString(R.string.data_Collection),
                        onClick = {
                            startActivity(
                                Intent(
                                    this,
                                    AllowDataCollectionActivity::class.java
                                )
                            )
                        }
                    )
                )

            }
        }

        setContent {
            TelewatchTheme {
                SplashSettingScreen(
                    title = title,
                    settings = settingsList
                )
            }
        }
    }

    private fun resetSelf() {
        // 清除缓存
        cacheDir.deleteRecursively()
        // 清空软件文件
        filesDir.deleteRecursively()
        // 清空 SharedPreferences
        getSharedPreferences("LoginPref", Context.MODE_PRIVATE).edit().clear().apply()
        getSharedPreferences("app_settings", Context.MODE_PRIVATE).edit().clear().apply()
        // Toast提醒
        Toast.makeText(this, getString(R.string.Successful), Toast.LENGTH_SHORT).show()
        // 重启软件
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            android.os.Process.killProcess(android.os.Process.myPid())
        }, 1000)
    }
}
