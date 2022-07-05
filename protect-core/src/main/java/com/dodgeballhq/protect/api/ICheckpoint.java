package com.dodgeballhq.protect.api;
import retrofit2.Call;
import retrofit2.http.*;

import com.dodgeballhq.protect.messages.*;

public interface ICheckpointRequest {
    @POST("/v1/verify")
    Call<CheckpointResponse> callCheckpoint(
            @Header("dodgeball-secret-key") String apiKey,
            @Header("dodgeball-source-id") String dbSourceId,
            @Header("dodgeball-custom-source-id") String clientId,
            @Body ClientCheckpointData body);
}
