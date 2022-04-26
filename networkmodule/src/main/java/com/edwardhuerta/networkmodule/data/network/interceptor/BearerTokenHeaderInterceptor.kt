package com.edwardhuerta.networkmodule.data.network.interceptor

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class BearerTokenHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().signedRequest()
        return chain.proceed(newRequest)
    }

    private fun Request.signedRequest(): Request = runBlocking {

        val processedRequest: Request = newBuilder()
            .addHeader("Authorization", "Bearer TOKEN_GOES_HERE")
            .build()

        processedRequest
    }
}