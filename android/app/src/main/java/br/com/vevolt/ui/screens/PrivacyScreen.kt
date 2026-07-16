package br.com.vevolt.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import br.com.vevolt.R
import br.com.vevolt.ui.components.AppCard
import br.com.vevolt.ui.components.OutlineActionButton
import br.com.vevolt.ui.components.PrimaryButton

@Composable
fun PrivacyScreen(
    onBack: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenTerms: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
                }
                Text(stringResource(R.string.privacy_terms), fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        item {
            AppCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PrivacySection(
                        stringResource(R.string.privacy_summary_title),
                        stringResource(R.string.privacy_summary_body)
                    )
                    PrivacySection(
                        stringResource(R.string.privacy_device_title),
                        stringResource(R.string.privacy_device_body)
                    )
                    PrivacySection(
                        stringResource(R.string.privacy_collection_title),
                        stringResource(R.string.privacy_collection_body)
                    )
                    PrivacySection(
                        stringResource(R.string.privacy_payments_title),
                        stringResource(R.string.privacy_payments_body)
                    )
                    PrivacySection(
                        stringResource(R.string.privacy_contact_title),
                        stringResource(R.string.privacy_contact_body)
                    )
                    OutlineActionButton(
                        stringResource(R.string.open_privacy_policy),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onOpenPrivacyPolicy
                    )
                    OutlineActionButton(
                        stringResource(R.string.open_terms),
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onOpenTerms
                    )
                }
            }
        }
        item {
            PrimaryButton(stringResource(R.string.back), modifier = Modifier.fillMaxWidth(), onClick = onBack)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PrivacySection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
        Text(body, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .72f))
    }
}
