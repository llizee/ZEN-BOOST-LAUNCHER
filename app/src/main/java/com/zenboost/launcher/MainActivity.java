package com.zenboost.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    LinearLayout root;

    final int purple = Color.rgb(176, 38, 255);
    final int black = Color.rgb(8, 10, 12);
    final int gray = Color.rgb(25, 29, 32);

    TextView createText(String text, int size, int color) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        return t;
    }

    Button createButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(purple);
        b.setBackgroundColor(gray);
        b.setAllCaps(false);
        b.setTextSize(16);
        return b;
    }

    void addSpace(int height) {
        View v = new View(this);
        root.addView(v, new LinearLayout.LayoutParams(1, height));
    }

    void setupRoot() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);
        root.setBackgroundColor(black);
        setContentView(root);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(black);
        getWindow().setNavigationBarColor(black);
        showHome();
    }

    void showHome() {
        setupRoot();
        root.setGravity(Gravity.CENTER);

        TextView title = createText("ZEN BOOST", 30, purple);
        root.addView(title);

        addSpace(8);
        root.addView(createText("PERFORMANCE INTERFACE", 12, Color.GRAY));

        addSpace(45);

        TextView clock = createText(
                new java.text.SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(new java.util.Date()),
                64, Color.WHITE);
        root.addView(clock);

        TextView date = createText(
                new java.text.SimpleDateFormat("EEEE, dd MMMM",
                        Locale.getDefault()).format(new java.util.Date()),
                16, Color.GRAY);
        root.addView(date);

        addSpace(35);

        Button apps = createButton("▦     APLICACIONES");
        root.addView(apps, new LinearLayout.LayoutParams(-1, 65));
        apps.setOnClickListener(v -> showApps());

        addSpace(12);

        Button gaming = createButton("⚡     MODO GAMING");
        root.addView(gaming, new LinearLayout.LayoutParams(-1, 65));
        gaming.setOnClickListener(v -> {
            Toast.makeText(this,
                    "Modo Gaming: siguiente fase",
                    Toast.LENGTH_SHORT).show();
        });

        addSpace(12);

        Button settings = createButton("⚙     AJUSTES DEL SISTEMA");
        root.addView(settings, new LinearLayout.LayoutParams(-1, 65));
        settings.setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_SETTINGS)));

        addSpace(35);
        root.addView(createText("LIGHT  •  FAST  •  CLEAN", 12, purple));
    }

    void showApps() {
        setupRoot();

        TextView title = createText("ZEN BOOST / APPS", 24, purple);
        root.addView(title);

        addSpace(10);

        Button back = createButton("← VOLVER");
        root.addView(back, new LinearLayout.LayoutParams(-1, 55));
        back.setOnClickListener(v -> showHome());

        addSpace(12);

        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setHint("Buscar aplicación...");
        search.setHintTextColor(Color.GRAY);
        search.setTextColor(Color.WHITE);
        search.setBackgroundColor(gray);
        search.setPadding(20, 12, 20, 12);
        root.addView(search, new LinearLayout.LayoutParams(-1, 55));

        addSpace(10);

        TextView count = createText("Cargando aplicaciones...", 14, Color.GRAY);
        root.addView(count);

        ScrollView scroll = new ScrollView(this);
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(list);

        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1f));

        List<ResolveInfo> apps = getLauncherApps();

        count.setText(apps.size() + " aplicaciones disponibles");

        renderApps(apps, list, search, count);

        search.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                    int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start,
                    int before, int count) {
                renderApps(apps, list, search, null);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    List<ResolveInfo> getLauncherApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> result = pm.queryIntentActivities(intent, 0);

        List<ResolveInfo> filtered = new ArrayList<>();

        for (ResolveInfo info : result) {
            if (info.activityInfo == null) continue;

            String packageName = info.activityInfo.packageName;

            if (!packageName.equals(getPackageName())) {
                filtered.add(info);
            }
        }

        Collections.sort(filtered, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                String nameA = a.loadLabel(pm).toString();
                String nameB = b.loadLabel(pm).toString();

                return nameA.toLowerCase(Locale.getDefault())
                        .compareTo(nameB.toLowerCase(Locale.getDefault()));
            }
        });

        return filtered;
    }

    void renderApps(List<ResolveInfo> apps,
                    LinearLayout list,
                    EditText search,
                    TextView count) {

        list.removeAllViews();

        String query = search.getText().toString()
                .trim().toLowerCase(Locale.getDefault());

        int shown = 0;

        for (ResolveInfo info : apps) {
            String name = info.loadLabel(getPackageManager()).toString();

            if (!name.toLowerCase(Locale.getDefault()).contains(query)) {
                continue;
            }

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(12, 8, 12, 8);
            row.setBackgroundColor(gray);

            TextView icon = new TextView(this);
            icon.setText("▣");
            icon.setTextColor(purple);
            icon.setTextSize(25);
            icon.setGravity(Gravity.CENTER);
            row.addView(icon, new LinearLayout.LayoutParams(55, 60));

            TextView label = new TextView(this);
            label.setText(name);
            label.setTextColor(Color.WHITE);
            label.setTextSize(16);
            label.setGravity(Gravity.CENTER_VERTICAL);
            row.addView(label, new LinearLayout.LayoutParams(
                    0, 60, 1f));

            TextView open = new TextView(this);
            open.setText("ABRIR  ›");
            open.setTextColor(purple);
            open.setTextSize(12);
            open.setGravity(Gravity.CENTER);
            row.addView(open, new LinearLayout.LayoutParams(90, 60));

            row.setOnClickListener(v -> {
                try {
                    Intent launch = getPackageManager()
                            .getLaunchIntentForPackage(
                                    info.activityInfo.packageName);

                    if (launch != null) {
                        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(launch);
                    } else {
                        Toast.makeText(this,
                                "No se puede abrir esta aplicación",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this,
                            "Error al abrir la aplicación",
                            Toast.LENGTH_SHORT).show();
                }
            });

            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(-1, -2);
            params.setMargins(0, 0, 0, 8);
            list.addView(row, params);

            shown++;
        }

        if (count != null) {
            count.setText(shown + " aplicaciones");
        }

        if (shown == 0) {
            TextView empty = createText(
                    "No se encontraron aplicaciones",
                    16, Color.GRAY);
            list.addView(empty);
        }
    }

    @Override
    public void onBackPressed() {
        showHome();
    }
}
