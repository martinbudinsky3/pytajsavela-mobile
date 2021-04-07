package com.example.mtaafe.data.models

sealed class ErrorEntity {

    object Network : ErrorEntity()

    object NotFound : ErrorEntity()

    object Unauthorized : ErrorEntity()

    object AccessDenied : ErrorEntity()

    object Unprocessable : ErrorEntity()

    object Unknown : ErrorEntity()
}