package com.safety.hss.safetyfirstdata;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

public class GetAccData extends IntentService {

    private static final String ACTION_COLLECT = "com.safety.hss.safetyfirstdata.action.COLLECT";
    private static final String ACTION_STOP = "com.safety.hss.safetyfirstdata.action.STOP";


    private static final String EXTRA_PARAM1 = "com.safety.hss.safetyfirstdata.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.safety.hss.safetyfirstdata.extra.PARAM2";

    public GetAccData() {
        super("GetAccData");
    }

    public static void startActionCollect(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GetAccData.class);

        intent.setAction(ACTION_COLLECT);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public static void stopActionCollect(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GetAccData.class);
        intent.setAction(ACTION_STOP);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_COLLECT.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionCollect(param1, param2);
            } else if (ACTION_STOP.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionStop(param1, param2);
            }
        }
    }

    private void handleActionCollect(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void handleActionStop(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
