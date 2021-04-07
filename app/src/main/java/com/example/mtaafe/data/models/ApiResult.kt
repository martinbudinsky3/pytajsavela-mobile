package com.example.mtaafe.data.models


sealed class ApiResult<T> {

    data class Success<T>(val data: T) : ApiResult<T>()

    data class Error<T>(val error: ErrorEntity) : ApiResult<T>()
}