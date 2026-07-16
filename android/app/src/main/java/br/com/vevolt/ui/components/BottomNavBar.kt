package br.com.vevolt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.CenterFocusStrong
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Route
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import br.com.vevolt.R
import br.com.vevolt.ui.navigation.Screen
import br.com.vevolt.ui.theme.ElectricBlue
import br.com.vevolt.ui.theme.NavigationGray

@Composable
fun BottomNavBar(current: Screen, onNavigate: (Screen) -> Unit) {
    Surface(shadowElevation = 12.dp, color = MaterialTheme.colorScheme.surface) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(stringResource(R.string.nav_map), Icons.Rounded.LocationOn, current == Screen.MAP) { onNavigate(Screen.MAP) }
            NavItem(stringResource(R.string.nav_route), Icons.Rounded.Route, current == Screen.ROUTE) { onNavigate(Screen.ROUTE) }
            CenterScanButton(current == Screen.SCAN) { onNavigate(Screen.SCAN) }
            NavItem(stringResource(R.string.nav_savings), Icons.Rounded.AttachMoney, current == Screen.ECONOMY) { onNavigate(Screen.ECONOMY) }
            NavItem(stringResource(R.string.nav_profile), Icons.Rounded.AccountCircle, current == Screen.PROFILE) { onNavigate(Screen.PROFILE) }
        }
    }
}

@Composable
private fun NavItem(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(6.dp)
    ) {
        Icon(icon, contentDescription = label, tint = if (selected) ElectricBlue else NavigationGray, modifier = Modifier.size(22.dp))
        Text(label, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, color = if (selected) ElectricBlue else NavigationGray)
    }
}

@Composable
private fun CenterScanButton(selected: Boolean, onClick: () -> Unit) {
    val label = stringResource(R.string.nav_scan)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(ElectricBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.CenterFocusStrong, contentDescription = label, tint = Color.White, modifier = Modifier.size(28.dp))
        }
        Text(label, fontSize = 11.sp, color = if (selected) ElectricBlue else NavigationGray, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}
