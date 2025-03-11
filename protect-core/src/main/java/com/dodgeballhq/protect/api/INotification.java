package com.dodgeballhq.protect.api;
import retrofit2.Call;
import retrofit2.http.*;

import com.dodgeballhq.protect.messages.*;

public interface INotification {
    @POST("/v1/track")
    @Headers("Content-Type:application/json")
    Call<NotificationResponse> callNotification(
            @Header("dodgeball-secret-key") String apiKey,
            @Header("dodgeball-session-id") String sessionExternalId,
            @Header("dodgeball-customer-id") String customerExternalId,
            @Body ApiEvent body);

}
