package com.sleepingbear.penglish;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;

public class GrammarActivity extends AppCompatActivity implements View.OnClickListener {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private GrammarCursorAdapter adapter;
    private Cursor cursor;
    private FileDownloadTask task;
    private String grammarKind = "G";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        ((RadioButton) findViewById(R.id.my_rb_grammar)).setOnClickListener(this);
        ((RadioButton) findViewById(R.id.my_rb_grammar_study)).setOnClickListener(this);

        checkGrammarVersion();

        DicUtils.setAdView(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
            bundle.putString("SCREEN", CommConstants.screen_grammar);

            Intent intent = new Intent(getApplication(), HelpActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if ( v.getId() == R.id.my_rb_grammar ) {
            grammarKind = "G";
        } else if ( v.getId() == R.id.my_rb_grammar_study ) {
            grammarKind = "S";
        }

        changeListView();
    }

    public void changeListView() {
        if ( "G".equals(grammarKind) ) {
            cursor = db.rawQuery(GrammarQuery.getGrammarCategory(), null);
        } else {
            cursor = db.rawQuery(GrammarQuery.getGrammarStudyCategory(), null);
        }

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "등록된 데이타가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        ListView listView = (ListView) findViewById(R.id.my_lv);
        adapter = new GrammarCursorAdapter(getApplicationContext(), cursor, 0);
        listView.setAdapter(adapter);

        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(itemClickListener);
        listView.setSelection(0);
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur = (Cursor) adapter.getItem(position);

            if ( "G".equals(grammarKind) ) {
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", cur.getString(cur.getColumnIndexOrThrow("TITLE1")));
                bundle.putString("CODE", cur.getString(cur.getColumnIndexOrThrow("CODE")).substring(4, 6));

                Intent intent = new Intent(getApplication(), GrammarViewActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("TITLE", cur.getString(cur.getColumnIndexOrThrow("TITLE1")));
                if ( "".equals(DicUtils.getString(cur.getString(cur.getColumnIndexOrThrow("CODE")))) ) {
                    bundle.putString("CODE", "");
                } else {
                    bundle.putString("CODE", cur.getString(cur.getColumnIndexOrThrow("CODE")).substring(4, 6));
                }

                Intent intent = new Intent(getApplication(), GrammarQuestionActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }

        }
    };


    public void checkGrammarVersion() {
        if (DicUtils.isNetWork(this)) {
            taskKind = "GRAMMAR_VERSION";
            task = new FileDownloadTask();
            task.execute();
        } else {
            changeListView();
        }
    }

    public String taskKind = "";
    public String version = "";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Grammar Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public class FileDownloadTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(GrammarActivity.this);
            pd.setIndeterminate(true);
            pd.setCancelable(false);
            pd.show();
            pd.setContentView(R.layout.custom_progress);

            pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            pd.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                if ("GRAMMAR_VERSION".equals(taskKind)) {
                    version = DicUtils.getUrlText("http://limsm9449data.cafe24.com/english/enGrammar.txt");

                    if (!DicUtils.getPreferences(GrammarActivity.this, "GRAMMAR_VERSION", "-").equals(version)) {
                        // 저장할 파일 생성
                        File appDir = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + CommConstants.folderName);
                        if (!appDir.exists()) {
                            appDir.mkdirs();
                        }
                        File file = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + CommConstants.folderName + "/enGrammar.zip");
                        OutputStream out = new FileOutputStream(file);

                        // 파일 다운로드
                        InputStream inputStream = new URL("http://limsm9449data.cafe24.com/english/enGrammar.zip").openStream();
                        int c = 0;
                        while ((c = inputStream.read()) != -1) {
                            out.write(c);
                        }
                        out.flush();
                        out.close();

                        //압축 해제
                        Decompress d = new Decompress(Environment.getExternalStorageDirectory().getAbsoluteFile() + CommConstants.folderName + "/enGrammar.zip", Environment.getExternalStorageDirectory().getAbsoluteFile() + CommConstants.folderName + "/");
                        d.unzip();

                        // 엑셀 파일을 읽어서  DB에 저장한다.
                        try {
                            db.beginTransaction();

                            // 문법 테이블 초기화
                            GrammarQuery.tableCreate(db);

                            File excelFile = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + CommConstants.folderName + "/enGrammar.xls");
                            FileInputStream myInput = new FileInputStream(excelFile);

                            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
                            HSSFWorkbook workbook = new HSSFWorkbook(myFileSystem);
                            HSSFSheet mySheet = workbook.getSheetAt(0);

                            Iterator<Row> rowIter = mySheet.rowIterator();
                            int seq = 1;
                            while ( rowIter.hasNext() ) {
                                HSSFRow myRow = (HSSFRow) rowIter.next();

                                int idx = 0;
                                String code = DicUtils.getExcelString(myRow.getCell(idx++));
                                String title1 = DicUtils.getExcelString(myRow.getCell(idx++));
                                String title2 = DicUtils.getExcelString(myRow.getCell(idx++));
                                String title3 = DicUtils.getExcelString(myRow.getCell(idx++));
                                String explain = DicUtils.getExcelString(myRow.getCell(idx++));
                                String sample1 = DicUtils.getExcelString(myRow.getCell(idx++));
                                String sample2 = DicUtils.getExcelString(myRow.getCell(idx++));
                                String answer = DicUtils.getExcelString(myRow.getCell(idx++));
                                String kind = DicUtils.getExcelString(myRow.getCell(idx++));
                                String other1 = DicUtils.getExcelString(myRow.getCell(idx++));

                                if (!"".equals(code)) {
                                    GrammarQuery.insGrammar(db, seq, code, title1, title2, title3, explain, sample1, sample2, answer, kind, other1);
                                    seq++;
                                }
                            }

                            db.setTransactionSuccessful();
                            db.endTransaction();

                            DicUtils.setPreferences(GrammarActivity.this, "GRAMMAR_VERSION", version);
                        } catch (Exception e) {
                            DicUtils.dicLog("GRAMMAR_VERSION 에러 = " + e.toString());
                            db.endTransaction();
                        }

                        //임시 파일 삭제
                        //(new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + CommConstants.folderName + "/enGrammar.zip")).delete();
                        //(new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + CommConstants.folderName + "/enGrammar.xls")).delete();
                    }
                }
            } catch (Exception e) {
                DicUtils.dicLog("Download 에러 = " + e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            pd.dismiss();
            task = null;

            if ("GRAMMAR_VERSION".equals(taskKind)) {
                changeListView();
            }

            super.onPostExecute(result);
        }
    }
}

class GrammarCursorAdapter extends CursorAdapter {
    int fontSize = 0;

    public GrammarCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);

        fontSize = Integer.parseInt( DicUtils.getPreferencesValue( context, CommConstants.preferences_font ) );
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.content_grammar_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.my_tv_category)).setText((cursor.getPosition() + 1) + ". " + String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("TITLE1"))));

        //사이즈 설정
        ((TextView) view.findViewById(R.id.my_tv_category)).setTextSize(fontSize);
    }

}


class GrammarQuery {
    public static String tableName = "dic_grammar";

    public static void tableCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);

        StringBuffer createSql = new StringBuffer();
        createSql.append("CREATE TABLE " + tableName);
        createSql.append("(");
        createSql.append("SEQ INTEGER PRIMARY KEY AUTOINCREMENT,");
        createSql.append("CODE TEXT,");
        createSql.append("TITLE1 TEXT,");
        createSql.append("TITLE2 TEXT,");
        createSql.append("TITLE3 TEXT,");
        createSql.append("EXPLAIN TEXT,");
        createSql.append("SAMPLE1 TEXT,");
        createSql.append("SAMPLE2 TEXT,");
        createSql.append("ANSWER TEXT,");
        createSql.append("KIND TEXT,");
        createSql.append("OTHER1 TEXT");
        createSql.append(")");

        db.execSQL(createSql.toString());
    }

    public static void insGrammar(SQLiteDatabase db, int seq, String code, String title1, String  title2, String title3, String explain, String sample1, String sample2, String answer, String kind, String other1) {
        StringBuffer sql = new StringBuffer();

        sql.append("INSERT INTO " + tableName + "( SEQ, CODE, TITLE1, TITLE2, TITLE3, EXPLAIN, SAMPLE1, SAMPLE2, ANSWER, KIND, OTHER1 )" + CommConstants.sqlCR);
        sql.append("VALUES ('" + seq + "','" + DicUtils.getQueryParam(code) + "','" +
                DicUtils.getQueryParam(title1) + "','" +
                DicUtils.getQueryParam(title2) + "','" +
                DicUtils.getQueryParam(title3) + "','" +
                DicUtils.getQueryParam(explain) + "','" +
                DicUtils.getQueryParam(sample1) + "','" +
                DicUtils.getQueryParam(sample2) + "','" +
                DicUtils.getQueryParam(answer) + "','" +
                DicUtils.getQueryParam(kind) + "','" +
                DicUtils.getQueryParam(other1) + "')" + CommConstants.sqlCR);
        //DicUtils.dicSqlLog(sql.toString());

        db.execSQL(sql.toString());
    }

    public static String getGrammarCategory() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT  SEQ _id, CODE, TITLE1" + CommConstants.sqlCR);
        sql.append("FROM    " + tableName + CommConstants.sqlCR);
        sql.append("WHERE   CODE LIKE '0000%'" + CommConstants.sqlCR);
        //DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

    public static String getGrammarStudyCategory() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT  0 _id, '' CODE, '전체 문제 풀기' TITLE1" + CommConstants.sqlCR);
        sql.append("UNION" + CommConstants.sqlCR);
        sql.append("SELECT  SEQ _id, CODE, TITLE1 || ' 문제 풀기' TITLE1" + CommConstants.sqlCR);
        sql.append("FROM    " + tableName + CommConstants.sqlCR);
        sql.append("WHERE   CODE LIKE '0000%'" + CommConstants.sqlCR);
        //DicUtils.dicSqlLog(sql.toString());

        return sql.toString();
    }

}