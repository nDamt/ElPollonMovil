package com.example.examenfinal

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ORSService {
    @Headers("Authorization: 5b3ce3597851110001cf6248aa349580a48b4d4ea61f0a42a998de59", "Content-Type: application/json")
    @POST("v2/directions/driving-car/geojson")
    fun getRoute(@Body body: RouteRequest): Call<ORSResponse>
}

data class RouteRequest(val coordinates: List<List<Double>>)
data class ORSResponse(val features: List<Feature>)
data class Feature(val geometry: Geometry)
data class Geometry(val coordinates: List<List<Double>>, val type: String)