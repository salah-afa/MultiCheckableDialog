package com.salahafaghani.multicheckabledialog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salahafaghani.multicheckabledialog.ui.theme.MultiCheckableDialogTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (viewModel.checkableList.isEmpty()) {
            viewModel.checkableList.addAll(getSampleCheckableData())
            viewModel.checkableState.putAll(getCheckableListStates(viewModel.checkableList))
        }

        setContent {
            MultiCheckableDialogTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: MainActivityViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                viewModel.openDialog.value = true
            }) {
            Text("Show Dialog")
        }
        MultiCheckableDialog(viewModel)
    }
}

@Composable
private fun GetItems(
    list: List<MultiCheckableItem>,
    checkableState: Map<Int, MutableState<Boolean>>
) {
    list.forEach {
        MultiCheckableItem(it, checkableState)
        if (it.nestedItems.isNotEmpty()) {
            GetItems(it.nestedItems, checkableState)
        }
    }
}

@Composable
fun MultiCheckableDialog(viewModel: MainActivityViewModel) {
    if (viewModel.openDialog.value) {
        AlertDialog(onDismissRequest = { viewModel.openDialog.value = false },
            modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 16.dp),
            text = {
                Column {
                    Row (modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 8.dp)) {
                        Text(
                            "Select Items",
                            fontSize = 20.sp
                        )
                        Checkbox(
                            checked = viewModel.checkAll.value,
                            onCheckedChange = { checked ->
                                viewModel.checkAll.value = checked
                                updateNestedItemsState(viewModel.checkableList, viewModel.checkableState, checked)
                            },
                            modifier = Modifier.padding(12.dp, 4.dp, 0.dp, 0.dp)
                        )
                    }

                    LazyColumn(
                        modifier = Modifier.weight(1f), contentPadding = PaddingValues(0.dp, 8.dp)
                    ) {
                        items(viewModel.checkableList) { level ->
                            MultiCheckableItem(level, viewModel.checkableState)
                            if (level.nestedItems.isNotEmpty()) {
                                GetItems(level.nestedItems, viewModel.checkableState)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(0.dp, 8.dp, 0.dp, 0.dp)
                            .align(Alignment.End)
                    ) {
                        Button(
                            onClick = {
                                viewModel.openDialog.value = false
                                updateCheckableList(viewModel.checkableList, viewModel.checkableState)
                            }, modifier = Modifier
                                .padding(8.dp, 0.dp, 0.dp, 0.dp)
                        ) {
                            Text("Set")
                        }

                        Button(
                            onClick = {
                                viewModel.openDialog.value = false
                                viewModel.checkableState.clear()
                                viewModel.checkableState.putAll(getCheckableListStates(viewModel.checkableList))
                            },
                            modifier = Modifier
                                .padding(8.dp, 0.dp, 0.dp, 0.dp)
                        ) {
                            Text("Cancel")
                        }
                    }
                }
            },
            confirmButton = { }
        )
    }
}

@Composable
fun MultiCheckableItem(item: MultiCheckableItem, states: Map<Int, MutableState<Boolean>>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                ((item.level - 1) * 40).dp,
                8.dp,
                8.dp,
                8.dp
            )
    ) {
        Checkbox(
            checked = states[item.id]!!.value,
            onCheckedChange = { checked ->
                states[item.id]!!.value = checked
                updateNestedItemsState(item.nestedItems, states, checked)
            },
            modifier = Modifier.padding(12.dp, 0.dp, 12.dp, 0.dp)
        )
        Text(
            text = item.name,
            fontSize = 16.sp
        )
    }
}

private fun getCheckableListStates(list: List<MultiCheckableItem>): MutableMap<Int, MutableState<Boolean>> {
    val states = mutableMapOf<Int, MutableState<Boolean>>()
    list.forEach {
        states[it.id] = mutableStateOf(it.checked)
        if (it.nestedItems.isNotEmpty()) {
            states.putAll(getCheckableListStates(it.nestedItems))
        }
    }
    return states
}

private fun updateCheckableList(
    list: List<MultiCheckableItem>,
    checkableState: Map<Int, MutableState<Boolean>>
) {
    list.forEach {
        it.checked = checkableState[it.id]!!.value
        if (it.nestedItems.isNotEmpty()) {
            updateCheckableList(it.nestedItems, checkableState)
        }
    }
}

private fun updateNestedItemsState(
    list: List<MultiCheckableItem>,
    checkableState: Map<Int, MutableState<Boolean>>,
    checked: Boolean
) {
    list.forEach {
        checkableState[it.id]!!.value = checked
        if (it.nestedItems.isNotEmpty()) {
            updateNestedItemsState(it.nestedItems, checkableState, checked)
        }
    }
}

private fun getSampleCheckableData(): List<MultiCheckableItem> {
    var id = 1
    val list = mutableListOf<MultiCheckableItem>()
    list.add(
        MultiCheckableItem(
            id++,
            "Item 1",
            false,
            1,
            listOf(
                MultiCheckableItem(
                    id++,
                    "Item 11",
                    false,
                    2,
                    listOf(
                        MultiCheckableItem(
                            id++,
                            "Item 111",
                            false,
                            3,
                            listOf()
                        ),
                        MultiCheckableItem(
                            id++,
                            "Item 112",
                            false,
                            3,
                            listOf()
                        )
                    )
                ),
                MultiCheckableItem(
                    id++,
                    "Item 12",
                    false,
                    2,
                    listOf()
                )
            )
        )
    )
    list.add(
        MultiCheckableItem(
            id++,
            "Item 2",
            false,
            1,
            listOf(
                MultiCheckableItem(
                    id++,
                    "Item 21",
                    false,
                    2,
                    listOf()
                )
            )
        )
    )
    list.add(
        MultiCheckableItem(
            id++,
            "Item 3",
            false,
            1,
            listOf(
                MultiCheckableItem(
                    id++,
                    "Item 31",
                    false,
                    2,
                    listOf(
                        MultiCheckableItem(
                            id++,
                            "Item 311",
                            false,
                            3,
                            listOf(
                                MultiCheckableItem(
                                    id++,
                                    "Item 3111",
                                    false,
                                    4,
                                    listOf()
                                )
                            )
                        )
                    )
                )
            )
        )
    )
    id += 1
    return list
}