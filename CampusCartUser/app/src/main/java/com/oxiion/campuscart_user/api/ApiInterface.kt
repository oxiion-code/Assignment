package com.oxiion.campuscart_user.api

import com.oxiion.campuscart_user.data.model.CheckStatus
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path

interface ApiInterface {
     @GET("apis/pg-sandbox/pg/v1/status/{merchantId}/{transactionId}")
     suspend fun getPaymentStatus(
       @HeaderMap headers:Map<String, String>,
       @Path("merchantId") merchantId:String,
       @Path("transactionId") transactionId:String
     ):Response<CheckStatus>
}