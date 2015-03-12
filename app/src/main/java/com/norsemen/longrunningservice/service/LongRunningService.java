package com.norsemen.longrunningservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

import com.norsemen.longrunningservice.R;
import com.norsemen.longrunningservice.util.Constants;


public class LongRunningService extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;	// Handler that receives messages from the thread
    private Messenger messenger;

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread handlerThread = new HandlerThread("LongRunningService", Thread.NORM_PRIORITY);
        handlerThread.start();

        // Get the HandlerThread's Looper and use it for the service handler
        serviceLooper = handlerThread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "LongRunningService starting", Toast.LENGTH_SHORT).show();

        messenger = (Messenger)intent.getExtras().get(Constants.MESSENGER);

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message message = serviceHandler.obtainMessage();
        message.arg1 = startId;
        serviceHandler.sendMessage(message);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "LongRunningService done", Toast.LENGTH_SHORT).show();
    }

    class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                Thread.sleep(2000);

                messenger.send(Message.obtain(null, Constants.MESSAGE_ONE, getApplicationContext().getString(R.string.message1)));

                Thread.sleep(5000);

                messenger.send(Message.obtain(null, Constants.MESSAGE_TWO, getApplicationContext().getString(R.string.message2)));

                Thread.sleep(5000);

                messenger.send(Message.obtain(null, Constants.MESSAGE_THREE, getApplicationContext().getString(R.string.message3)));

                Thread.sleep(5000);

                messenger.send(Message.obtain(null, Constants.MESSAGE_FOUR, getApplicationContext().getString(R.string.message4)));


            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }
}