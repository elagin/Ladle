package ru.crew4dev.forksnknife;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by elagin on 22.03.17.
 */

public class MyIntentService extends IntentService {

    private final BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);

    public static final String RESULT = "RESULT";
    public static final String RESULT_CODE = "result_code";

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            default:
                //mBroadcaster.broadcastIntentWithState(action, RESULT_SUCCSESS, "");
                break;
        }
    }
}
