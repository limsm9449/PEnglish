package com.sleepingbear.penglish;

import android.app.Activity;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GrammarQuestionActivity extends AppCompatActivity {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private GrammarQuestionCursorAdapter adapter;
    private Cursor cursor;
    private int sCm = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_question);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(this.getIntent().getExtras().getString("TITLE"));

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        changeListView();

        DicUtils.setAdView(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_help) {
            Bundle bundle = new Bundle();
            bundle.putString("SCREEN", CommConstants.screen_grammar_question);

            Intent intent = new Intent(getApplication(), HelpActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeListView() {
        cursor = db.rawQuery(GrammarQuestionQuery.getQuestion(this.getIntent().getExtras().getString("CODE")), null);

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "등록된 데이타가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        ListView listView = (ListView) findViewById(R.id.my_lv);
        adapter = new GrammarQuestionCursorAdapter(getApplicationContext(), cursor, 0, this);
        listView.setAdapter(adapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(itemClickListener);
        listView.setSelection(0);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur = (Cursor) adapter.getItem(position);
        }
    };

}


class GrammarQuestionCursorAdapter extends CursorAdapter {
    int fontSize = 0;
    boolean[] isItemClick;
    boolean[] isMakeAnswer;
    String[] sample1;
    String[] questionAnswer;
    String[] correctAnswer;
    int sCm = 0;
    private Activity mActivity;

    static class ViewHolder {
        protected int position;
        protected String code;
        protected String sample1;
        protected String sample2;
        protected String answer;
    }

    public GrammarQuestionCursorAdapter(Context context, Cursor cursor, int flags, Activity activity) {
        super(context, cursor, 0);

        mActivity = activity;

        //초기화
        isItemClick = new boolean[cursor.getCount()];
        isMakeAnswer = new boolean[cursor.getCount()];
        sample1 = new String[cursor.getCount()];
        questionAnswer = new String[cursor.getCount()];
        correctAnswer = new String[cursor.getCount()];
        for ( int i = 0; i < isItemClick.length; i++ ) {
            isItemClick[i] = false;
            isMakeAnswer[i] = false;
            sample1[i] = "";
            questionAnswer[i] = "";
            correctAnswer[i] = "";
        }

        fontSize = Integer.parseInt( DicUtils.getPreferencesValue( context, CommConstants.preferences_font ) );
    }

    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        View view = null;

        view = LayoutInflater.from(context).inflate(R.layout.content_grammar_question_item, parent, false);

        //answer 기록
        GrammarQuestionCursorAdapter.ViewHolder viewHolder = new GrammarQuestionCursorAdapter.ViewHolder();
        viewHolder.position = -1;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GrammarQuestionCursorAdapter.ViewHolder viewHolder = (GrammarQuestionCursorAdapter.ViewHolder) v.getTag();

                isItemClick[viewHolder.position] = true;

                notifyDataSetChanged();
            }
        });
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                final GrammarQuestionCursorAdapter.ViewHolder viewHolder = (GrammarQuestionCursorAdapter.ViewHolder) v.getTag();

                final android.support.v7.app.AlertDialog.Builder dlg = new android.support.v7.app.AlertDialog.Builder(mActivity);
                dlg.setTitle("메뉴 선택");
                dlg.setSingleChoiceItems(new String[]{"Hint","문장 상세","Naver 번역","Google 번역"}, sCm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        sCm = arg1;
                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if ( sCm == 0 ) {
                            //Hint
                            Bundle bundle = new Bundle();
                            bundle.putString("TITLE", "Hint");

                            if ( "0000".equals(viewHolder.code.substring(2,6)) ) {
                                bundle.putString("CODE", viewHolder.code.substring(0, 2));
                            } else if ( "00".equals(viewHolder.code.substring(4,6)) ) {
                                bundle.putString("CODE", viewHolder.code.substring(0, 4));
                            } else {
                                bundle.putString("CODE", viewHolder.code);
                            }

                            Intent intent = new Intent(mActivity.getApplication(), GrammarViewActivity.class);
                            intent.putExtras(bundle);
                            mActivity.startActivity(intent);
                        } else if ( sCm == 1 ) {
                            //문장 상세
                            Bundle bundle = new Bundle();
                            if ( isItemClick[viewHolder.position] ) {
                                bundle.putString("foreign", viewHolder.sample1.replaceAll("_____", viewHolder.answer.split("\\^")[0]));
                            } else {
                                bundle.putString("foreign", viewHolder.sample1);
                            }
                            bundle.putString("han", viewHolder.sample2);
                            bundle.putString("sampleSeq", "");

                            Intent intent = new Intent(mActivity.getApplication(), SentenceViewActivity.class);
                            intent.putExtras(bundle);
                            mActivity.startActivity(intent);
                        } else {
                            Bundle bundle = new Bundle();
                            if ( isItemClick[viewHolder.position] ) {
                                bundle.putString("sentence", viewHolder.sample1.replaceAll("_____", viewHolder.answer.split("\\^")[0]));
                            } else {
                                bundle.putString("sentence", viewHolder.sample1);
                            }
                            bundle.putString("site", (sCm == 2 ? "Google" : "Naver"));

                            Intent intent = new Intent(mActivity.getApplication(), WebTranslateActivity.class);
                            intent.putExtras(bundle);
                            mActivity.startActivity(intent);
                        }
                    }
                });
                dlg.show();

                return true;
            }
        });
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        GrammarQuestionCursorAdapter.ViewHolder viewHolder = (GrammarQuestionCursorAdapter.ViewHolder) view.getTag();
        viewHolder.position = cursor.getPosition();
        viewHolder.code = DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("CODE")));
        viewHolder.sample1 = DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("SAMPLE1")));
        viewHolder.sample2 = DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("SAMPLE2")));
        viewHolder.answer = DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("ANSWER")));
        sample1[cursor.getPosition()] = (cursor.getPosition() + 1) + ". " + DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("SAMPLE1")));

        if ( isMakeAnswer[cursor.getPosition()] == false ) {
            isMakeAnswer[cursor.getPosition()] = true;

            if ( !"".equals(DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("ANSWER"))))  ) {
                questionAnswer[cursor.getPosition()] = DicUtils.getMakeRandomAnswer(sample1[cursor.getPosition()], DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("ANSWER"))));
                correctAnswer[cursor.getPosition()] = questionAnswer[cursor.getPosition()].replaceAll("_____", " [ " + DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("ANSWER"))).split("\\^")[0] + " ] ");
            }
        }

        ((TextView) view.findViewById(R.id.my_tv_sample1)).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.my_tv_sample2)).setVisibility(View.VISIBLE);

        ((TextView) view.findViewById(R.id.my_tv_sample1)).setText("");
        ((TextView) view.findViewById(R.id.my_tv_sample2)).setText("");

        if ( "".equals(DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("SAMPLE1")))) ) {
            ((TextView) view.findViewById(R.id.my_tv_sample1)).setVisibility(View.GONE);
        } else {
            if ( "".equals(DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("ANSWER")))) ) {
                ((TextView) view.findViewById(R.id.my_tv_sample1)).setText(DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("SAMPLE1"))));
            } else {
                if ( isItemClick[cursor.getPosition()] ) {
                    ((TextView) view.findViewById(R.id.my_tv_sample1)).setText(correctAnswer[cursor.getPosition()]);
                } else {
                    ((TextView) view.findViewById(R.id.my_tv_sample1)).setText(questionAnswer[cursor.getPosition()]);
                }
            }
        }

        if ( "".equals(DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("SAMPLE2")))) ) {
            ((TextView) view.findViewById(R.id.my_tv_sample2)).setVisibility(View.GONE);
        } else {
            ((TextView) view.findViewById(R.id.my_tv_sample2)).setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("SAMPLE2"))));
        }

        //사이즈 설정
        ((TextView) view.findViewById(R.id.my_tv_sample1)).setTextSize(fontSize);
        ((TextView) view.findViewById(R.id.my_tv_sample2)).setTextSize(fontSize);
    }
}


class GrammarQuestionQuery {
    public static String tableName = "dic_grammar";

    public static String getQuestion(String code) {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT  SEQ _id, CODE, TITLE1, TITLE2, TITLE3, EXPLAIN, SAMPLE1, SAMPLE2, ANSWER" + CommConstants.sqlCR);
        sql.append("FROM    " + tableName + CommConstants.sqlCR);
        if ( "".equals(code) ) {
            sql.append("WHERE   ANSWER != ''" + CommConstants.sqlCR);
        } else {
            sql.append("WHERE   CODE LIKE '" + code + "%'" + CommConstants.sqlCR);
            sql.append("AND     ANSWER != ''" + CommConstants.sqlCR);
        }
        sql.append("ORDER   BY RANDOM()    " + CommConstants.sqlCR);
        DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }
}