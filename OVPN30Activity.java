package de.blinkt.openvpn.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collection;

import de.blinkt.openvpn.LaunchVPN;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;
import de.blinkt.openvpn.activities.ConfigConverter;
import de.blinkt.openvpn.activities.DisconnectVPN;
import de.blinkt.openvpn.activities.FileSelect;
import de.blinkt.openvpn.activities.LogWindow;
import de.blinkt.openvpn.fragments.VPNProfileList;

public class OVPN30Activity extends BaseActivity {

    private static final int REQUEST_FILE = 500;
    private static final int REQUEST_IMPORT = 501;

    private LinearLayout root;
    private TextView status;
    private TextView profileCount;

    private int dp(int value) {
        return (int) (
                value
                        * getResources()
                        .getDisplayMetrics()
                        .density
                        + 0.5f
        );
    }

    private GradientDrawable rounded(
            int color,
            int radius
    ) {
        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));

        return drawable;
    }

    private TextView makeText(
            String value,
            int size,
            int color,
            boolean bold
    ) {
        TextView text =
                new TextView(this);

        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);
        text.setGravity(
                Gravity.CENTER_VERTICAL
        );

        text.setTypeface(
                Typeface.create(
                        "sans-serif",
                        bold
                                ? Typeface.BOLD
                                : Typeface.NORMAL
                )
        );

        return text;
    }

    private Button makeButton(
            String value
    ) {
        Button button =
                new Button(this);

        button.setText(value);
        button.setTextSize(15);
        button.setTextColor(Color.WHITE);
        button.setAllCaps(false);

        button.setGravity(
                Gravity.CENTER
        );

        button.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        button.setPadding(
                dp(10),
                dp(3),
                dp(10),
                dp(3)
        );

        button.setBackground(
                rounded(
                        Color.rgb(24, 116, 235),
                        18
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        dp(56)
                );

        params.setMargins(
                0,
                dp(5),
                0,
                dp(5)
        );

        button.setLayoutParams(params);

        return button;
    }

    private void addSpace(
            LinearLayout parent,
            int height
    ) {
        View space =
                new View(this);

        parent.addView(
                space,
                new LinearLayout.LayoutParams(
                        1,
                        dp(height)
                )
        );
    }

    @Override
    protected void onCreate(
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        getWindow().setStatusBarColor(
                Color.rgb(5, 8, 15)
        );

        getWindow().setNavigationBarColor(
                Color.rgb(5, 8, 15)
        );

        buildDashboard();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (status != null) {
            refreshStatus();
        }
    }

    private void buildDashboard() {

        ScrollView scroll =
                new ScrollView(this);

        scroll.setFillViewport(true);

        scroll.setBackgroundColor(
                Color.rgb(5, 8, 15)
        );

        root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setPadding(
                dp(20),
                dp(24),
                dp(20),
                dp(30)
        );

        scroll.addView(root);

        // --------------------------------
        // HEADER
        // --------------------------------

        TextView logo =
                makeText(
                        "OVPN30",
                        34,
                        Color.WHITE,
                        true
                );

        logo.setLetterSpacing(0.08f);

        root.addView(
                logo,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(52)
                )
        );

        TextView subtitle =
                makeText(
                        "SECURE VPN CLIENT",
                        11,
                        Color.rgb(
                                70,
                                155,
                                255
                        ),
                        true
                );

        subtitle.setLetterSpacing(0.12f);

        root.addView(
                subtitle,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(28)
                )
        );

        addSpace(root, 10);

        // --------------------------------
        // STATUS CARD
        // --------------------------------

        LinearLayout statusCard =
                new LinearLayout(this);

        statusCard.setOrientation(
                LinearLayout.VERTICAL
        );

        statusCard.setPadding(
                dp(20),
                dp(18),
                dp(20),
                dp(18)
        );

        statusCard.setBackground(
                rounded(
                        Color.rgb(
                                15,
                                24,
                                40
                        ),
                        22
                )
        );

        root.addView(
                statusCard,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        TextView connectionTitle =
                makeText(
                        "CONNECTION",
                        10,
                        Color.rgb(
                                125,
                                145,
                                170
                        ),
                        true
                );

        statusCard.addView(
                connectionTitle
        );

        status =
                makeText(
                        "●  Ready to connect",
                        21,
                        Color.rgb(
                                255,
                                190,
                                80
                        ),
                        true
                );

        statusCard.addView(
                status,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(50)
                )
        );

        profileCount =
                makeText(
                        "0 profiles",
                        12,
                        Color.rgb(
                                145,
                                165,
                                190
                        ),
                        false
                );

        statusCard.addView(
                profileCount
        );

        addSpace(root, 12);

        // --------------------------------
        // CONNECT
        // --------------------------------

        Button connect =
                makeButton(
                        "CONNECT"
                );

        connect.setTextSize(17);

        connect.setOnClickListener(
                v -> connectOrOpenProfiles()
        );

        root.addView(
                connect,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(60)
                )
        );

        addSpace(root, 10);

        // --------------------------------
        // PROFILE CARD
        // --------------------------------

        LinearLayout profileCard =
                new LinearLayout(this);

        profileCard.setOrientation(
                LinearLayout.VERTICAL
        );

        profileCard.setPadding(
                dp(18),
                dp(17),
                dp(18),
                dp(17)
        );

        profileCard.setBackground(
                rounded(
                        Color.rgb(
                                15,
                                24,
                                40
                        ),
                        22
                )
        );

        root.addView(
                profileCard,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        profileCard.addView(
                makeText(
                        "PROFILES",
                        18,
                        Color.WHITE,
                        true
                )
        );

        profileCard.addView(
                makeText(
                        "Manage your OpenVPN configurations",
                        12,
                        Color.rgb(
                                145,
                                165,
                                190
                        ),
                        false
                )
        );

        addSpace(
                profileCard,
                8
        );

        Button openProfiles =
                makeButton(
                        "Open Profiles"
                );

        openProfiles.setOnClickListener(
                v -> openProfiles()
        );

        profileCard.addView(
                openProfiles
        );

        Button importButton =
                makeButton(
                        "Import .OVPN"
                );

        importButton.setBackground(
                rounded(
                        Color.rgb(
                                22,
                                35,
                                55
                        ),
                        18
                )
        );

        importButton.setTextColor(
                Color.rgb(
                        80,
                        165,
                        255
                )
        );

        importButton.setOnClickListener(
                v -> importProfile()
        );

        profileCard.addView(
                importButton
        );

        addSpace(root, 12);

        // --------------------------------
        // TOOLS CARD
        // --------------------------------

        LinearLayout tools =
                new LinearLayout(this);

        tools.setOrientation(
                LinearLayout.VERTICAL
        );

        tools.setPadding(
                dp(18),
                dp(17),
                dp(18),
                dp(17)
        );

        tools.setBackground(
                rounded(
                        Color.rgb(
                                15,
                                24,
                                40
                        ),
                        22
                )
        );

        root.addView(
                tools,
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                )
        );

        tools.addView(
                makeText(
                        "TOOLS",
                        18,
                        Color.WHITE,
                        true
                )
        );

        Button logs =
                makeButton(
                        "Connection Logs"
                );

        logs.setBackground(
                rounded(
                        Color.rgb(
                                22,
                                35,
                                55
                        ),
                        18
                )
        );

        logs.setOnClickListener(
                v -> openLogs()
        );

        tools.addView(logs);

        Button settings =
                makeButton(
                        "Profile Settings"
                );

        settings.setBackground(
                rounded(
                        Color.rgb(
                                22,
                                35,
                                55
                        ),
                        18
                )
        );

        settings.setOnClickListener(
                v -> openProfiles()
        );

        tools.addView(settings);

        addSpace(root, 18);

        // --------------------------------
        // FOOTER
        // --------------------------------

        TextView footer =
                makeText(
                        "OVPN30  •  XOR SCRAMBLE",
                        10,
                        Color.rgb(
                                70,
                                90,
                                115
                        ),
                        false
                );

        footer.setGravity(
                Gravity.CENTER
        );

        root.addView(
                footer,
                new LinearLayout.LayoutParams(
                        -1,
                        dp(40)
                )
        );

        setContentView(scroll);

        refreshStatus();
    }

    private void refreshStatus() {

        boolean connected =
                VpnStatus.isVPNActive();

        if (connected) {

            status.setText(
                    "●  Connected"
            );

            status.setTextColor(
                    Color.rgb(
                            50,
                            220,
                            125
                    )
            );

        } else {

            status.setText(
                    "●  Ready to connect"
            );

            status.setTextColor(
                    Color.rgb(
                            255,
                            190,
                            80
                    )
            );
        }

        Collection<VpnProfile> list =
                ProfileManager
                        .getInstance(this)
                        .getProfiles();

        int count =
                list == null
                        ? 0
                        : list.size();

        if (count == 1) {

            profileCount.setText(
                    "1 profile"
            );

        } else {

            profileCount.setText(
                    count + " profiles"
            );
        }
    }

    private void connectOrOpenProfiles() {

        if (VpnStatus.isVPNActive()) {

            startActivity(
                    new Intent(
                            this,
                            DisconnectVPN.class
                    )
            );

            return;
        }

        Collection<VpnProfile> list =
                ProfileManager
                        .getInstance(this)
                        .getProfiles();

        if (
                list != null
                        && list.size() == 1
        ) {

            VpnProfile profile =
                    list.iterator().next();

            Intent intent =
                    new Intent(
                            this,
                            LaunchVPN.class
                    );

            intent.putExtra(
                    LaunchVPN.EXTRA_KEY,
                    profile
                            .getUUID()
                            .toString()
            );

            intent.setAction(
                    Intent.ACTION_MAIN
            );

            startActivity(intent);

        } else {

            openProfiles();
        }
    }

    private void openProfiles() {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(
                        android.R.id.content,
                        new VPNProfileList()
                )
                .addToBackStack(
                        "profiles"
                )
                .commit();
    }

    private void openLogs() {

        startActivity(
                new Intent(
                        this,
                        LogWindow.class
                )
        );
    }

    private void importProfile() {

        Intent intent =
                new Intent(
                        this,
                        FileSelect.class
                );

        intent.putExtra(
                FileSelect.NO_INLINE_SELECTION,
                true
        );

        intent.putExtra(
                FileSelect.WINDOW_TITLE,
                R.string.import_configuration_file
        );

        startActivityForResult(
                intent,
                REQUEST_FILE
        );
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (
                requestCode == REQUEST_FILE
                        && resultCode == Activity.RESULT_OK
                        && data != null
        ) {

            String fileData =
                    data.getStringExtra(
                            FileSelect.RESULT_DATA
                    );

            if (fileData == null) {
                return;
            }

            Uri uri =
                    Uri.parse(
                            "file://" + fileData
                    );

            Intent converter =
                    new Intent(
                            this,
                            ConfigConverter.class
                    );

            converter.setAction(
                    ConfigConverter.IMPORT_PROFILE
            );

            converter.setData(uri);

            startActivityForResult(
                    converter,
                    REQUEST_IMPORT
            );

        } else if (
                requestCode == REQUEST_IMPORT
                        && resultCode == Activity.RESULT_OK
        ) {

            Toast.makeText(
                    this,
                    "Profile imported",
                    Toast.LENGTH_SHORT
            ).show();

            refreshStatus();
        }
    }
}
