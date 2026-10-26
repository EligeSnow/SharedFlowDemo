package com.example.sharedflowdemo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class DemoViewModel : ViewModel() {

    // Изменяемый поток остается внутри ViewModel, снаружи доступна только версия для чтения.
    private val _sharedFlow = MutableSharedFlow<Int>()
    val sharedFlow = _sharedFlow.asSharedFlow()

    init {
        // Запускаем генератор при создании ViewModel; его работа продолжится при пересоздании экрана.
        sharedFlowInit()
    }

    private fun sharedFlowInit() {
        // viewModelScope автоматически отменит корутину при очистке ViewModel.
        viewModelScope.launch {
            // Отправляем значения с интервалом в две секунды, как задано в лабораторной работе.
            for (i in 1..1000) {
                delay(2000)
                // Диагностический вывод нужен для наблюдения за эмиссией в Logcat.
                println("Emitting $i")
                _sharedFlow.emit(i)
            }
        }
    }
}
