package com.studbudd.application_tracker.common.models

sealed class Resource<T>(
    val message: String,
    val data: T? = null
) {

    class Success<T>(data: T, message: String? = null):
        Resource<T>(message ?: "Task Successful", data)

    class Failure<T>(message: String, data: T? = null): Resource<T>(message, data)

    class LoggedOut<T>(): Resource<T>("Login Required")

}