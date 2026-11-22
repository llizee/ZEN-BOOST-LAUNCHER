package com.zenboost.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.provider.Settings;
import android.content.Context;

public class MainActivity extends Activity {

    LinearLayout root;
    int green = Color.rgb(176,38,255);
    int black = Color.rgb(8,10,12);
    int gray = Color.rgb(25,29,32);

    TextView createText(String text, int size, int color) {
        TextView t = new TextView(this);
        t.setText(text);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.CENTER);
        t.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        return t;
    }

    void addSpace(int height) {
        View v = new View(this);
        root.addView(v, new LinearLayout.LayoutParams(1,height));
    }

    Button createButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(green);
        b.setBackgroundColor(gray);
        b.setAllCaps(false);
        b.setTextSize(16);
        return b;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(black);
        getWindow().setNavigationBarColor(black);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28,40,28,24);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(black);

        setContentView(root);

        TextView title = createText("ZEN BOOST", 30, green);
        root.addView(title);

        addSpace(8);

        TextView subtitle = createText("PERFORMANCE INTERFACE", 12, Color.GRAY);
        root.addView(subtitle);

        addSpace(45);

        TextView clock = createText(
                new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date()),
                64, Color.WHITE);
        root.addView(clock);

        TextView date = createText(
                new java.text.SimpleDateFormat("EEEE, dd MMMM").format(new java.util.Date()),
                16, Color.GRAY);
        root.addView(date);

        addSpace(35);

        Button apps = createButton("▦     APLICACIONES");
        root.addView(apps, new LinearLayout.LayoutParams(-1,65));

        apps.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            Toast.makeText(this,
                    "Próxima versión: lista de aplicaciones",
                    Toast.LENGTH_SHORT).show();
        });

        addSpace(12);

        Button gaming = createButton("⚡     MODO GAMING");
        root.addView(gaming, new LinearLayout.LayoutParams(-1,65));

        gaming.setOnClickListener(v -> {
            Toast.makeText(this,
                    "ZEN GAMING próximamente",
                    Toast.LENGTH_SHORT).show();
        });

        addSpace(12);

        Button settings = createButton("⚙     AJUSTES DEL SISTEMA");
        root.addView(settings, new LinearLayout.LayoutParams(-1,65));

        settings.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        });

        addSpace(35);

        TextView footer = createText("LIGHT  •  FAST  •  CLEAN", 12, green);
        root.addView(footer);
    }
}
