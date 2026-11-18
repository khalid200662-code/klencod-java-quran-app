/*
 * Copyright (c) 2026 خالد العامري (Khalid Al-Amri). All rights reserved.
 */
package com.khalid.alamri.quran;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class MainActivity extends Activity {
    private final List<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setTitle("القرآن الكريم");

        final ListView list = new ListView(this);
        list.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        setContentView(list);

        final ProgressBar bar = new ProgressBar(this);
        Toast.makeText(this, "جاري التحميل...", Toast.LENGTH_SHORT).show();

        new Thread(() -> {
            try {
                JSONArray arr = new JSONObject(
                    get("https://api.alquran.cloud/v1/surah")).getJSONArray("data");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject s = arr.getJSONObject(i);
                    titles.add(s.getInt("number") + " - " + s.getString("name")
                        + "  (" + s.getInt("numberOfAyahs") + " آية)");
                }
                runOnUiThread(() -> {
                    list.setAdapter(new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, titles) {
                        @Override
                        public View getView(int p, View v, android.view.ViewGroup g) {
                            TextView t = (TextView) super.getView(p, v, g);
                            t.setTextSize(20);
                            t.setPadding(32, 32, 32, 32);
                            t.setGravity(Gravity.RIGHT);
                            return t;
                        }
                    });
                    list.setOnItemClickListener((a, v, pos, id) -> {
                        Intent i = new Intent(this, SurahActivity.class);
                        i.putExtra("number", pos + 1);
                        i.putExtra("title", titles.get(pos));
                        startActivity(i);
                    });
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this,
                    "تعذر التحميل، تأكد من الإنترنت", Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    static String get(String u) throws Exception {
        HttpURLConnection c = (HttpURLConnection) new URL(u).openConnection();
        c.setConnectTimeout(15000);
        c.setReadTimeout(15000);
        BufferedReader r = new BufferedReader(
            new InputStreamReader(c.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) sb.append(line);
        r.close();
        return sb.toString();
    }
}
