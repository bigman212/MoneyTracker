package com.moneytracker.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.moneytracker.R
import com.moneytracker.ui.theme.MoneyTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    state: MainScreenUi,
    modifier: Modifier = Modifier,
    onAddExpenseClick: () -> Unit = {}
) {
    MainScreenContent(
        state = state,
        modifier = modifier,
        onAddExpenseClick = onAddExpenseClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreenContent(
    state: MainScreenUi,
    modifier: Modifier = Modifier,
    onAddExpenseClick: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(R.string.main_toolbar_title))
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExpenseClick,
                content = {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = state.balance,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(72.dp))
            Text(
                text = stringResource(R.string.main_today_expenses),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(32.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                items(
                    items = state.expenses,
                    key = ExpenseRowUi::id
                ) { expense ->
                    ExpenseRow(expense = expense)
                }
            }
        }
    }
}

fun previewMainScreenUi(): MainScreenUi =
    MainScreenUi(
        balance = "12 450.00 руб",
        expenses = List(20) { index ->
            ExpenseRowUi(
                id = index.toString(),
                title = if (index == 0) {
                    "Очень длинное название расхода"
                } else {
                    "Название расхода"
                },
                category = "Такси",
                amount = "1.2 руб"
            )
        }
    )

@Preview(showBackground = true)
@Composable
fun MainScreenLoadedPreview() {
    MoneyTrackerTheme(dynamicColor = false) {
        MainScreenContent(state = previewMainScreenUi())
    }
}
