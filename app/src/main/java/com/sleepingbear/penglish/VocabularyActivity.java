package com.sleepingbear.penglish;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class VocabularyActivity extends AppCompatActivity {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private VocabularyCursorAdapter adapter;
    int fontSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View dialog_layout = inflater.inflate(R.layout.dialog_category_add, null);

                //dialog 생성..
                AlertDialog.Builder builder = new AlertDialog.Builder(VocabularyActivity.this);
                builder.setView(dialog_layout);
                final AlertDialog alertDialog = builder.create();

                ((TextView) dialog_layout.findViewById(R.id.my_d_category_add_tv_title)).setText("단어장 추가");
                final EditText et_ins = ((EditText) dialog_layout.findViewById(R.id.my_d_category_add_et_ins));
                ((Button) dialog_layout.findViewById(R.id.my_d_category_add_b_ins)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("".equals(et_ins.getText().toString())) {
                            Toast.makeText(getApplication(), "단어장 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();

                            String insCategoryCode = DicQuery.getInsCategoryCode(db);
                            db.execSQL(DicQuery.getInsNewCategory(CommConstants.vocabularyCode, insCategoryCode, et_ins.getText().toString()));

                            changeListView();

                            DicUtils.setDbChange(getApplicationContext());  //DB 변경 체크

                            Toast.makeText(getApplicationContext(), "단어장에 추가하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                ((Button) dialog_layout.findViewById(R.id.my_d_category_add_b_close)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });

        fontSize = Integer.parseInt( DicUtils.getPreferencesValue( this, CommConstants.preferences_font ) );

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        DicUtils.setAdView(this);

        changeListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 상단 메뉴 구성
        getMenuInflater().inflate(R.menu.menu_help, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_help) {
            Bundle bundle = new Bundle();
            bundle.putString("SCREEN", CommConstants.screen_vocabularyNote);

            Intent intent = new Intent(getApplication(), HelpActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeListView() {
        DicUtils.dicLog(this.getClass().toString() + " changeListView");

        Cursor cursor = db.rawQuery(DicQuery.getMyVocabularyCategoryCount(), null);

        ListView listView = (ListView) findViewById(R.id.my_a_vocabulary_note_lv);
        adapter = new VocabularyCursorAdapter(this, cursor, 0);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(itemClickListener);
        listView.setOnItemLongClickListener(itemLongClickListener);

        listView.setSelection(0);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur = (Cursor) adapter.getItem(position);

            Bundle bundle = new Bundle();
            bundle.putString("kind", cur.getString(cur.getColumnIndexOrThrow("KIND")));
            bundle.putString("kindName", cur.getString(cur.getColumnIndexOrThrow("KIND_NAME")));

            Intent intent = new Intent(getApplication(), VocabularyViewActivity.class);
            intent.putExtras(bundle);

            startActivityForResult(intent, CommConstants.a_vocabulary);
        }
    };

    AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final Cursor cur = (Cursor) adapter.getItem(position);

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            //layout 구성
            final View dialog_layout = inflater.inflate(R.layout.dialog_category_iud, null);

            final String kind = cur.getString(cur.getColumnIndexOrThrow("KIND"));

            //dialog 생성..
            AlertDialog.Builder builder = new AlertDialog.Builder(VocabularyActivity.this);
            builder.setView(dialog_layout);
            final AlertDialog alertDialog = builder.create();

            ((TextView) dialog_layout.findViewById(R.id.my_d_category_tv_category)).setText("단어장 관리");

            final EditText et_upd = ((EditText) dialog_layout.findViewById(R.id.my_et_upd));
            et_upd.setText(cur.getString(cur.getColumnIndexOrThrow("KIND_NAME")));

            ((Button) dialog_layout.findViewById(R.id.my_b_upd)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("VOC0001".equals(kind)) {
                        Toast.makeText(getApplicationContext(), "기본 단어장은 수정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    } else {
                        if ("".equals(et_upd.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "단어장 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();

                            db.execSQL(DicQuery.getUpdCategory(CommConstants.vocabularyCode, kind, et_upd.getText().toString()));
                            DicUtils.setDbChange(getApplicationContext());  //DB 변경 체크

                            changeListView();

                            Toast.makeText(getApplicationContext(), "단어장 이름을 수정하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            ((Button) dialog_layout.findViewById(R.id.my_b_del)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("VOC0001".equals(kind)) {
                        Toast.makeText(getApplicationContext(), "기본 단어장은 삭제할 수 없습니다.", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    } else {
                        new android.support.v7.app.AlertDialog.Builder(VocabularyActivity.this)
                                .setTitle("알림")
                                .setMessage("삭제된 데이타는 복구할 수 없습니다. 삭제하시겠습니까?")
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        alertDialog.dismiss();

                                        DicDb.delCategory(db, CommConstants.vocabularyCode, kind);
                                        DicDb.delMyVocabularyAll(db, kind);

                                        DicUtils.setDbChange(getApplicationContext());  //DB 변경 체크

                                        changeListView();

                                        Toast.makeText(getApplicationContext(), "단어장을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                    }
                }
            });

            final EditText et_saveName = ((EditText) dialog_layout.findViewById(R.id.my_et_voc_name));
            et_saveName.setText(cur.getString(cur.getColumnIndexOrThrow("KIND_NAME")) + ".xls");
            ((Button) dialog_layout.findViewById(R.id.my_b_download)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String saveFileName = et_saveName.getText().toString();
                    if ("".equals(saveFileName)) {
                        Toast.makeText(getApplicationContext(), "저장할 파일명을 입력하세요.", Toast.LENGTH_SHORT).show();
                    } else if (saveFileName.indexOf(".") > -1 && !"xls".equals(saveFileName.substring(saveFileName.length() - 3, saveFileName.length()).toLowerCase())) {
                        Toast.makeText(getApplicationContext(), "확장자는 xls 입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        String fileName = DicUtils.getFileName(saveFileName, "xls");

                        Cursor cursor = db.rawQuery(DicQuery.getSaveMyVocabulary(kind), null);
                        boolean isSave = DicUtils.writeExcelVocabulary(fileName, cursor);
                        if ( isSave ) {
                            Toast.makeText(getApplicationContext(), "단어장을 정상적으로 내보냈습니다.", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                }
            });

            ((Button) dialog_layout.findViewById(R.id.my_b_upload)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileChooser filechooser = new FileChooser(VocabularyActivity.this, "xls");
                    filechooser.setFileListener(new FileChooser.FileSelectedListener() {
                        @Override
                        public void fileSelected(final File file) {
                            boolean isSave = DicUtils.readExcelVocabulary(db, file, kind, false);
                            if ( isSave ) {
                                Toast.makeText(getApplicationContext(), "단어장을 정상적으로 가져왔습니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                changeListView();
                            }
                        }
                    });
                    //filechooser.setExtension("xls");
                    filechooser.showDialog();
                }
            });

            ((Button) dialog_layout.findViewById(R.id.my_b_close)).setOnClickListener(new View.OnClickListener() {
                                                                                          @Override
                                                                                          public void onClick(View v) {
                                                                                              alertDialog.dismiss();
                                                                                          }
                                                                                      }
            );

            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

            return true;
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        DicUtils.dicLog("onActivityResult : " + requestCode + " : " + resultCode);

        switch ( requestCode ) {
            case CommConstants.a_vocabulary :
                changeListView();

                break;
        }
    }
}

class VocabularyCursorAdapter extends CursorAdapter {
    int fontSize = 0;

    public VocabularyCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);

        fontSize = Integer.parseInt( DicUtils.getPreferencesValue( context, CommConstants.preferences_font ) );
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.content_vocabulary_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.my_tv_category)).setText(cursor.getString(cursor.getColumnIndexOrThrow("KIND_NAME")));
        ((TextView) view.findViewById(R.id.my_tv_cnt)).setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("CNT"))) +
                " ( 암기 : " + String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("M_CNT"))) + ", " +
                " 미암기 : " + String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("UM_CNT"))) + " )");

        //사이즈 설정
        ((TextView) view.findViewById(R.id.my_tv_category)).setTextSize(fontSize);
        ((TextView) view.findViewById(R.id.my_tv_cnt)).setTextSize(fontSize);
    }
}