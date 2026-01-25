package com.overklassniy.stankinschedule.settings.ui

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.overklassniy.stankinschedule.core.domain.settings.DarkMode
import com.overklassniy.stankinschedule.core.ui.utils.BrowserUtils
import com.overklassniy.stankinschedule.settings.ui.components.DialogPreference
import com.overklassniy.stankinschedule.settings.ui.components.PreferenceDivider
import com.overklassniy.stankinschedule.settings.ui.components.PreferenceSpacer
import com.overklassniy.stankinschedule.settings.ui.components.RegularPreference
import com.overklassniy.stankinschedule.settings.ui.components.SettingsScaffold

@Composable
fun RootSettingsScreen(
    viewModel: SettingsViewModel,
    onBackPressed: () -> Unit,
    navigateToSchedule: () -> Unit,
    navigateToMore: () -> Unit,
    modifier: Modifier = Modifier
) {

    SettingsScaffold(
        title = stringResource(R.string.settings_title),
        onBackPressed = onBackPressed,
        modifier = modifier
    ) {
        val context = LocalContext.current

        val nightMode by viewModel.nightMode.collectAsState()

        LaunchedEffect(nightMode) {
            val mode = when (nightMode) {
                DarkMode.Default -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                DarkMode.Dark -> AppCompatDelegate.MODE_NIGHT_YES
                DarkMode.Light -> AppCompatDelegate.MODE_NIGHT_NO
            }

            if (mode != AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }

        DialogPreference(
            title = stringResource(R.string.pref_dark_mode),
            items = DarkMode.values().asList(),
            selected = nightMode,
            label = {
                @StringRes val id = when (it) {
                    DarkMode.Default -> R.string.dark_mode_default
                    DarkMode.Dark -> R.string.dark_mode_dark
                    DarkMode.Light -> R.string.dark_mode_light
                }
                stringResource(id)
            },
            onItemChanged = { viewModel.setNightMode(it) },
            icon = R.drawable.ic_pref_dark_mode
        )

        PreferenceDivider()

        RegularPreference(
            title = stringResource(R.string.pref_schedule),
            subtitle = stringResource(R.string.pref_schedule_summary),
            onClick = navigateToSchedule,
            icon = R.drawable.ic_pref_schedule
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            RegularPreference(
                title = stringResource(R.string.pref_notification),
                subtitle = stringResource(R.string.pref_notification_summary),
                onClick = {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    context.startActivity(intent)
                },
                icon = R.drawable.ic_pref_notifications
            )
        }

        RegularPreference(
            title = stringResource(R.string.pref_more),
            subtitle = stringResource(R.string.pref_more_summary),
            onClick = navigateToMore,
            icon = R.drawable.ic_pref_more
        )

        PreferenceDivider()

        val currentLanguage = remember {
            val config = context.resources.configuration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.locales.get(0)?.language ?: "en"
            } else {
                @Suppress("DEPRECATION")
                config.locale?.language ?: "en"
            }
        }
        val isRussian = currentLanguage == "ru"

        RegularPreference(
            title = stringResource(R.string.terms_and_conditions),
            subtitle = stringResource(R.string.terms_and_conditions_summary),
            onClick = {
                val url = if (isRussian) {
                    "https://raw.githubusercontent.com/overklassniy/STANKIN_Schedule_app/master/Terms%20%26%20Conditions.md"
                } else {
                    "https://raw.githubusercontent.com/overklassniy/STANKIN_Schedule_app/master/Terms%20%26%20Conditions_en.md"
                }
                BrowserUtils.openLink(context = context, url = url)
            },
            icon = R.drawable.ic_terms
        )

        RegularPreference(
            title = stringResource(R.string.privacy_policy),
            subtitle = stringResource(R.string.privacy_policy_summary),
            onClick = {
                val url = if (isRussian) {
                    "https://raw.githubusercontent.com/overklassniy/STANKIN_Schedule_app/master/Privacy%20Policy.md"
                } else {
                    "https://raw.githubusercontent.com/overklassniy/STANKIN_Schedule_app/master/Privacy%20Policy_en.md"
                }
                BrowserUtils.openLink(context = context, url = url)
            },
            icon = R.drawable.ic_privacy_policy
        )

        PreferenceSpacer()

        Image(
            painter = painterResource(R.drawable.logo_about),
            contentDescription = null,
            modifier = Modifier
                .sizeIn(maxHeight = 200.dp, minWidth = 200.dp)
                .clickable {
                    BrowserUtils.openLink(
                        context = context,
                        url = "https://github.com/overklassniy/STANKIN_Schedule_app.git"
                    )
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.version) + " " + BuildConfig.APP_VERSION,
                style = MaterialTheme.typography.titleMedium
            )
            
            val changelogText = stringResource(R.string.changelog)
            val changelogUrl = if (isRussian) {
                "https://raw.githubusercontent.com/overklassniy/STANKIN_Schedule_app/master/changelog.md"
            } else {
                "https://raw.githubusercontent.com/overklassniy/STANKIN_Schedule_app/master/changelog_en.md"
            }
            
            val primaryColor = MaterialTheme.colorScheme.primary
            val changelogAnnotatedText = buildAnnotatedString {
                withAnnotation(tag = "URL", annotation = changelogUrl) {
                    withStyle(style = SpanStyle(color = primaryColor)) {
                        append(changelogText)
                    }
                }
            }
            
            var changelogLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
            
            Text(
                text = changelogAnnotatedText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = primaryColor
                ),
                modifier = Modifier
                    .pointerInput(changelogAnnotatedText) {
                        detectTapGestures { pos ->
                            changelogLayoutResult?.let { layout ->
                                val offset = layout.getOffsetForPosition(pos)
                                changelogAnnotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                    .firstOrNull()?.let { annotation ->
                                        BrowserUtils.openLink(context, annotation.item)
                                    }
                            }
                        }
                    },
                onTextLayout = { changelogLayoutResult = it }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            val primaryColor = MaterialTheme.colorScheme.primary
            val textColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)

            val legacyText = stringResource(R.string.about_developer_legacy)
            val legacyName = stringResource(R.string.about_developer_legacy_name)

            val legacyAnnotatedText = buildAnnotatedString {
                append(legacyText)
                append(" ")

                withAnnotation(tag = "URL", annotation = "https://github.com/overklassniy/STANKIN_Schedule_app.git") {
                    withStyle(style = SpanStyle(color = primaryColor)) {
                        append(legacyName)
                    }
                }
            }

            var legacyLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
            
            Text(
                text = legacyAnnotatedText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = textColor,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(legacyAnnotatedText) {
                        detectTapGestures { pos ->
                            legacyLayoutResult?.let { layout ->
                                val offset = layout.getOffsetForPosition(pos)
                                legacyAnnotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                    .firstOrNull()?.let { annotation ->
                                        BrowserUtils.openLink(context, annotation.item)
                                    }
                            }
                        }
                    },
                onTextLayout = { legacyLayoutResult = it }
            )

            val forkText = stringResource(R.string.about_developer_fork)
            val forkName = stringResource(R.string.about_developer_fork_name)

            val forkAnnotatedText = buildAnnotatedString {
                append(forkText)
                append(" ")

                withAnnotation(tag = "URL", annotation = "https://github.com/overklassniy/STANKIN_Schedule_app.git") {
                    withStyle(style = SpanStyle(color = primaryColor)) {
                        append(forkName)
                    }
                }
            }

            var forkLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
            
            Text(
                text = forkAnnotatedText,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = textColor,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(forkAnnotatedText) {
                        detectTapGestures { pos ->
                            forkLayoutResult?.let { layout ->
                                val offset = layout.getOffsetForPosition(pos)
                                forkAnnotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                    .firstOrNull()?.let { annotation ->
                                        BrowserUtils.openLink(context, annotation.item)
                                    }
                            }
                        }
                    },
                onTextLayout = { forkLayoutResult = it }
            )
        }

        PreferenceDivider()

        val supportEmail = stringResource(R.string.support_email)
        RegularPreference(
            title = stringResource(R.string.support_email_title),
            subtitle = stringResource(R.string.support_email_summary),
            onClick = {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$supportEmail")
                }
                if (emailIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(emailIntent)
                }
            },
            icon = R.drawable.ic_pref_more
        )

        RegularPreference(
            title = stringResource(R.string.support_github_title),
            subtitle = stringResource(R.string.support_github_summary),
            onClick = {
                BrowserUtils.openLink(
                    context = context,
                    url = "https://github.com/overklassniy/STANKIN_Schedule_app/issues"
                )
            },
            icon = R.drawable.ic_pref_more
        )

        PreferenceSpacer()
    }
}
