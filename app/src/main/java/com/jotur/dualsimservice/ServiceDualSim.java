package com.jotur.dualsimservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import androidx.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.List;

public class ServiceDualSim extends Service {

    private Integer subActual = null;
    private Integer sim1 = null;
    private Integer sim2 = null;
    private SubscriptionInfo simInfo1;
    private SubscriptionInfo simInfo2;
    private Boolean dualSim;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        switchSim();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void switchSim() {

        SubscriptionManager subscriptionManager = SubscriptionManager.from(this);

        @SuppressLint("MissingPermission")
        List smList = subscriptionManager.getActiveSubscriptionInfoList();

        Method[] smMethods = subscriptionManager.getClass().getMethods();

        dualSim = smList.size() == 2;

        if (dualSim) {
            simInfo1 = (SubscriptionInfo) smList.get(0);
            simInfo2 = (SubscriptionInfo) smList.get(1);
            sim1 = simInfo1.getSubscriptionId();
            sim2 = simInfo2.getSubscriptionId();
        }

        for (Method m : smMethods) {
            if (m.getName().equals(("getDefaultDataSubscriptionId"))) {
                try {
                    subActual = (int) m.invoke(subscriptionManager);
                } catch (Exception e) {

                }
            }
        }

        for (Method m : smMethods) {
            if (m.getName().equals("setDefaultDataSubId") && dualSim) {
                try {
                    if (subActual == sim1) {
                        m.invoke(subscriptionManager, sim2);
                    }
                    if (subActual == sim2) {
                        m.invoke(subscriptionManager, sim1);
                    }
                } catch (Exception e) {

                }
            }
        }

    }

}
