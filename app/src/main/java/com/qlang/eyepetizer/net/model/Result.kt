package com.qlang.eyepetizer.net.model

class Result<out T> constructor(private val _value: T?) {

    val isSuccess: Boolean get() = _value !is Failure
    val isFailure: Boolean get() = _value is Failure

    val value: T?
        get() = when {
            isFailure -> null
            else -> _value as T
        }

    val exception: Throwable?
        get() = when (_value) {
            is Failure -> _value.exception
            else -> null
        }

    val errorMessage: String?
        get() = when (_value) {
            is Failure -> _value.msg
            else -> null
        }

    override fun toString(): String =
            when (_value) {
                is Failure -> _value.toString() // "Failure($exception)"
                else -> "Success($_value)"
            }

    companion object {
        fun <T> success(value: T?): Result<T> =
                Result(value)

        fun <T> failure(exception: Throwable, msg: String? = ""): Result<T> =
                Result(Failure(exception, msg) as? T?)
    }

    class Failure(val exception: Throwable, val msg: String? = "") {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }
}