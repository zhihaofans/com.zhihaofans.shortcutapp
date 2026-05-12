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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.zhihaofans.shortcutapp.ui.theme.ShortcutsAppTheme
import io.zhihao.library.android.kotlinEx.appName
import io.zhihao.library.android.kotlinEx.appVersionCodeString
import io.zhihao.library.android.kotlinEx.appVersionName
import io.zhihao.library.android.kotlinEx.getAppIcon
import io.zhihao.library.android.kotlinEx.isNotNullAndEmpty
import io.zhihao.library.android.util.AlertUtil
import io.zhihao.library.android.util.AppUtil
import io.zhihao.library.android.util.ShortcutsUtil

class MainActivity : ComponentActivity() {
    private val pm by lazy { packageManager }

    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val alertUtil = AlertUtil(this@MainActivity)
            ShortcutsAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
                    TopAppBar(
                        title = { Text("Shortcut App v${AppUtil.getAppVersionName()}") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }, floatingActionButton = {
                    FloatingActionButton(onClick = { /* TODO: add action */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }) { innerPadding ->
                    val apps = remember {
                        pm.getInstalledApplications(PackageManager.GET_META_DATA)
                            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
                            .sortedBy {
                                pm.getApplicationLabel(it).toString()
                            }
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
                                            //Remove the shortcut
//                                            ShortcutManagerCompat.removeDynamicShortcuts(
//                                                this@MainActivity, listOf(app.packageName)
//                                            )
                                            alertUtil.showInputAlert(
                                                title = "删除快捷方式",
                                                inputText = "确定要删除这个吗：" + app.appName,
                                                onClick = { text, dialog ->
                                                    ShortcutsUtil().removeShortcut(
                                                        app.packageName
                                                    )
                                                })

                                        } else {
                                            alertUtil.showInputAlert(
                                                title = "添加快捷方式",
                                                inputText = "确定要添加这个吗：" + app.appName,
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
                        }
                    }
                }
            }
        }
    }

    private fun pushShortcut(packageName: String) {
        if (packageName.isNotNullAndEmpty()) {
            val appInfo = pm.getApplicationInfo(packageName, 0)
            val launchIntent = pm.getLaunchIntentForPackage(packageName) ?: return
            ShortcutsUtil().pushShortcut(
                packageName,
                launchIntent,
                appInfo.appName,
                IconCompat.createWithBitmap(appInfo.getAppIcon()?.toBitmap()!!)
            )

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ShortcutsAppTheme {
        Greeting("Android")
    }
}