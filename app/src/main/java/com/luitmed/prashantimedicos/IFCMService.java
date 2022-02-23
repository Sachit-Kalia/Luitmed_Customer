package com.luitmed.prashantimedicos;

import com.luitmed.prashantimedicos.Models.FCMResponse;
import com.luitmed.prashantimedicos.Models.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA-BFtc2c:APA91bFwedLUN7cWF3abFd24s8KHYxtpYwXfLOJ5wx9vCJ823ic_DtJDdpDZUjBJtuxvKBefDwtRCFgTazvLJq9kcqoWuKjBZv1RjB8AnFgZKtdPYedWpscCpOJKNBknh04GkfVGNKTQ"

    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);

}
