/*
 * Copyright (c) 2024 gohj99. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.gohj99.telewatch.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

@Composable
fun LinkText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFEFEFE), // 默认白色
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge, // 默认使用主题的bodyLarge样式
    onLinkClick: ((String) -> Unit)? = null
) {
    val annotatedString = buildAnnotatedString {
        val regex = Regex("(https?://[\\w-]+(\\.[\\w-]+)*(:[0-9]+)?(/[\\w-?=&%.]*)?)")
        var lastIndex = 0

        regex.findAll(text).forEach { result ->
            val start = result.range.first
            val end = result.range.last + 1

            // Append text before the link
            append(text.substring(lastIndex, start))

            // Append the link with a different style
            pushStringAnnotation(tag = "URL", annotation = result.value)
            withStyle(style = SpanStyle(color = Color(0xFF66D3FE), textDecoration = TextDecoration.Underline)) {
                append(result.value)
            }
            pop()

            lastIndex = end
        }

        // Append remaining text
        append(text.substring(lastIndex))
    }

    ClickableText(
        text = annotatedString,
        style = style.copy(color = color), // 应用传入的颜色和样式
        modifier = modifier,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    // Handle click on the URL
                    onLinkClick?.invoke(annotation.item)
                }
        }
    )
}

@Composable
fun <T> MainCard(
    column: @Composable () -> Unit,
    item: T,
    callback: (T) -> Unit = {},
    color: Color = Color(0xFF404953)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { callback(item) },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = color // 设置 Card 的背景颜色
        )
    ) {
        Column(modifier = Modifier.padding(start = 12.dp, top = 9.dp, end = 14.dp, bottom = 9.dp)) {
            column()
        }
    }
}
