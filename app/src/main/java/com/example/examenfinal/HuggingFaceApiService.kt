package com.example.examenfinal

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface HuggingFaceApiService {
    @Headers("Content-Type: application/json")
    @POST("predict")
    fun predict(@Body data: Map<String?, Int?>?): Call<Map<String?, Any?>?>?
}