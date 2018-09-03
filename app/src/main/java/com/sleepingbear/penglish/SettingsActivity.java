package com.sleepingbear.penglish;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener{

    private static final String TAG = "PreSettingsActivity";

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private PreferenceScreen screen;
    private ListPreference mFontSize;
    private ListPreference mWebViewFontSize;
    private ListPreference mWordView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);

        screen = getPreferenceScreen();

        mFontSize = (ListPreference) screen.findPreference("key_fontSize");
        mFontSize.setOnPreferenceChangeListener(this);

        mWebViewFontSize = (ListPreference) screen.findPreference("key_webViewFontSize");
        mWebViewFontSize.setOnPreferenceChangeListener(this);

        mWordView = (ListPreference) screen.findPreference("key_wordView");
        mWordView.setOnPreferenceChangeListener(this);

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();
    }

    @Override
    public void onResume(){

        super.onResume();

        updateSummary();
        DicUtils.dicLog("onResume");
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference) {
        DicUtils.dicLog("onPreferenceTreeClick : " + preference.getKey());
        if ( preference.getKey().equals("key_backup") ) {
            //layout 구성
            LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View dialog_layout = li.inflate(R.layout.dialog_backup, null);

            //dialog 생성..
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setView(dialog_layout);
            final android.app.AlertDialog alertDialog = builder.create();

            final EditText et_saveName = ((EditText) dialog_layout.findViewById(R.id.my_d_dm_et_save));
            et_saveName.setText("backup_" + DicUtils.getCurrentDate() + ".xls");
            ((Button) dialog_layout.findViewById(R.id.my_b_save)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String saveFileName = et_saveName.getText().toString();
                    if ("".equals(saveFileName)) {
                        Toast.makeText(getApplicationContext(), "저장할 파일명을 입력하세요.", Toast.LENGTH_SHORT).show();
                    } else if (saveFileName.indexOf(".") > -1 && !"xls".equals(saveFileName.substring(saveFileName.length() - 3, saveFileName.length()).toLowerCase())) {
                        Toast.makeText(getApplicationContext(), "확장자는 xls 입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        String fileName = DicUtils.getFileName(saveFileName, "xls");

                        boolean isSave = DicUtils.writeExcelBackup(getApplicationContext(), db, fileName);
                        if ( isSave ) {
                            Toast.makeText(getApplicationContext(), "백업 데이타를 정상적으로 내보냈습니다.", Toast.LENGTH_LONG).show();
                            alertDialog.dismiss();
                        }
                    }
                }
            });

            ((Button) dialog_layout.findViewById(R.id.my_b_close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        } else if ( preference.getKey().equals("key_recovery") ) {
            FileChooser filechooser = new FileChooser(SettingsActivity.this, "xls");
            filechooser.setFileListener(new FileChooser.FileSelectedListener() {
                @Override
                public void fileSelected(final File file) {
                    boolean isSave = DicUtils.readExcelBackup(getApplicationContext(), db, file);
                    if ( isSave ) {
                        Toast.makeText(getApplicationContext(), "백업 데이타를 정상적으로 가져왔습니다.", Toast.LENGTH_LONG).show();
                    }
                }
            });
            //filechooser.setExtension("xls");
            filechooser.showDialog();
        } else if ( preference.getKey().equals("key_voc_clear") ) {
            new AlertDialog.Builder(this)
                    .setTitle("알림")
                    .setMessage("단어장을 초기화 하시겠습니까?\n초기화 후에는 복구할 수 없습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DicDb.initVocabulary(db);
                            Toast.makeText(getApplicationContext(), "단어장이 초기화 되었습니다.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        } else if ( preference.getKey().equals("key_my_conv_clear") ) {
            new AlertDialog.Builder(this)
                    .setTitle("알림")
                    .setMessage("My 회화를 초기화 하시겠습니까?\n초기화 후에는 복구할 수 없습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DicDb.initMyConversationNote(db);
                            Toast.makeText(getApplicationContext(), "My 회화가 초기화 되었습니다.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        } else if ( preference.getKey().equals("key_conv_clear") ) {
            new AlertDialog.Builder(this)
                    .setTitle("알림")
                    .setMessage("학습 회화를 초기화 하시겠습니까?\n초기화 후에는 복구할 수 없습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DicDb.initConversationNote(db);
                            Toast.makeText(getApplicationContext(), "학습 회화가 초기화 되었습니다.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        } else if ( preference.getKey().equals("key_today_clear") ) {
            new AlertDialog.Builder(this)
                    .setTitle("알림")
                    .setMessage("오늘의 단어를 초기화 하시겠습니까?\n초기화 후에는 복구할 수 없습니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DicDb.initToday(db);
                            Toast.makeText(getApplicationContext(), "오늘의 단어가 초기화 되었습니다.", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        } else if ( preference.getKey().equals("key_mail") ) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            intent.putExtra(Intent.EXTRA_TEXT, "어플관련 문제점을 적어 주세요.\n빠른 시간 안에 수정을 하겠습니다.\n감사합니다.");
            intent.setData(Uri.parse("mailto:limsm9449@gmail.com"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if ( preference.getKey().equals("key_review") ) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        }

        return false;
    }


    public boolean onPreferenceChange(Preference preference, Object newValue) {
        DicUtils.dicLog("preference : " + preference +", newValue : "+ newValue);

        String value = (String) newValue;
        if ( preference == mFontSize ) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);
            mFontSize.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
        } else if ( preference == mWebViewFontSize ) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);
            mWebViewFontSize.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
        } else if ( preference == mWordView ) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);
            mWordView.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
        }
        return true;
    }


    private void updateSummary(){
        mFontSize.setSummary(mFontSize.getEntry());
        mWebViewFontSize.setSummary(mWebViewFontSize.getEntry());
        mWordView.setSummary(mWordView.getEntry());
    }
}
