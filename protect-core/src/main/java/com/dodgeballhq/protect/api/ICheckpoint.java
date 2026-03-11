package com.dodgeballhq.protect.api;
import retrofit2.Call;
import retrofit2.http.*;

import com.dodgeballhq.protect.messages.*;

public interface ICheckpoint {
    @POST("/v1/checkpoint")
    @Headers("Content-Type:application/json")
    Call<CheckpointResponse> callCheckpoint(
            @Header("dodgeball-secret-key") String apiKey,
            @Header("dodgeball-source-token") String sourceToken,
            @Header("dodgeball-session-id") String sessionExternalId,
            @Header("dodgeball-customer-id") String customerExternalId,
            @Header("dodgeball-verification-id") String priorVerificationId,
            @Body ClientCheckpointData body);

}
