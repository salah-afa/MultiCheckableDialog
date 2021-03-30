package com.salahafaghani.multicheckabledialog

data class MultiCheckableItem (
    val id: Int,
    val name: String,
    var checked: Boolean,
    val level: Int,
    val nestedItems: List<MultiCheckableItem>
)