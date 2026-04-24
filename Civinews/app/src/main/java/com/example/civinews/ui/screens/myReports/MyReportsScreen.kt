package com.example.civinews.ui.screens.myReports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.civinews.ui.base.screens.ErrorScreen
import com.example.civinews.ui.base.screens.LoadingScreen
import com.example.civinews.ui.base.screens.NoDataScreen
import com.example.civinews.ui.screens.home.ReportCard // Tu tarjeta maestra
import com.example.civinews.ui.theme.newsreaderFontFamily


@Composable
fun MyReportsScreen(
    modifier: Modifier = Modifier,
    onReportClick: (String) -> Unit, // Delegamos para la navegación a Detalles
    viewModel: MyReportsViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val events = MyReportsEvents(
        onRefresh = viewModel::loadMyReports,
        onReportClick = onReportClick
    )

    LaunchedEffect(Unit) {
        viewModel.loadMyReports()
    }

    when (state) {
        is MyReportsListState.Loading -> LoadingScreen()
        is MyReportsListState.NoData -> NoDataScreen(
            modifier = modifier,
            message = "Aún no has creado ningún aviso ciudadano."
        )
        is MyReportsListState.Error -> ErrorScreen(
            modifier = modifier,
            message = state.message,
            onRetry = events.onRefresh
        )
        is MyReportsListState.Success -> {
            MyReportsContent(
                modifier = modifier,
                state = state,
                events = events
            )
        }
    }
}

@Composable
fun MyReportsContent(
    modifier: Modifier = Modifier,
    state: MyReportsListState.Success,
    events: MyReportsEvents
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "ÁREA PERSONAL",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Tus Reportes",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 36.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Haz seguimiento en tiempo real del estado de los avisos que has enviado al ayuntamiento.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
        }

        items(
            items = state.dataset,
            key = { report -> report.id }
        ) { report ->
            ReportCard(
                report = report,
                onClick = { events.onReportClick(report.id) }
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}