package com.seif.chatapp.Fragments;

import com.seif.chatapp.Notifications.MyResponse;
import com.seif.chatapp.Notifications.Sender;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAhwBaJnk:APA91bH2YiDfuH80M16lBmpG-r915g0j3oIySexi4AmdlQfYj0iZCAD55lxeh_yMyXQDTYK5OE-IncUMIel7tEoBVjhxSiqkyTJF_gzgZ-iQwPWAJz8CCr9WQCFSW3aWsyqrtLsMyFtm"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
