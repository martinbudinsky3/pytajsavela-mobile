package com.example.mtaafe.utils

import android.util.Log
import com.example.mtaafe.data.models.ErrorEntity
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection

class ErrorHandler {
    companion object {
        fun getError(throwable: Throwable): ErrorEntity {
            return when(throwable) {
                is IOException -> ErrorEntity.Network
                is HttpException -> {
                    when(throwable.code()) {
                        // no cache found in case of no network, thrown by retrofit -> treated as network error
                        //DataConstants.Network.HttpStatusCode.UNSATISFIABLE_REQUEST -> Error.Network

                        // unauthorized
                        HttpURLConnection.HTTP_UNAUTHORIZED -> ErrorEntity.Unauthorized

                        // access denied
                        HttpURLConnection.HTTP_FORBIDDEN -> ErrorEntity.AccessDenied

                        // not found
                        HttpURLConnection.HTTP_NOT_FOUND -> ErrorEntity.NotFound

                        // unprocessable entity
                        422 -> ErrorEntity.Unprocessable

                        // all the others will be treated as unknown error
                        else -> ErrorEntity.Unknown
                    }
                }
                else -> ErrorEntity.Unknown
            }
        }
    }
}