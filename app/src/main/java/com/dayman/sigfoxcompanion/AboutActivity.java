package com.dayman.sigfoxcompanion;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        tb.setTitle(R.string.about_activity_title);

        TextView aboutTextSourceLink = (TextView) findViewById(R.id.about_text_source_link);

        // Perhaps use Chrome WebView here at some point
        String source_code_link = "<a href=\"https://github.com/PycomCompanion/sigfoxcompanion\">Source code</a>";

        if (Build.VERSION.SDK_INT >= 24)
            aboutTextSourceLink.setText(Html.fromHtml(source_code_link, Html.FROM_HTML_MODE_LEGACY));
        else
            Html.fromHtml(source_code_link);

        aboutTextSourceLink.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
