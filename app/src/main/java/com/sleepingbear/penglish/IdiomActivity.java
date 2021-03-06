package com.sleepingbear.penglish;

import android.app.AlertDialog;
import android.content.Context;
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

public class IdiomActivity extends AppCompatActivity {

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private IdiomCursorAdapter adapter;

    private Cursor cursor;
    private String search = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idiom);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        changeListView();

        DicUtils.setAdView(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 상단 메뉴 구성
        getMenuInflater().inflate(R.menu.menu_search, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_search) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            final View dialog_layout = inflater.inflate(R.layout.dialog_search, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(IdiomActivity.this);
            builder.setView(dialog_layout);
            final AlertDialog alertDialog = builder.create();

            final EditText et_search = ((EditText) dialog_layout.findViewById(R.id.my_et_search));
            et_search.setText(search);
            ((Button) dialog_layout.findViewById(R.id.my_b_search)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    search = et_search.getText().toString();
                    changeListView();
                    alertDialog.dismiss();
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
        } else if (id == R.id.action_help) {
            Bundle bundle = new Bundle();
            bundle.putString("SCREEN", CommConstants.screen_idiom);

            Intent intent = new Intent(getApplication(), HelpActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeListView() {
        cursor = db.rawQuery(DicQuery.getIdiomList(search), null);

        if ( cursor.getCount() == 0 ) {
            Toast.makeText(this, "검색된 데이타가 없습니다.", Toast.LENGTH_SHORT).show();
        }

        ListView listView = (ListView) findViewById(R.id.my_lv);
        adapter = new IdiomCursorAdapter(getApplicationContext(), cursor, 0);
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
            bundle.putString("IDIOM", cur.getString(cur.getColumnIndexOrThrow("IDIOM")));
            bundle.putString("DESC", cur.getString(cur.getColumnIndexOrThrow("DESC")));
            bundle.putString("SQL_WHERE", cur.getString(cur.getColumnIndexOrThrow("SQL_WHERE")));

            Intent intent = new Intent(getApplication(), IdiomViewActivity.class);
            intent.putExtras(bundle);

            startActivity(intent);
        }
    };

    AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur = (Cursor) adapter.getItem(position);

            Bundle bundle = new Bundle();
            bundle.putString("kind", "IDIOM");
            bundle.putString("sqlWhere", cur.getString(cur.getColumnIndexOrThrow("SQL_WHERE")));

            Intent intent = new Intent(getApplicationContext(), ConversationNoteStudyActivity.class);
            intent.putExtras(bundle);

            startActivity(intent);

            return true;
        }
    };
}

class IdiomCursorAdapter extends CursorAdapter {
    int fontSize = 0;

    public IdiomCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);

        fontSize = Integer.parseInt( DicUtils.getPreferencesValue( context, CommConstants.preferences_font ) );
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.content_idiom_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.my_tv_idiom)).setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("IDIOM"))));
        ((TextView) view.findViewById(R.id.my_tv_same_idiom)).setText(" = " + String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("SAME_IDIOM"))));
        ((TextView) view.findViewById(R.id.my_tv_idiom_desc)).setText(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("DESC"))));

        //사이즈 설정
        ((TextView) view.findViewById(R.id.my_tv_idiom)).setTextSize(fontSize);
        ((TextView) view.findViewById(R.id.my_tv_same_idiom)).setTextSize(fontSize);
        ((TextView) view.findViewById(R.id.my_tv_idiom_desc)).setTextSize(fontSize);

        if ( "".equals(String.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("SAME_IDIOM")))) ) {
            ((TextView) view.findViewById(R.id.my_tv_same_idiom)).setVisibility(View.GONE);
        }
    }

}