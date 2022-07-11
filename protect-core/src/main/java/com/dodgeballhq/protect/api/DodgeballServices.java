package com.dodgeballhq.protect.api;

import com.dodgeballhq.protect.Dodgeball;
import jdk.nashorn.internal.codegen.CompilerConstants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;

import com.dodgeballhq.protect.messages.*;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;


public class DodgeballServices {

    public static CheckpointResponse executeSynchronous(
            String baseUrl,
            String dbApiKey,
            CheckpointRequest request
    ) {
        try
        {
            CheckpointCall caller = new CheckpointCall(
                    baseUrl,
                    dbApiKey,
                    request
            );

            return caller.call();
        }
        catch (Exception exc){
            return new CheckpointResponse(exc);
        }
    }

    public static CompletableFuture<CheckpointResponse> executeAsync(
            String baseUrl,
            String dbApiKey,
            CheckpointRequest request
    ) throws Exception {
        CheckpointCall caller = new CheckpointCall(
                baseUrl,
                dbApiKey,
                request
        );

        CheckpointCall.Invoker invoker = caller.prepare();
        return CompletableFuture.supplyAsync(invoker::execute);
    }

    public static class CheckpointCall implements
            Callable<CheckpointResponse>,
            Callback<CheckpointResponse> {
        public CheckpointCall(
                String baseUrl,
                String dbApiKey,
                CheckpointRequest request
        ){
            this.baseUrl = baseUrl;
            this.dbApiKey = dbApiKey;
            this.request = request;
        }

        public static class Invoker{
            public Invoker(Call<CheckpointResponse> toCall){
                this.toCall = toCall;
            }

            public CheckpointResponse execute(){
                try {
                    Response<CheckpointResponse> response = this.toCall.execute();
                    if (response.isSuccessful()) {
                        return response.body();
                    } else {
                        return new CheckpointResponse(
                                false,
                                new DodgeballApiError[]{new DodgeballApiError(response.message())},
                                null,
                                false
                        );
                    }
                }
                catch(Exception exc){
                    return new CheckpointResponse(exc);
                }
            }

            private Call<CheckpointResponse> toCall;
        }

        @Override
        public CheckpointResponse call() throws Exception{
            try{
                Invoker invoker = this.prepare();
                return invoker.execute();
            }
            catch (Exception exc){
                return new CheckpointResponse(exc);
            }
        }

        public Invoker prepare() throws Exception {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ClientCheckpointData checkpointData = new ClientCheckpointData(request);

            ICheckpoint checkpoint = retrofit.create(ICheckpoint.class);
            Call<CheckpointResponse> toCall = checkpoint.callCheckpoint(
                    dbApiKey,
                    request.sourceToken,
                    request.sessionExternalId,
                    request.customerExternalId,
                    request.priorCheckpointId,
                    checkpointData
            );

            return new Invoker(toCall);
        }

        String baseUrl;
        String dbApiKey;
        public CheckpointRequest request;
        public CheckpointResponse response;

        @Override
        public void onResponse(
                Call<CheckpointResponse> rThis,
                Response<CheckpointResponse> response) {
            this.response = response.body();
        }

        @Override
        public void onFailure(Call<CheckpointResponse> rThis, Throwable throwable) {
            DodgeballApiError[] errors = {
                    new DodgeballApiError(
                            throwable.getMessage(),
                            throwable.getStackTrace().toString())
            };

            this.response = new CheckpointResponse(
                    false,
                    errors,
                    null,
                    false
            );
        }
    }

    public CheckpointResponse execute(
            String baseUrl,
            String dbApiKey,
            CheckpointRequest request
    ) {
        try
        {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ClientCheckpointData checkpointData = new ClientCheckpointData(request);

            ICheckpoint checkpoint = retrofit.create(ICheckpoint.class);
            Call<CheckpointResponse> toCall =  checkpoint.callCheckpoint(
                    dbApiKey,
                    request.sourceToken,
                    request.customerExternalId,
                    request.sessionExternalId,
                    request.priorCheckpointId,
                    checkpointData
            );

            final CheckpointResponse[] toReturn = {null};
            Callback<CheckpointResponse> callback =
                    new Callback<CheckpointResponse>() {
                        @Override
                        public void onResponse(Call<CheckpointResponse> call, Response<CheckpointResponse> response) {

                        }

                        @Override
                        public void onFailure(Call<CheckpointResponse> call, Throwable throwable) {

                        }
                    };

            toCall.enqueue(callback);
            return toReturn[0];
        }
        catch (Exception exc){

            Object stackTrace = exc.getStackTrace();
            String stackTraceString = null;
            if(stackTrace != null){
                stackTraceString = stackTrace.toString();
            }

            DodgeballApiError[] errors = {
                    new DodgeballApiError(
                            exc.getMessage(),
                            stackTraceString
                    )
            };

            return new CheckpointResponse(
                    false,
                    errors,
                    null,
                    false);
        }
    }
}