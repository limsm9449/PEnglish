package com.sleepingbear.penglish;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CardStudyActivity extends AppCompatActivity implements View.OnClickListener {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private int sIdx = 0;
    private Cursor cursor;
    private TextView foreign;
    private TextView han;
    private String kind = "F";
    int fontSize = 0;
    private int const_pattern = 0;
    private int const_idiom = 1;
    private int const_naver = 2;
    private int const_vocabulary = 3;
    private int const_daum_voc1 = 4;
    private int const_daum_voc2 = 5;
    private int const_daum_voc3 = 6;
    private int const_daum_all = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_study);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        fontSize = Integer.parseInt( DicUtils.getPreferencesValue( this, CommConstants.preferences_font ) ) + 3;
        ((TextView) this.findViewById(R.id.my_tv_foreign)).setTextSize(fontSize);
        ((TextView) this.findViewById(R.id.my_tv_han)).setTextSize(fontSize);

        foreign = (TextView) this.findViewById(R.id.my_tv_foreign);
        han = (TextView) this.findViewById(R.id.my_tv_han);

        //((TextView) findViewById(R.id.my_tv_foreign)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.my_rb_foreign)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.my_rb_han)).setOnClickListener(this);

        Spinner spinner = (Spinner) this.findViewById(R.id.my_s_kind);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.randomKind, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                sIdx = parent.getSelectedItemPosition();

                changeListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        spinner.setSelection(0);

        ((TextView) findViewById(R.id.my_tv_foreign)).setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeLongPress() {
                if ( sIdx == const_pattern ) {
                    Bundle bundle = new Bundle();
                    bundle.putString("PATTERN", cursor.getString(cursor.getColumnIndexOrThrow("FOREIGN_TEXT")));
                    bundle.putString("DESC", cursor.getString(cursor.getColumnIndexOrThrow("HAN_TEXT")));
                    bundle.putString("SQL_WHERE", cursor.getString(cursor.getColumnIndexOrThrow("OTHER1")));

                    Intent intent = new Intent(getApplication(), PatternViewActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);
                } else if ( sIdx == const_idiom ) {
                    Bundle bundle = new Bundle();
                    bundle.putString("IDIOM", cursor.getString(cursor.getColumnIndexOrThrow("FOREIGN_TEXT")));
                    bundle.putString("DESC", cursor.getString(cursor.getColumnIndexOrThrow("HAN_TEXT")));
                    bundle.putString("SQL_WHERE", cursor.getString(cursor.getColumnIndexOrThrow("OTHER1")));

                    Intent intent = new Intent(getApplication(), IdiomViewActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);
                } else if ( sIdx == const_naver ) {
                    Bundle bundle = new Bundle();
                    bundle.putString("foreign", cursor.getString(cursor.getColumnIndexOrThrow("FOREIGN_TEXT")));
                    bundle.putString("han", cursor.getString(cursor.getColumnIndexOrThrow("HAN_TEXT")));
                    bundle.putString("sampleSeq", cursor.getString(cursor.getColumnIndexOrThrow("OTHER1")));

                    Intent intent = new Intent(getApplication(), SentenceViewActivity.class);
                    intent.putExtras(bundle);

                    startActivity(intent);
                } else if ( sIdx == const_vocabulary || sIdx == const_daum_voc1 || sIdx == const_daum_voc2 || sIdx == const_daum_voc3 || sIdx == const_daum_all ) {
                    final String entryId = DicDb.getEntryIdForWord(db, cursor.getString(cursor.getColumnIndexOrThrow("FOREIGN_TEXT")));
                    if ( !"".equals(entryId) ) {
                        Intent intent = new Intent(getApplication(), WordViewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("entryId", entryId);
                        intent.putExtras(bundle);

                        startActivity(intent);
                    }
                }
            }
            public void onSwipeDown() {
                setHan();
            }
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                //Toast.makeText(RandomStudyActivity.this, "right", Toast.LENGTH_SHORT).show();
                if ( !cursor.isFirst() ) {
                    cursor.moveToPrevious();
                    setForeign();
                }
            }
            public void onSwipeLeft() {
                //Toast.makeText(RandomStudyActivity.this, "left", Toast.LENGTH_SHORT).show();
                if ( !cursor.isLast() ) {
                    cursor.moveToNext();
                    setForeign();
                }
            }
            public void onSwipeBottom() {
            }
        });
        ((LinearLayout) findViewById(R.id.my_ll__random_study)).setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                //Toast.makeText(RandomStudyActivity.this, "right", Toast.LENGTH_SHORT).show();
                if ( !cursor.isFirst() ) {
                    cursor.moveToPrevious();
                    setForeign();
                }
            }
            public void onSwipeLeft() {
                //Toast.makeText(RandomStudyActivity.this, "left", Toast.LENGTH_SHORT).show();
                if ( !cursor.isLast() ) {
                    cursor.moveToNext();
                    setForeign();
                }
            }
            public void onSwipeBottom() {
            }
        });

        DicUtils.setAdView(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.my_rb_foreign) {
            kind = "F";
            setForeign();
        } else if (v.getId() == R.id.my_rb_han) {
            kind = "H";
            setForeign();
        } else if (v.getId() == R.id.my_tv_foreign) {
            setHan();
        }
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
            finish();
        } else if (id == R.id.action_help) {
            Bundle bundle = new Bundle();
            bundle.putString("SCREEN", CommConstants.screen_cardStudy);

            Intent intent = new Intent(getApplication(), HelpActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeListView() {
        String query = "";
        if ( sIdx == const_pattern ) {
            query = CardStudyQuery.getPattern();
        } else if ( sIdx == const_idiom ) {
            query = CardStudyQuery.getIdiom();
        } else if ( sIdx == const_naver ) {
            query = CardStudyQuery.getNaverConversation();
        } else if ( sIdx == const_vocabulary ) {
            query = CardStudyQuery.getVocabulary();
        } else if ( sIdx == const_daum_voc1 ) {
            query = CardStudyQuery.getDaumVocabulary("R3");
        } else if ( sIdx == const_daum_voc2 ) {
            query = CardStudyQuery.getDaumVocabulary("R2");
        } else if ( sIdx == const_daum_voc3 ) {
            query = CardStudyQuery.getDaumVocabulary("R1");
        } else if ( sIdx == const_daum_all ) {
            query = CardStudyQuery.getDaumVocabulary("");
        }
        DicUtils.dicSqlLog(query);

        cursor = db.rawQuery(query, null);

        foreign.setText("");
        han.setText("");

        if ( cursor.getCount() == 0 ) {
            Toast.makeText(this, "검색된 데이타가 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            cursor.moveToNext();
        }

        setForeign();
    }

    public void setForeign() {
        if ( "F".equals(kind) ) {
            if ( sIdx == const_vocabulary || sIdx == const_daum_voc1 || sIdx == const_daum_voc2 || sIdx == const_daum_voc3 || sIdx == const_daum_all ) {
                foreign.setText(cursor.getString(cursor.getColumnIndexOrThrow("FOREIGN_TEXT")) + " " + cursor.getString(cursor.getColumnIndexOrThrow("OTHER1")));
            } else {
                foreign.setText(cursor.getString(cursor.getColumnIndexOrThrow("FOREIGN_TEXT")));
            }
        } else {
            foreign.setText(cursor.getString(cursor.getColumnIndexOrThrow("HAN_TEXT")));
        }
        han.setText("");
    }

    public void setHan() {
        if ( "F".equals(kind) ) {
            han.setText(cursor.getString(cursor.getColumnIndexOrThrow("HAN_TEXT")));
        } else {
            if ( sIdx == const_vocabulary || sIdx == const_daum_voc1 || sIdx == const_daum_voc2 || sIdx == const_daum_voc3 || sIdx == const_daum_all ) {
                han.setText(cursor.getString(cursor.getColumnIndexOrThrow("FOREIGN_TEXT")) + " " + cursor.getString(cursor.getColumnIndexOrThrow("OTHER1")));
            } else {
                han.setText(cursor.getString(cursor.getColumnIndexOrThrow("FOREIGN_TEXT")));
            }
        }
    }
}

class CardStudyQuery {
    public static String getPattern() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT SEQ _id, PATTERN FOREIGN_TEXT, DESC HAN_TEXT, SQL_WHERE OTHER1, '' OTHER2" + CommConstants.sqlCR);
        sql.append("  FROM DIC_PATTERN" + CommConstants.sqlCR);
        sql.append(" ORDER BY RANDOM()" + CommConstants.sqlCR);

        return sql.toString();
    }

    public static String getIdiom() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT SEQ _id, IDIOM FOREIGN_TEXT, DESC HAN_TEXT, SQL_WHERE OTHER1, SAME_IDIOM OTHER2" + CommConstants.sqlCR);
        sql.append("  FROM DIC_IDIOM" + CommConstants.sqlCR);
        sql.append(" ORDER BY RANDOM()" + CommConstants.sqlCR);

        return sql.toString();
    }

    public static String getNaverConversation() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT _id, SENTENCE1 FOREIGN_TEXT, SENTENCE2 HAN_TEXT, SAMPLE_SEQ OTHER1, '' OTHER2" + CommConstants.sqlCR);
        sql.append("  FROM NAVER_CONVERSATION" + CommConstants.sqlCR);
        sql.append(" ORDER BY RANDOM()" + CommConstants.sqlCR);

        return sql.toString();
    }

    public static String getVocabulary() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT SEQ _id, WORD FOREIGN_TEXT, MEAN HAN_TEXT, SPELLING OTHER1, '' OTHER2" + CommConstants.sqlCR);
        sql.append("  FROM DIC_MY_VOC" + CommConstants.sqlCR);
        sql.append(" ORDER BY RANDOM()" + CommConstants.sqlCR);

        return sql.toString();
    }

    public static String getDaumVocabulary(String category) {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT B.SEQ _id, B.WORD FOREIGN_TEXT, B.MEAN HAN_TEXT, B.SPELLING OTHER1, '' OTHER2" + CommConstants.sqlCR);
        sql.append("  FROM DAUM_VOCABULARY A, DIC B" + CommConstants.sqlCR);
        sql.append(" WHERE A.ENTRY_ID = B.ENTRY_ID" + CommConstants.sqlCR);
        if ( !"".equals(category) ) {
            sql.append("   AND A.CATEGORY_ID LIKE '" + category + "%'" + CommConstants.sqlCR);
        }
        sql.append(" ORDER BY RANDOM()" + CommConstants.sqlCR);

        return sql.toString();
    }
}
