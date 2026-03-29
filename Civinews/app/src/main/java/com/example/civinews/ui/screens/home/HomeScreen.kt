package com.example.civinews.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.civinews.R
import com.example.civinews.ui.base.screens.ErrorScreen
import com.example.civinews.ui.base.screens.LoadingScreen
import com.example.civinews.ui.base.screens.NoDataScreen
import com.example.civinews.ui.theme.newsreaderFontFamily

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onThemeChange: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val events = HomeEvents(
        onCategorySelected = viewModel::selectCategory,
        onNewReportClick = { /* TODO: Navegación */ }
    )

    when (state) {
        is HomeListState.Loading -> LoadingScreen()
        is HomeListState.NoData -> NoDataScreen()
        is HomeListState.Error -> ErrorScreen(
            message = state.message,
            onRetry = { viewModel.getData() }
        )
        is HomeListState.Success -> {
            HomeContent(
                modifier = modifier,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                state = state,
                events = events
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    onThemeChange: () -> Unit,
    state: HomeListState.Success,
    events: HomeEvents
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontStyle = FontStyle.Italic,
                        fontFamily = newsreaderFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 24.sp
                    )
                },
                actions = {
                    IconButton(onClick = onThemeChange) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = events.onNewReportClick,
                containerColor = MaterialTheme.colorScheme.primary,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text(stringResource(id = R.string.home_fab_new_report), fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.home_subtitle_community),
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(id = R.string.home_title_reports),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // 1. Guardamos el ID del String (Int), no el texto resuelto
                    val categories = listOf(
                        R.string.category_all to Icons.Default.GridView,
                        R.string.category_security to Icons.Default.Security,
                        R.string.category_traffic to Icons.Default.Traffic,
                        R.string.category_events to Icons.Default.Event
                    )

                    items(categories) { (nameResId, icon) ->
                        // 2. Resolvemos el texto aquí dentro, que sí es un contexto Composable
                        val name = stringResource(id = nameResId)

                        FilterChip(
                            selected = state.selectedCategory == name,
                            onClick = { events.onCategorySelected(name) },
                            label = { Text(name) },
                            leadingIcon = { Icon(icon, null, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }
            }

            if (state.dataset.isEmpty()) {
                item {
                    NoDataScreen(
                        modifier = Modifier.fillParentMaxSize(),
                        message = stringResource(id = R.string.home_no_data_category, state.selectedCategory)
                    )
                }
            } else {
                items(state.dataset) { report ->
                    ReportCard(report)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun ReportCard(report: ReportUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column {
            AsyncImage(
                model = report.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                error = painterResource(id = R.drawable.ic_launcher_foreground)
            )

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            text = report.category.uppercase(),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.1f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MaterialTheme.colorScheme.tertiary))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = report.status.uppercase(), color = MaterialTheme.colorScheme.tertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = report.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(report.time, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Text(stringResource(id = R.string.bullet_separator), color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))

                    Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(report.location ?: stringResource(id = R.string.report_location_unknown), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}