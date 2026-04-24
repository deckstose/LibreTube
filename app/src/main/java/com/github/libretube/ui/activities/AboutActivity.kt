package com.github.libretube.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices.PHONE
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.github.libretube.BuildConfig
import com.github.libretube.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AboutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AboutScreen()
            }
        }
    }

    companion object {
        const val DONATE_URL = "https://github.com/libre-tube/LibreTube#donate"
        const val WEBSITE_URL = "https://libretube.dev"
        const val GITHUB_URL = "https://github.com/libre-tube/LibreTube"
        const val PIPED_GITHUB_URL = "https://github.com/TeamPiped/Piped"
        const val WEBLATE_URL = "https://hosted.weblate.org/projects/libretube/libretube/"
        const val LICENSE_URL = "https://gnu.org/"
    }
}

@Composable
private fun AboutScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val onBackPress: () -> Unit = {
        scope.launch {
            onBackPressedDispatcher?.onBackPressed()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { AboutScreenTopBar(onBackPress = onBackPress) },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            AppIconHeader()
            AboutCard(
                text = "Donate",
                icon = R.drawable.ic_donate,
                url = AboutActivity.DONATE_URL,
                scope = scope,
                snackbarHostState = snackBarHostState,
            )
            AboutCard(
                text = "Website",
                icon = R.drawable.ic_region,
                url = AboutActivity.WEBSITE_URL,
                scope = scope,
                snackbarHostState = snackBarHostState,
            )
            AboutCard(
                text = "Github",
                icon = R.drawable.ic_github,
                url = AboutActivity.GITHUB_URL,
                scope = scope,
                snackbarHostState = snackBarHostState,
            )
            AboutCard(
                text = "Piped",
                icon = R.drawable.ic_piped,
                url = AboutActivity.PIPED_GITHUB_URL,
                scope = scope,
                snackbarHostState = snackBarHostState,
            )
            AboutCard(
                text = "Translations",
                icon = R.drawable.ic_weblate,
                url = AboutActivity.WEBLATE_URL,
                scope = scope,
                snackbarHostState = snackBarHostState,
            )
            AboutCard(
                text = "License",
                icon = R.drawable.ic_license,
                url = AboutActivity.LICENSE_URL,
                scope = scope,
                snackbarHostState = snackBarHostState,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutScreenTopBar(modifier: Modifier = Modifier, onBackPress: () -> Unit = {}) {
    TopAppBar(title = { Text("About") }, modifier = modifier, navigationIcon = {
        IconButton(onClick = onBackPress) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null,
            )
        }
    })
}

@Composable
private fun AppIconHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(vertical = 30.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.5f),
            horizontalAlignment = Alignment.End,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_launcher_lockscreen),
                contentDescription = null,
                modifier = Modifier
                    .height(110.dp)
                    .fillMaxWidth(),
                tint = MaterialTheme.colorScheme.secondary,
            )
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Text(
                    text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    modifier = Modifier
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                        .align(Alignment.CenterHorizontally),
                )
            }
        }
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun AboutCard(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    text: String,
    url: String,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    val context = LocalContext.current
    val copied = stringResource(R.string.copied_to_clipboard)

    Card(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .height(64.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .combinedClickable(onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW).setData(url.toUri())
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                )
            }, onLongClick = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = copied,
                        withDismissAction = true,
                        duration = SnackbarDuration.Long,
                    )
                }
            }),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview(device = PHONE, showSystemUi = true)
@Composable
private fun AboutScreenPreview() {
    MaterialTheme {
        AboutScreen()
    }
}
