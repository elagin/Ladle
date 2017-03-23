package ru.crew4dev.forksnknife.network;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by elagin on 23.03.17.
 */

public class GetListRequest extends HTTPClient {
    public GetListRequest(Context context) {
        this.context = context;
        post = new HashMap<>();
        post.put("method", "getlist");
    }
}
