package com.zhihaofans.shortcutapp.utils

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import io.zhihao.library.android.kotlinEx.isNotNullAndEmpty

class ViewUtil {
    @Composable
    fun TextDialog(
        title: String,
        message: String,
        confirmText: String = "确定",
        onConfirm: () -> Unit
    ) {
        AlertDialog(
            // 点击外部区域关闭
            onDismissRequest = {
                // 执行取消回调（如果有）
                onConfirm()
            },
            // 标题区域
            title = {
                Text(text = title)
            },
            // 内容区域
            text = {
                Text(text = message)
            },
            // 确认按钮
            confirmButton = {
                TextButton(
                    onClick = {
                        // 执行确认逻辑
                        onConfirm()
                    }
                ) {
                    Text(confirmText)
                }
            }
        )
    }

    @Composable
    fun TwoButtonTextDialog(
        showDialog: Boolean,
        title: String,
        message: String,
        confirmText: String = "确定",
        cancelText: String = "取消",
        onCancel: (() -> Unit)? = null,
        onConfirm: () -> Unit,
    ) {
        // 如果状态为 false
        // 直接不显示 Dialog
        if (!showDialog) return

        AlertDialog(
            // 点击外部区域关闭
            onDismissRequest = {
                // 执行取消回调（如果有）
                onCancel?.invoke()
            },
            // 标题区域
            title = {
                Text(text = title)
            },
            // 内容区域
            text = {
                Text(text = message)
            },
            // 确认按钮
            confirmButton = {
                TextButton(
                    onClick = {
                        // 执行确认逻辑
                        onConfirm()
                    }
                ) {
                    Text(confirmText)
                }
            },
            // 取消按钮
            dismissButton = {
                TextButton(
                    onClick = {
                        // 执行取消逻辑（如果有）
                        onCancel?.invoke()
                    }
                ) {
                    Text(cancelText)
                }
            }
        )
    }

    @Composable
    fun ImageTextDialog(
        title: String,
        message: String,
        image: Bitmap?,
        cancelText: String? = null,
        onCancel: (() -> Unit)? = null,
        confirmText: String = "确定",
        onConfirm: () -> Unit
    ) {
        AlertDialog(
            // 点击外部区域关闭
            onDismissRequest = {
                // 执行取消回调（如果有）
                onConfirm()
            },
            // 标题区域
            title = {
                Text(text = title)
            },
            // 内容区域
            text = {
                Column {
                    // 文本
                    Text(text = message)
                    // 图片
                    if (image != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Image(
                            bitmap = image.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            },
            // 确认按钮
            confirmButton = {
                TextButton(
                    onClick = {
                        // 执行确认逻辑
                        onConfirm()
                    }
                ) {
                    Text(confirmText)
                }
            },
            // 取消按钮
            dismissButton = {
                if (cancelText.isNotNullAndEmpty()) {
                    TextButton(
                        onClick = {
                            // 执行取消逻辑（如果有）
                            onCancel?.invoke()
                        }
                    ) {
                        Text(cancelText!!)
                    }
                }
            }
        )
    }
}