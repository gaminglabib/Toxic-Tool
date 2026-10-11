package com.example.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ToolItem
import com.example.ui.theme.MonospaceCodeStyle
import com.example.ui.theme.ToxicAccentRed
import com.example.ui.theme.ToxicBackground
import com.example.ui.theme.ToxicBorder
import com.example.ui.theme.ToxicBorderLight
import com.example.ui.theme.ToxicCodeBg
import com.example.ui.theme.ToxicCyan
import com.example.ui.theme.ToxicEmerald
import com.example.ui.theme.ToxicEmeraldBright
import com.example.ui.theme.ToxicEmeraldContainer
import com.example.ui.theme.ToxicEmeraldDark
import com.example.ui.theme.ToxicRedDark
import com.example.ui.theme.ToxicSurface
import com.example.ui.theme.ToxicSurfaceHigh
import com.example.ui.theme.ToxicSurfaceVariant
import com.example.ui.theme.ToxicTextMuted
import com.example.ui.theme.ToxicTextPrimary
import com.example.ui.theme.ToxicTextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color = ToxicBorder,
    backgroundColor: Color = ToxicSurface,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val cardModifier = if (onClick != null) {
        modifier
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(backgroundColor, shape)
            .clickable(onClick = onClick)
    } else {
        modifier
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(backgroundColor, shape)
    }

    Box(modifier = cardModifier) {
        content()
    }
}

@Composable
fun ToolCard(
    tool: ToolItem,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CyberCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tool_card_${tool.id}"),
        onClick = onClick,
        borderColor = if (isFavorite) ToxicEmerald.copy(alpha = 0.4f) else ToxicBorder
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ToxicEmeraldDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tool.name.take(2).uppercase(),
                            color = ToxicEmeraldBright,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = tool.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = ToxicTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = tool.category.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = ToxicEmerald
                        )
                    }
                }

                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier
                        .size(40.dp)
                        .testTag("favorite_btn_${tool.id}")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) ToxicAccentRed else ToxicTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tool.shortDescription,
                style = MaterialTheme.typography.bodyMedium,
                color = ToxicTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (tool.isNetworkRequired) {
                    CyberBadge(
                        text = "NETWORK",
                        color = ToxicCyan,
                        backgroundColor = ToxicCyan.copy(alpha = 0.12f)
                    )
                } else {
                    CyberBadge(
                        text = "OFFLINE",
                        color = ToxicEmerald,
                        backgroundColor = ToxicEmeraldDark
                    )
                }
                if (tool.isSensitiveSecret) {
                    CyberBadge(
                        text = "NO-LOG SECRET",
                        color = ToxicAccentRed,
                        backgroundColor = ToxicRedDark
                    )
                }
            }
        }
    }
}

@Composable
fun CyberBadge(
    text: String,
    color: Color = ToxicEmerald,
    backgroundColor: Color = ToxicEmeraldDark,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 0.5.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun CyberButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    isDestructive: Boolean = false,
    isOutlined: Boolean = false,
    enabled: Boolean = true,
    testTag: String = "cyber_button"
) {
    val buttonModifier = modifier
        .defaultMinSize(minHeight = 48.dp)
        .testTag(testTag)

    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            modifier = buttonModifier,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isDestructive) ToxicAccentRed else ToxicEmerald,
                disabledContentColor = ToxicTextMuted
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = androidx.compose.ui.graphics.SolidColor(
                    if (isDestructive) ToxicAccentRed.copy(alpha = 0.6f) else ToxicBorderLight
                )
            )
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        }
    } else {
        Button(
            onClick = onClick,
            enabled = enabled,
            modifier = buttonModifier,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDestructive) ToxicAccentRed else ToxicEmerald,
                contentColor = if (isDestructive) Color.White else ToxicBackground,
                disabledContainerColor = ToxicSurfaceHigh,
                disabledContentColor = ToxicTextMuted
            )
        ) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(text = text, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ToxicEmerald)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = ToxicTextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = ToxicTextSecondary,
                modifier = Modifier.padding(start = 11.dp)
            )
        }
    }
}

@Composable
fun ResultCard(
    content: String,
    label: String = "OUTPUT",
    modifier: Modifier = Modifier,
    infoBadge: String? = null,
    testTag: String = "result_output"
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    CyberCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = ToxicBorder,
        backgroundColor = ToxicCodeBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = ToxicEmerald,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    if (infoBadge != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        CyberBadge(text = infoBadge, color = ToxicTextSecondary, backgroundColor = ToxicSurface)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(content))
                            copied = true
                            scope.launch {
                                delay(2000)
                                copied = false
                            }
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("copy_output_btn")
                    ) {
                        Icon(
                            imageVector = if (copied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                            contentDescription = "Copy output",
                            tint = if (copied) ToxicEmerald else ToxicTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (content.isEmpty()) "(No output generated yet)" else content,
                style = MonospaceCodeStyle,
                color = if (content.isEmpty()) ToxicTextMuted else ToxicTextPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(testTag)
            )
        }
    }
}

@Composable
fun WarningNoticeCard(
    message: String,
    modifier: Modifier = Modifier,
    title: String = "SECURITY ADVISORY"
) {
    CyberCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = ToxicAccentRed.copy(alpha = 0.5f),
        backgroundColor = ToxicRedDark.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = ToxicAccentRed,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = ToxicAccentRed,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = ToxicTextPrimary
                )
            }
        }
    }
}

@Composable
fun PrivacyNoticeBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    CyberCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = ToxicEmeraldDark,
        backgroundColor = ToxicEmeraldContainer.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = ToxicEmeraldBright,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = ToxicTextSecondary
            )
        }
    }
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(ToxicSurfaceVariant)
                .border(1.dp, ToxicBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ToxicEmerald,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = ToxicTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = ToxicTextMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        if (actionButtonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            CyberButton(
                text = actionButtonText,
                onClick = onActionClick,
                isOutlined = true
            )
        }
    }
}
