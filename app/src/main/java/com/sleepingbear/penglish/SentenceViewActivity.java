package com.sleepingbear.penglish;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

public class SentenceViewActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {
    private TextToSpeech myTTS;

    public DbHelper dbHelper;
    public SQLiteDatabase db;
    public SentenceViewActivityCursorAdapter adapter;
    public int mSelect = 0;
    public String han;
    public String notHan;
    public String sampleSeq;
    public String onlyWordList;
    public boolean isMySample = false;

    private int fontSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_view);

        myTTS = new TextToSpeech(this, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        ActionBar ab = getSupportActionBar();
        ab.setTitle("문장 상세");
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        Bundle b = getIntent().getExtras();
        notHan = b.getString("foreign").replaceAll("'"," ");
        han = b.getString("han").replaceAll("'"," ");
        sampleSeq = b.getString("sampleSeq");
        onlyWordList = b.getString("onlyWordList");
        if ( "Y".equals(onlyWordList) ) {
            ((RelativeLayout) this.findViewById(R.id.my_rl_1)).setVisibility(View.GONE);
            ab.setTitle("단어");
        }

        fontSize = Integer.parseInt( DicUtils.getPreferencesValue( this, CommConstants.preferences_font ) );

        changeListView();

        ImageButton ib_tts = (ImageButton) findViewById(R.id.my_ib_tts);
        ib_tts.setOnClickListener(this);

        DicUtils.setAdView(this);
    }

    public void changeListView() {
        //문장의 단어를 구한다.
        //String[] splitStr = b.getString("viet").split(CommConstants.splitStr);
        notHan = notHan.replaceAll("", "");
        String[] splitStr = DicUtils.sentenceSplit(notHan);

        Cursor wordCursor = null;
        String word = "";
        String tOneWord = "";
        String oneWord = "";
        for ( int m = 0; m < splitStr.length; m++ ) {
            if ( " ".equals(splitStr[m]) || "".equals(splitStr[m]) ) {
                continue;
            }

            word += DicUtils.getSentenceWord(splitStr, 3, m) + ",";
            // 2 단어
            word += DicUtils.getSentenceWord(splitStr, 2, m) + ",";
            // 1 단어
            tOneWord = DicUtils.getSentenceWord(splitStr, 1, m);
            word += tOneWord + ",";
            oneWord += tOneWord + ",";

            if ( "s".equals(tOneWord.substring(tOneWord.length() - 1)) ) {
                word += tOneWord.substring(0, tOneWord.length() - 1) + ",";
            }
        }

        ((TextView) findViewById(R.id.my_tv_foreign)).setText(notHan);
        ((TextView) findViewById(R.id.my_tv_han)).setText(han);

        ((TextView) findViewById(R.id.my_tv_foreign)).setTextSize(fontSize + 2);
        ((TextView) findViewById(R.id.my_tv_han)).setTextSize(fontSize);

        StringBuffer sql = new StringBuffer();
        if ( "".equals(word) ) {
            sql.append("SELECT DISTINCT SEQ _id, 1 ORD,  WORD, MEAN, ENTRY_ID, SPELLING, (SELECT COUNT(*) FROM DIC_MY_VOC WHERE WORD = A.WORD) MY_VOC FROM DIC A WHERE ENTRY_ID = 'xxxxxxxx'" + CommConstants.sqlCR);
        } else {
            sql.append("SELECT SEQ _id, ORD,  WORD, MEAN, ENTRY_ID, SPELLING, (SELECT COUNT(*) FROM DIC_MY_VOC WHERE WORD = A.WORD) MY_VOC FROM DIC A WHERE KIND = 'F' AND WORD IN ('" + word.substring(0, word.length() -1).toLowerCase().replaceAll(",","','") + "')" + CommConstants.sqlCR);
            sql.append("UNION" + CommConstants.sqlCR);
            sql.append("SELECT SEQ _id, ORD,  WORD, MEAN, ENTRY_ID, SPELLING, (SELECT COUNT(*) FROM DIC_MY_VOC WHERE WORD = A.WORD) MY_VOC FROM DIC A WHERE KIND = 'F' AND WORD IN (SELECT DISTINCT WORD FROM DIC_TENSE WHERE WORD_TENSE IN ('" + oneWord.substring(0, oneWord.length() -1).toLowerCase().replaceAll(",","','") + "'))" + CommConstants.sqlCR);
            sql.append(" ORDER BY ORD" + CommConstants.sqlCR);
        }
        DicUtils.dicSqlLog(sql.toString());
        wordCursor = db.rawQuery(sql.toString(), null);

        ListView dicViewListView = (ListView) this.findViewById(R.id.my_lv);
        adapter = new SentenceViewActivityCursorAdapter(this, wordCursor, 0);
        dicViewListView.setAdapter(adapter);
        dicViewListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        dicViewListView.setOnItemClickListener(itemClickListener);

        dicViewListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cur = (Cursor) adapter.getItem(position);
                cur.moveToPosition(position);

                final String entryId = cur.getString(cur.getColumnIndexOrThrow("ENTRY_ID"));
                final String word = cur.getString(cur.getColumnIndexOrThrow("WORD"));
                final String seq = cur.getString(cur.getColumnIndexOrThrow("_id"));

                //메뉴 선택 다이얼로그 생성
                Cursor cursor = db.rawQuery(DicQuery.getSentenceViewContextMenu(), null);
                final String[] kindCodes = new String[cursor.getCount()];
                final String[] kindCodeNames = new String[cursor.getCount()];

                int idx = 0;
                while ( cursor.moveToNext() ) {
                    kindCodes[idx] = cursor.getString(cursor.getColumnIndexOrThrow("KIND"));
                    kindCodeNames[idx] = cursor.getString(cursor.getColumnIndexOrThrow("KIND_NAME"));
                    idx++;
                }
                cursor.close();

                final AlertDialog.Builder dlg = new AlertDialog.Builder(SentenceViewActivity.this);
                dlg.setTitle("단어장 선택");
                dlg.setSingleChoiceItems(kindCodeNames, mSelect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        mSelect = arg1;
                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DicDb.insMyVocabularyFromDic(db, entryId, kindCodes[mSelect]);
                        DicUtils.setDbChange(getApplicationContext());  //DB 변경 체크

                        adapter.dataChange();
                    }
                });
                dlg.show();

                return true;
            }
        });
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur = (Cursor) adapter.getItem(position);

            Intent intent = new Intent(getApplication(), WordViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("entryId", cur.getString(cur.getColumnIndexOrThrow("ENTRY_ID")));
            bundle.putString("seq", cur.getString(cur.getColumnIndexOrThrow("_id")));
            intent.putExtras(bundle);

            startActivity(intent);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_ib_tts:
                //myTTS.speak(((TextView)this.findViewById(R.id.my_c_wv_tv_spelling)).getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                myTTS.speak(((TextView)this.findViewById(R.id.my_tv_foreign)).getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void onInit(int status) {
        Locale loc = new Locale("en");

        if (status == TextToSpeech.SUCCESS) {
            int result = myTTS.setLanguage(Locale.ENGLISH);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }
        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTTS.shutdown();
    }
}

class SentenceViewActivityCursorAdapter extends CursorAdapter {
    int fontSize = 0;
    private SQLiteDatabase mDb;
    private Cursor mCursor;

    static class ViewHolder {
        protected String word;
        protected String entryId;
        protected ImageButton myvoc;
        protected boolean isMyVoc;
        protected int position;
    }

    public SentenceViewActivityCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
        mCursor = cursor;
        mDb = ((SentenceViewActivity)context).db;
        fontSize = Integer.parseInt( DicUtils.getPreferencesValue( context, CommConstants.preferences_font ) );
    }

    public void dataChange() {
        mCursor.requery();
        mCursor.move(mCursor.getPosition());

        //변경사항을 반영한다.
        notifyDataSetChanged();
    }

    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.content_sentence_view_item, parent, false);

        ViewHolder viewHolder = new ViewHolder();
        viewHolder.myvoc = (ImageButton) view.findViewById(R.id.my_ib_myvoc);
        viewHolder.myvoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewHolder viewHolder = (ViewHolder)v.getTag();

                if ( viewHolder.isMyVoc ) {
                    DicDb.delMyVocabularyInAllCategory(mDb, viewHolder.word);
                    DicUtils.setDbChange(context);  //DB 변경 체크
                } else {
                    DicDb.insMyVocabularyFromDic(mDb, viewHolder.entryId, CommConstants.defaultVocabularyCode);
                    DicUtils.setDbChange(context);  //DB 변경 체크
                }

                dataChange();
            }
        });

        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.entryId = cursor.getString(cursor.getColumnIndexOrThrow("ENTRY_ID"));
        viewHolder.word = cursor.getString(cursor.getColumnIndexOrThrow("WORD"));
        viewHolder.position = cursor.getPosition();
        viewHolder.myvoc.setTag(viewHolder);

        ((TextView) view.findViewById(R.id.my_tv_word)).setText(DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("WORD"))));
        ((TextView) view.findViewById(R.id.my_tv_spelling)).setText(DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("SPELLING"))));
        ((TextView) view.findViewById(R.id.my_tv_mean)).setText(DicUtils.getString(cursor.getString(cursor.getColumnIndexOrThrow("MEAN"))));

        ImageButton ib_myvoc = (ImageButton)view.findViewById(R.id.my_ib_myvoc);
        if ( cursor.getInt(cursor.getColumnIndexOrThrow(CommConstants.vocabularyCode)) > 0 ) {
            ib_myvoc.setImageResource(android.R.drawable.star_on);
            viewHolder.isMyVoc = true;
        } else {
            ib_myvoc.setImageResource(android.R.drawable.star_off);
            viewHolder.isMyVoc = false;
        }

        ((TextView) view.findViewById(R.id.my_tv_word)).setTextSize(fontSize);
        ((TextView) view.findViewById(R.id.my_tv_spelling)).setTextSize(fontSize);
        ((TextView) view.findViewById(R.id.my_tv_mean)).setTextSize(fontSize);
    }
}