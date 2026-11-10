package dev.betterfy.circlemusic;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashSet;
import java.util.HashMap;

public class MainActivity extends Activity {
    private int background, surface, selectedSurface, text, secondary, accent;
    private RadioGroup group;
    private final HashMap<String, String> actions = new HashMap<>();

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        boolean dark = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        if (Build.VERSION.SDK_INT >= 31) {
            background = getColor(dark ? android.R.color.system_neutral1_900 : android.R.color.system_neutral1_10);
            surface = getColor(dark ? android.R.color.system_neutral1_800 : android.R.color.system_neutral1_100);
            selectedSurface = getColor(dark ? android.R.color.system_accent1_700 : android.R.color.system_accent1_100);
            text = getColor(dark ? android.R.color.system_neutral1_50 : android.R.color.system_neutral1_900);
            secondary = getColor(dark ? android.R.color.system_neutral2_200 : android.R.color.system_neutral2_700);
            accent = getColor(dark ? android.R.color.system_accent1_200 : android.R.color.system_accent1_700);
        } else {
            background = Color.parseColor(dark ? "#201719" : "#faf8f8"); surface = Color.parseColor(dark ? "#382b2d" : "#efebeb");
            selectedSurface = Color.parseColor(dark ? "#683d40" : "#eedcdc"); text = Color.parseColor(dark ? "#f3dddd" : "#231d1d");
            secondary = Color.parseColor(dark ? "#d3bdbd" : "#5f5252"); accent = Color.parseColor(dark ? "#f1b4b4" : "#7d4c4c");
        }
        getWindow().setStatusBarColor(background);
        getWindow().setNavigationBarColor(background);
        getWindow().getDecorView().setSystemUiVisibility(dark ? 0 : View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(background);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(48), dp(20), dp(32));
        scroll.addView(root);
        setContentView(scroll);

        TextView eyebrow = label("CIRCLE MUSIC", 12, accent);
        eyebrow.setLetterSpacing(0.08f);
        eyebrow.setTypeface(null, Typeface.BOLD);
        root.addView(eyebrow);
        TextView title = label("Музыка\nв твоём плеере", 36, text);
        title.setLetterSpacing(-0.025f);
        title.setLineSpacing(dp(2), 1f);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(-1, -2);
        titleParams.setMargins(0, dp(12), 0, dp(12));
        root.addView(title, titleParams);
        TextView description = label("Находи треки через Circle to Search и открывай поиск в любимом приложении.", 16, secondary);
        description.setLineSpacing(dp(4), 1f);
        root.addView(description);

        TextView section = label("Плеер", 16, accent);
        section.setTypeface(null, Typeface.BOLD);
        LinearLayout.LayoutParams sectionParams = new LinearLayout.LayoutParams(-1, -2);
        sectionParams.setMargins(dp(4), dp(38), 0, dp(12));
        root.addView(section, sectionParams);
        group = new RadioGroup(this);
        root.addView(group, new LinearLayout.LayoutParams(-1, -2));
        String selected = getSharedPreferences("settings", 0).getString("player", "");
        addPlayer("Не перенаправлять", "", null, selected);
        HashSet<String> musicApps = new HashSet<>();
        Intent music = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MUSIC);
        for (ResolveInfo info : getPackageManager().queryIntentActivities(music, 0)) musicApps.add(info.activityInfo.packageName);
        HashSet<String> seen = new HashSet<>();
        Intent youtubeSearch = PlayerSearch.create(PlayerSearch.YOUTUBE_MUSIC, null, "Circle Music");
        ResolveInfo youtube = getPackageManager().resolveActivity(youtubeSearch, android.content.pm.PackageManager.MATCH_DEFAULT_ONLY);
        if (youtube != null && youtube.activityInfo.exported) {
            seen.add(PlayerSearch.YOUTUBE_MUSIC);
            actions.put(PlayerSearch.YOUTUBE_MUSIC, SearchAction.ANDROID);
            ApplicationInfo app = youtube.activityInfo.applicationInfo;
            addPlayer(app.loadLabel(getPackageManager()).toString(), PlayerSearch.YOUTUBE_MUSIC,
                app.loadIcon(getPackageManager()), selected);
        }
        for (String action : new String[] {SearchAction.ANDROID, SearchAction.GOOGLE}) {
            for (ResolveInfo info : getPackageManager().queryIntentActivities(new Intent(action), 0)) {
                String name = info.activityInfo.packageName;
                if (PlayerSearch.YOUTUBE_MUSIC.equals(name)) continue;
                if (!musicApps.contains(name) && info.activityInfo.applicationInfo.category != ApplicationInfo.CATEGORY_AUDIO) continue;
                if (!seen.add(name)) continue;
                actions.put(name, action);
                addPlayer(info.loadLabel(getPackageManager()).toString(), name, info.loadIcon(getPackageManager()), selected);
            }
        }
        group.setOnCheckedChangeListener((g, id) -> {
            for (int i = 0; i < group.getChildCount(); i++) {
                RadioButton row = (RadioButton) group.getChildAt(i);
                row.setBackground(card(row.isChecked() ? selectedSurface : surface));
            }
            RadioButton row = findViewById(id);
            if (row != null) {
                getSharedPreferences("settings", 0).edit().putString("player", (String) row.getTag()).putString("search_action", actions.get((String) row.getTag())).apply();
                restartGoogle();
            }
        });

        TextView note = label("Для работы включи Circle Music в LSPosed для приложения Google и перезапусти Google.", 14, secondary);
        note.setLineSpacing(dp(4), 1f);
        LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(-1, -2);
        noteParams.setMargins(dp(4), dp(28), dp(4), 0);
        root.addView(note, noteParams);
    }

    private void restartGoogle() {
        new Thread(() -> {
            boolean success = false;
            try {
                success = RestartGoogle.waitFor(Runtime.getRuntime().exec(new String[] {"su", "-c", "am force-stop com.google.android.googlequicksearchbox"}));
            } catch (Exception ignored) {
                if (ignored instanceof InterruptedException) Thread.currentThread().interrupt();
            }
            boolean stopped = success;
            runOnUiThread(() -> Toast.makeText(this, stopped ? "Google перезапустится при следующем поиске" : "Не удалось перезапустить Google — проверь доступ root", Toast.LENGTH_LONG).show());
        }).start();
    }

    private void addPlayer(String label, String packageName, Drawable icon, String selected) {
        RadioButton row = new RadioButton(this);
        row.setId(View.generateViewId());
        row.setTag(packageName);
        row.setText(label);
        row.setTextColor(text);
        row.setTextSize(16);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setMinHeight(dp(76));
        row.setPadding(dp(18), dp(12), dp(18), dp(12));
        row.setButtonTintList(new ColorStateList(new int[][] { new int[] {android.R.attr.state_checked}, new int[] {} }, new int[] {accent, secondary}));
        row.setCompoundDrawablePadding(dp(14));
        if (icon != null) {
            icon.setBounds(0, 0, dp(32), dp(32));
            row.setCompoundDrawables(icon, null, null, null);
        }
        boolean checked = packageName.equals(selected);
        row.setBackground(card(checked ? selectedSurface : surface));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dp(4));
        group.addView(row, params);
        if (checked) group.check(row.getId());
    }

    private TextView label(String value, int size, int color) {
        TextView view = new TextView(this);
        view.setText(value); view.setTextSize(size); view.setTextColor(color);
        return view;
    }
    private GradientDrawable card(int color) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color); shape.setCornerRadius(dp(24));
        return shape;
    }
    private int dp(float value) { return (int) (value * getResources().getDisplayMetrics().density + 0.5f); }
}


