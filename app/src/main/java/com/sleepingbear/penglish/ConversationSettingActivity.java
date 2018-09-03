package com.sleepingbear.penglish;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConversationSettingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        ((EditText) findViewById(R.id.my_et_line_height)).setText(DicUtils.getPreferencesValue(this, CommConstants.preferences_convLineHeight));
        ((EditText) findViewById(R.id.my_et_font_weight)).setText(DicUtils.getPreferencesValue(this, CommConstants.preferences_convFontWeight));

        ((Button) findViewById(R.id.my_b_default)).setOnClickListener(this);

        DicUtils.setAdView(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            DicUtils.setPreferences(this, CommConstants.preferences_convLineHeight, ((EditText) findViewById(R.id.my_et_line_height)).getText().toString());
            DicUtils.setPreferences(this, CommConstants.preferences_convFontWeight, ((EditText) findViewById(R.id.my_et_font_weight)).getText().toString());

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.my_b_default:
                ((EditText) findViewById(R.id.my_et_line_height)).setText("120");
                ((EditText) findViewById(R.id.my_et_font_weight)).setText("36");

                break;
        }
    }
}
