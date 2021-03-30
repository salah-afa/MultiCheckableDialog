package com.salahafaghani.multicheckabledialog

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainActivityViewModel : ViewModel() {
    val openDialog = mutableStateOf(false)
    val checkAll = mutableStateOf(false)
    val checkableState = mutableMapOf<Int, MutableState<Boolean>>()
    val checkableList = mutableListOf<MultiCheckableItem>()
}