package cn.wsgwz.gravity.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.Nullable;

import cn.wsgwz.gravity.helper.SpeedStatisticsHelper;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SpeedStatisticsService extends Service {
    private SpeedStatisticsHelper speedStatisticsHelper = SpeedStatisticsHelper.getInstance();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        speedStatisticsHelper.show(this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        speedStatisticsHelper.destroy();
        super.onDestroy();
    }
}
