package com.qlang.eyepetizer.bean

class FocusEntity<T : Any> {
    var value: T? = null
    var position: Int = -1

    constructor()

    constructor(value: T?) {
        this.value = value
    }

    fun setCurrent(value: T?, position: Int = -1) {
        this.value = value;this.position = position
    }

    fun clear() {
        value = null;position = -1
    }
}