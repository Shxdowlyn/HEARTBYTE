package com.heartbyte.app;

import android.Manifest;
import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {

    private final Handler handler = new Handler(Looper.getMainLooper());

    private TextView terminal;
    private Button startButton, stopButton;

    private boolean running = false;
    private int sent = 0;

    private final String[] messages = {
        "Señal detectada: alguien te mandó cariño. 💗",
        "Escaneo completado: 100% ternura.",
        "Alerta rosa: se encontró una sonrisa pendiente.",
        "HEARTBYTE: transmisión de afecto recibida.",
        "Revelación: no era un virus. Era una sorpresa. 💗"
    };

    private static final String CHANNEL = "heartbyte_demo";
    private static final int NOTIFICATION_ID = 4101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createChannel();
        buildScreen();

        log("HEARTBYTE.exe iniciado");
        log("ROMANTIC CYBER LAB");
        log("Esperando autorización...");
    }

    private void buildScreen() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 28, 24, 24);
        root.setBackgroundColor(Color.rgb(8, 8, 13));

        TextView title = new TextView(this);
        title.setText("HEARTBYTE.exe");
        title.setTextColor(Color.rgb(255, 80, 180));
        title.setTextSize(27);
        title.setGravity(Gravity.CENTER);

        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("ROMANTIC CYBER LAB // PROJECT 001");
        subtitle.setTextColor(Color.rgb(120, 255, 170));
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 10, 0, 20);

        root.addView(subtitle);

        ScrollView scroll = new ScrollView(this);

        terminal = new TextView(this);
        terminal.setTextColor(Color.rgb(130, 255, 170));
        terminal.setTextSize(13);
        terminal.setPadding(14, 14, 14, 14);
        terminal.setBackgroundColor(Color.rgb(17, 17, 24));

        scroll.addView(terminal);

        root.addView(scroll,
            new LinearLayout.LayoutParams(
                -1, 0, 1f
            )
        );

        startButton = new Button(this);
        startButton.setText("INICIAR SIMULACIÓN");
        startButton.setOnClickListener(v -> startSimulation());

        root.addView(startButton);

        stopButton = new Button(this);
        stopButton.setText("DETENER");
        stopButton.setOnClickListener(v -> stopSimulation());
        stopButton.setEnabled(false);

        root.addView(stopButton);

        TextView note = new TextView(this);
        note.setText("Simulación ficticia. Podés detenerla cuando quieras.");
        note.setTextColor(Color.LTGRAY);
        note.setPadding(4, 14, 4, 0);

        root.addView(note);

        setContentView(root);
    }

    private void startSimulation() {

        if (running) return;

        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                77
            );

            log("Solicitando permiso...");
            return;
        }

        sent = 0;
        running = true;

        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        log("Permiso disponible.");
        log("Iniciando secuencia...");

        handler.post(notificationStep);
    }

    @Override
    public void onRequestPermissionsResult(
        int requestCode,
        String[] permissions,
        int[] results
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, results
        );

        if (requestCode == 77) {
            if (results.length > 0 &&
                results[0] == PackageManager.PERMISSION_GRANTED) {

                startSimulation();

            } else {
                log("Permiso denegado.");
            }
        }
    }

    private final Runnable notificationStep = new Runnable() {

        @Override
        public void run() {

            if (!running) return;

            if (sent >= messages.length) {
                finishSimulation();
                return;
            }

            sendNotification(messages[sent]);

            log("> " + messages[sent]);

            sent++;

            if (sent < messages.length) {
                handler.postDelayed(this, 4000);
            } else {
                finishSimulation();
            }
        }
    };

    private void finishSimulation() {
        running = false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        log("Secuencia finalizada.");
    }

    private void stopSimulation() {

        running = false;

        handler.removeCallbacks(notificationStep);

        NotificationManager nm =
            (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE
            );

        nm.cancel(NOTIFICATION_ID);

        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        log("DETENIDO por el usuario.");
    }

    private void sendNotification(String message) {

        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {

            stopSimulation();
            return;
        }

        Notification.Builder builder;

        if (Build.VERSION.SDK_INT >= 26) {
            builder = new Notification.Builder(this, CHANNEL);
        } else {
            builder = new Notification.Builder(this);
        }

        Notification notification = builder
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("HEARTBYTE · simulación")
            .setContentText(message)
            .setStyle(
                new Notification.BigTextStyle().bigText(message)
            )
            .setAutoCancel(true)
            .build();

        NotificationManager nm =
            (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE
            );

        nm.notify(NOTIFICATION_ID, notification);
    }

    private void createChannel() {

        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel channel =
                new NotificationChannel(
                    CHANNEL,
                    "HEARTBYTE · simulación",
                    NotificationManager.IMPORTANCE_DEFAULT
                );

            channel.setDescription(
                "Avisos locales de HEARTBYTE."
            );

            NotificationManager nm =
                getSystemService(NotificationManager.class);

            nm.createNotificationChannel(channel);
        }
    }

    private void log(String message) {

        if (terminal != null) {
            terminal.append(message + "\n");
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(notificationStep);
        super.onDestroy();
    }
}