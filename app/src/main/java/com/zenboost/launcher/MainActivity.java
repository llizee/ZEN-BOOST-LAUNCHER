package com.zenboost.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.BatteryManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private final int PURPLE = Color.rgb(176, 38, 255);
    private final int BLACK = Color.rgb(8, 10, 12);
    private final int GRAY = Color.rgb(25, 29, 32);
    private LinearLayout root;

    private TextView ramView;
    private TextView storageView;
    private TextView batteryView;

    private TextView createText(String text, int size, int color) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        t.setPadding(12, 12, 12, 12);
        return t;
    }

    private Button createButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(PURPLE);
        b.setBackgroundColor(GRAY);
        b.setAllCaps(false);
        b.setTextSize(15);
        return b;
    }

    private void addSpace(int height) {
        View v = new View(this);
        root.addView(v, new LinearLayout.LayoutParams(1, height));
    }

    private void addButton(String text, Runnable action) {
        Button b = createButton(text);
        root.addView(b, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 62));

        b.setOnClickListener(v -> action.run());
        addSpace(10);
    }

    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024L * 1024L)
            return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024L * 1024L)
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.2f GB",
                bytes / (1024.0 * 1024.0 * 1024.0));
    }

    private String getRamInfo() {
        android.app.ActivityManager am =
                (android.app.ActivityManager) getSystemService(ACTIVITY_SERVICE);

        android.app.ActivityManager.MemoryInfo mi =
                new android.app.ActivityManager.MemoryInfo();

        am.getMemoryInfo(mi);

        long total = mi.totalMem;
        long available = mi.availMem;
        long used = total - available;

        return "RAM USADA: " + formatSize(used)
                + "\nRAM DISPONIBLE: " + formatSize(available)
                + "\nRAM TOTAL: " + formatSize(total);
    }

    private String getStorageInfo() {
        StatFs stat = new StatFs(Environment.getDataDirectory().getPath());

        long total = stat.getTotalBytes();
        long free = stat.getAvailableBytes();
        long used = total - free;

        int percent = total > 0 ? (int) ((used * 100L) / total) : 0;

        return "ALMACENAMIENTO USADO: " + formatSize(used)
                + "\nESPACIO LIBRE: " + formatSize(free)
                + "\nCAPACIDAD TOTAL: " + formatSize(total)
                + "\nUSO: " + percent + "%";
    }

    private String getBatteryInfo() {
        BatteryManager bm = (BatteryManager)
                getSystemService(Context.BATTERY_SERVICE);

        int level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        return "BATERÍA: " + level + "%";
    }

    private void updateMonitor() {
        if (ramView != null) ramView.setText(getRamInfo());
        if (storageView != null) storageView.setText(getStorageInfo());
        if (batteryView != null) batteryView.setText(getBatteryInfo());
    }

    private void showApps(boolean gamesMode) {
        PackageManager pm = getPackageManager();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ApplicationInfo> apps =
                pm.queryIntentActivities(intent, 0) == null
                        ? new ArrayList<>()
                        : new ArrayList<>();

        for (android.content.pm.ResolveInfo ri :
                pm.queryIntentActivities(intent, 0)) {
            ApplicationInfo ai = ri.activityInfo.applicationInfo;

            if (ai.packageName.equals(getPackageName())) continue;

            if (!apps.contains(ai)) apps.add(ai);
        }

        ArrayList<ApplicationInfo> filtered = new ArrayList<>();

        for (ApplicationInfo ai : apps) {
            String label = pm.getApplicationLabel(ai).toString();

            if (gamesMode) {
                String s = label.toLowerCase();

                if (s.contains("game") || s.contains("play")
                        || s.contains("pubg") || s.contains("free fire")
                        || s.contains("minecraft") || s.contains("roblox")
                        || s.contains("cod") || s.contains("call of duty")
                        || s.contains("clash") || s.contains("genshin")
                        || s.contains("fortnite") || s.contains("mobile legends")
                        || s.contains("asphalt") || s.contains("brawl")
                        || s.contains("pokemon") || s.contains("wild rift")) {
                    filtered.add(ai);
                }
            } else {
                filtered.add(ai);
            }
        }

        filtered.sort((a, b) ->
                pm.getApplicationLabel(a).toString().compareToIgnoreCase(
                        pm.getApplicationLabel(b).toString()));

        if (filtered.isEmpty()) {
            Toast.makeText(this,
                    gamesMode ? "No se detectaron juegos automáticamente"
                            : "No se encontraron aplicaciones",
                    Toast.LENGTH_SHORT).show();

            if (gamesMode) showApps(false);
            return;
        }

        String[] names = new String[filtered.size()];

        for (int i = 0; i < filtered.size(); i++) {
            names[i] = pm.getApplicationLabel(filtered.get(i)).toString();
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(gamesMode ? "🎮 Selecciona un juego"
                        : "📱 Aplicaciones")
                .setItems(names, (d, which) -> {
                    ApplicationInfo selected = filtered.get(which);
                    Intent launch = pm.getLaunchIntentForPackage(selected.packageName);

                    if (launch != null) {
                        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(launch);
                    } else {
                        Toast.makeText(this,
                                "No se puede abrir esta aplicación",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cerrar", null)
                .create();

        dialog.show();
    }

    private void showRam() {
        new AlertDialog.Builder(this)
                .setTitle("📊 Monitor RAM")
                .setMessage(getRamInfo())
                .setPositiveButton("Actualizar", (d, w) -> showRam())
                .setNegativeButton("Cerrar", null)
                .show();
    }

    private void showStorage() {
        new AlertDialog.Builder(this)
                .setTitle("💾 Almacenamiento")
                .setMessage(getStorageInfo())
                .setPositiveButton("Ajustes", (d, w) -> {
                    try {
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_INTERNAL_STORAGE_SETTINGS));
                    } catch (Exception e) {
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_SETTINGS));
                    }
                })
                .setNegativeButton("Cerrar", null)
                .show();
    }

    private void showBattery() {
        new AlertDialog.Builder(this)
                .setTitle("🔋 Batería")
                .setMessage(getBatteryInfo()
                        + "\n\nZEN BOOST no puede forzar modos internos"
                        + " de batería, pero puedes configurarlos desde Android.")
                .setPositiveButton("Ahorro de batería", (d, w) -> {
                    try {
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_BATTERY_SAVER));
                    } catch (Exception e) {
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_SETTINGS));
                    }
                })
                .setNegativeButton("Cerrar", null)
                .show();
    }

    private void cleanCache() {
        long before = getCacheSize(getCacheDir());

        deleteContents(getCacheDir());

        long after = getCacheSize(getCacheDir());
        long cleaned = Math.max(0, before - after);

        Toast.makeText(this,
                "Caché propia limpiada: " + formatSize(cleaned),
                Toast.LENGTH_LONG).show();

        updateMonitor();
    }

    private long getCacheSize(File file) {
        if (file == null || !file.exists()) return 0;

        long size = 0;

        if (file.isFile()) return file.length();

        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) size += getCacheSize(f);
        }

        return size;
    }

    private void deleteContents(File file) {
        if (file == null || !file.exists()) return;

        File[] files = file.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) deleteContents(f);
            f.delete();
        }
    }

    private void showSettings() {
        startActivity(new Intent(
                android.provider.Settings.ACTION_SETTINGS));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(BLACK);
        getWindow().setNavigationBarColor(BLACK);

        ScrollView scroll = new ScrollView(this);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 30, 24, 30);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(BLACK);

        scroll.setBackgroundColor(BLACK);
        scroll.addView(root);

        setContentView(scroll);

        addSpace(25);

        root.addView(createText("ZEN BOOST 2.0", 30, PURPLE));
        root.addView(createText("ULTRA PERFORMANCE", 13, Color.GRAY));

        addSpace(18);

        TextView clock = createText("", 58, Color.WHITE);
        root.addView(clock);

        TextView date = createText("", 16, Color.GRAY);
        root.addView(date);

        Runnable updateClock = new Runnable() {
            @Override
            public void run() {
                java.util.Date now = new java.util.Date();

                clock.setText(new java.text.SimpleDateFormat(
                        "HH:mm").format(now));

                date.setText(new java.text.SimpleDateFormat(
                        "EEEE, dd MMMM").format(now));

                clock.postDelayed(this, 1000);
            }
        };

        clock.post(updateClock);

        addSpace(20);

        TextView monitorTitle =
                createText("SYSTEM MONITOR", 16, PURPLE);
        root.addView(monitorTitle);

        ramView = createText(getRamInfo(), 13, Color.WHITE);
        root.addView(ramView);

        storageView = createText(getStorageInfo(), 13, Color.WHITE);
        root.addView(storageView);

        batteryView = createText(getBatteryInfo(), 13, Color.WHITE);
        root.addView(batteryView);

        addSpace(18);

        addButton("📱  APLICACIONES", () -> showApps(false));

        addButton("🎮  GAMING PRO", () -> showApps(true));

        addButton("📊  MONITOR RAM", () -> showRam());

        addButton("💾  STORAGE MONITOR", () -> showStorage());

        addButton("🔋  BATTERY SAVER", () -> showBattery());

        addButton("🧹  SMART CLEANER", () -> {
            new AlertDialog.Builder(this)
                    .setTitle("Smart Cleaner")
                    .setMessage("Limpiar caché temporal de ZEN BOOST."
                            + "\nNo se borrarán datos de otras aplicaciones.")
                    .setPositiveButton("Limpiar", (d, w) -> cleanCache())
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        addButton("⚙️  SYSTEM SETTINGS", () -> showSettings());

        addSpace(20);

        root.addView(createText(
                "LIGHT  •  FAST  •  CLEAN", 12, PURPLE));

        addSpace(20);
    }
}
