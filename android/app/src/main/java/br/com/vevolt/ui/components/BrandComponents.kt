package br.com.vevolt.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.vevolt.R
import br.com.vevolt.ui.theme.ElectricBlue
import br.com.vevolt.ui.theme.EnergyGreen

@Composable
fun BrandLogo(modifier: Modifier = Modifier, compact: Boolean = false) {
    if (compact) {
        Image(
            painter = painterResource(R.drawable.vevolt_logo_mark),
            contentDescription = stringResource(R.string.app_name),
            modifier = modifier.size(58.dp),
            contentScale = ContentScale.Fit
        )
    } else {
        Row(
            modifier = modifier
                .width(286.dp)
                .height(104.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.vevolt_logo_mark),
                contentDescription = null,
                modifier = Modifier.size(92.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Ve", color = ElectricBlue, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Volt", color = EnergyGreen, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold)
                }
                Text(
                    stringResource(R.string.brand_tagline),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
