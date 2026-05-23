package com.zhihaofans.shortcutapp

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.zhihaofans.shortcutapp.ui.theme.ShortcutsAppTheme
import com.zhihaofans.shortcutapp.utils.ViewUtil
import io.zhihao.library.android.kotlinEx.appName
import io.zhihao.library.android.kotlinEx.appVersionCodeString
import io.zhihao.library.android.kotlinEx.appVersionName
import io.zhihao.library.android.kotlinEx.getAppIcon
import io.zhihao.library.android.util.AlertUtil
import io.zhihao.library.android.util.AppUtil
import io.zhihao.library.android.util.ShortcutsUtil
import io.zhihao.library.android.util.ToastUtil

class MainActivity : ComponentActivity() {
    private val pm by lazy { packageManager }
    private val alertUtil = AlertUtil(this)
    private val toastUtil = ToastUtil(this)
    private val viewUtil = ViewUtil()

    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var appPackageName by remember { mutableStateOf("") }
            var alertTitle by remember { mutableStateOf("Alert") }
            var alertMessage by remember { mutableStateOf("") }
            var showTextAlert by remember { mutableStateOf(false) }
            var showDeleteAppAlert by remember { mutableStateOf(false) }
            var showDeleteAllDialog by remember { mutableStateOf(false) }
            var showAddDialog by remember { mutableStateOf(false) }
            ShortcutsAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(
                        title = { Text("Shortcut App v${AppUtil.getAppVersionName()}") },
                        actions = {
                            var showMenu by remember {
                                mutableStateOf(false)
                            }
                            IconButton(
                                onClick = {
                                    showMenu = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Menu"
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = {
                                    showMenu = false
                                }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text("关于")
                                    },
                                    onClick = {
                                        showMenu = false
                                        alertTitle = "关于"

                                        alertMessage =
                                            "Shortcut App v${AppUtil.getAppVersionName()}\n" +
                                                    "Powered by zhihaofans's Android Library v" +
                                                    io.zhihao.library.android.BuildConfig.IO_ZHIHAO_LIB_VERSION
                                        showTextAlert = true
                                    }
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }, floatingActionButton = {
                    FloatingActionButton(onClick = {
                        if (ShortcutsUtil().getAllShortcuts().isEmpty()) {
//                            toastUtil.showShortToast("没有快捷方式可以删除了")
                            alertTitle = "提示"
                            alertMessage = "没有快捷方式可以删除了"
                            showTextAlert = true
                        } else {
//                            alertUtil.showInputAlert(
//                                title = "确定要删除所有快捷方式吗",
//                                inputText = ShortcutsUtil().getCount().toString() + "个快捷方式",
//                                onClick = { text, dialog ->
//                                    ShortcutManagerCompat.removeAllDynamicShortcuts(this)
//                                    toastUtil.showShortToast("已删除所有快捷方式")
//                                })
                            showDeleteAllDialog = true
                        }
                    }) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "Remove All Shortcuts")
                    }
                }) { innerPadding ->
                    val apps = remember {
                        pm.getInstalledApplications(PackageManager.GET_META_DATA)
                            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
                            .sortedBy {
                                pm.getApplicationLabel(it).toString()
                            }
                    }
                    if (showTextAlert) {
                        viewUtil.TextDialog(alertTitle, alertMessage) {
                            showTextAlert = false
                        }
//                        viewUtil.TwoButtonDialog(
//                            showDialog = showTextAlert,
//                            title = alertTitle,
//                            message = alertMessage,
//                            onConfirm = { TODO() }
//                        )
                    }
                    if (showDeleteAppAlert) {
                        val deleteApp = apps.find { it.packageName == appPackageName }
                        if (deleteApp != null) {
                            viewUtil.ImageTextDialog(
                                deleteApp.appName,
                                deleteApp.packageName,
                                deleteApp.getAppIcon()?.toBitmap(),
                                cancelText = "取消",
                                confirmText = "删除",
                                onCancel = {
                                    showDeleteAppAlert = false
                                },
                            ) {
                                ShortcutsUtil().removeShortcut(
                                    deleteApp.packageName
                                )
                                appPackageName = ""
                                showDeleteAppAlert = false
                                toastUtil.showShortToast("已删除快捷方式")
                            }
                        }
                    }
                    if (showDeleteAllDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showDeleteAllDialog = false
                            },
                            title = {
                                Text("确定要删除${ShortcutsUtil().getCount()}个快捷方式吗")
                            },
                            text = {
                                val shortcutText = ShortcutsUtil()
                                    .getAllShortcuts()
                                    .joinToString("\n\n") { shortcut ->
                                        val appName = try {
                                            pm.getApplicationLabel(
                                                pm.getApplicationInfo(shortcut.id, 0)
                                            ).toString()
                                        } catch (e: Exception) {
                                            shortcut.id
                                        }
                                        "$appName\n包名：${shortcut.id}"
                                    }
                                Text(shortcutText)
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        ShortcutManagerCompat.removeAllDynamicShortcuts(this@MainActivity)
                                        toastUtil.showShortToast("已删除所有快捷方式")
                                        showDeleteAllDialog = false
                                    }
                                ) {
                                    Text("确定")
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = {
                                        showDeleteAllDialog = false
                                    }
                                ) {
                                    Text("取消")
                                }
                            }
                        )
                    }
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                    ) {
                        items(
                            items = apps, key = { it.packageName }) { app ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val hasPushed =
                                            ShortcutManagerCompat.getDynamicShortcuts(this@MainActivity)
                                                .any { it.id == app.packageName }
                                        if (hasPushed) {
//                                            alertUtil.showInputAlert(
//                                                title = "确定要删除这个快捷方式吗",
//                                                inputText = app.appName,
//                                                onClick = { text, dialog ->
//                                                    ShortcutsUtil().removeShortcut(
//                                                        app.packageName
//                                                    )
//                                                    toastUtil.showShortToast("已删除快捷方式")
//                                                })
                                            appPackageName = app.packageName
                                            showDeleteAppAlert = true
                                        } else {
                                            alertUtil.showInputAlert(
                                                title = "确定要添加这个快捷方式吗",
                                                inputText = app.appName,
                                                onClick = { text, dialog ->
                                                    pushShortcut(app.packageName)
                                                })
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                val bitmap = remember(app.packageName) {
                                    pm.getApplicationIcon(app).toBitmap(
                                        width = 80, height = 80
                                    )
                                }
                                Image(
                                    bitmap = bitmap.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = app.appName,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = app.appVersionName + "(" + app.appVersionCodeString + ")",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = app.packageName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }

    private fun pushShortcut(packageName: String) {
        if (packageName.isEmpty()) {
            toastUtil.showShortToast("无法添加空白包名的快捷方式")
        } else if (ShortcutsUtil().hasFullShortcuts()) {
            alertUtil.showInputAlert(
                title = "快捷方式已满",
                inputText = "当前快捷方式已满，无法添加更多了",
                onClick = { text, dialog ->
                    dialog.dismiss()
                })
        } else {
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val launchIntent = pm.getLaunchIntentForPackage(packageName) ?: return
            ShortcutsUtil().pushShortcut(
                packageName,
                launchIntent,
                appInfo.appName,
                IconCompat.createWithBitmap(appInfo.getAppIcon()?.toBitmap()!!)
            )
            toastUtil.showShortToast("已添加快捷方式")
        }
    }
}
