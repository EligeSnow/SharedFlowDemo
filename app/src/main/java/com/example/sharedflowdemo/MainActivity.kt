package com.example.sharedflowdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.SharedFlow
import com.example.sharedflowdemo.ui.theme.SharedFlowDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharedFlowDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Передаем отступы Scaffold в экран, чтобы содержимое не перекрывало системные панели.
                    ScreenSetup(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ScreenSetup(
    modifier: Modifier = Modifier,
    viewModel: DemoViewModel = viewModel()
) {
    // ViewModel создается и сохраняется для текущего владельца композиции; экран получает только поток для чтения.
    MainScreen(modifier, viewModel.sharedFlow)
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    sharedFlow: SharedFlow<Int>
) {
    // remember сохраняет список между рекомпозициями, а изменение списка обновляет LazyColumn.
    val messages = remember { mutableStateListOf<Int>() }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(key1 = Unit) {
        // Сбор действует только в состоянии STARTED: в фоне корутина отменяется,
        // а при возвращении приложения сбор начинается заново.
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            sharedFlow.collect {
                // Диагностический вывод позволяет сверить сбор с эмиссией в Logcat.
                println("Collecting $it")
                messages.add(it)
            }
        }
    }

    // LazyColumn отображает только видимые элементы списка и обновляется при добавлении значений.
    LazyColumn(modifier = modifier) {
        items(messages) {
            Text(
                text = "Collected Value = $it",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SharedFlowDemoTheme {
        // Для Preview используем ViewModel, чтобы передать MainScreen требуемый SharedFlow.
        val viewModel: DemoViewModel = viewModel()
        MainScreen(sharedFlow = viewModel.sharedFlow)
    }
}