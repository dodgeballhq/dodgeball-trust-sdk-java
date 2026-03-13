package com.dodgeballhq.protect.api;
import retrofit2.Call;
import retrofit2.http.*;

import com.dodgeballhq.protect.messages.*;

public interface IVerify {
    @GET("/v1/verification/{verificationId}")
    @Headers("Content-Type:application/json")
    Call<CheckpointResponse> callVerify(
            @Header("dodgeball-secret-key") String apiKey,
            @Header("dodgeball-source-token") String sourceToken,
            @Header("dodgeball-session-id") String sessionExternalId,
            @Header("dodgeball-customer-id") String customerExternalId,
            @Header("dodgeball-verification-id") String priorVerificationId,
            @Path("verificationId") String verificationId);
}
