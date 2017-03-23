package ru.crew4dev.forksnknife;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import ru.crew4dev.forksnknife.network.GetListRequest;
import ru.crew4dev.forksnknife.network.RequestErrors;

import static android.content.RestrictionsManager.RESULT_ERROR;

/**
 * Created by elagin on 22.03.17.
 */

public class MyIntentService extends IntentService {

    private MyApp myApp = null;

    public static final String RESULT = "RESULT";
    public static final String RESULT_CODE = "result_code";

    public final static int RESULT_SUCCSESS = 0;
    public final static int RESULT_ERROR = 1;

    public static final String ACTION_GET_LIST = "ru.crew4dev.forksnknife.action.GetList";

    private final BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);

    public MyIntentService() {
        super("MyIntentService");
    }

    public void onCreate() {
        super.onCreate();
        myApp = (MyApp) getApplicationContext();
    }

    private static Intent newIntent(Context context, String action) {
        Intent res = new Intent(context, MyIntentService.class);
        res.setAction(action);
        return res;
    }

    public static void startActionGetPointList(Context context) {
        Intent intent = newIntent(context, ACTION_GET_LIST);
        context.startService(intent);
    }

    private JSONObject handleActionGetPointList() {
        return new GetListRequest(this).request(myApp.getPreferences().getServerURI());
    }

    private void returnError(JSONObject response, String action) {
        mBroadcaster.broadcastIntentWithState(action, RESULT_ERROR, RequestErrors.getError(response));
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case ACTION_GET_LIST:
                JSONObject pointList = handleActionGetPointList();
                if (RequestErrors.isError(pointList)) {
                    returnError(pointList, action);
                } else {
                    //myApp.getPoints().updatePointsList(pointList.getJSONArray(RESULT));
                    mBroadcaster.broadcastIntentWithState(action, RESULT_SUCCSESS, "");
                }
                break;

            default:
                //mBroadcaster.broadcastIntentWithState(action, RESULT_SUCCSESS, "");
                break;
        }
    }
}
