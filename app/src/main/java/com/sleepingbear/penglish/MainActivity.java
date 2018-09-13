package com.sleepingbear.penglish;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, View.OnLongClickListener {
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    private static final int MY_PERMISSIONS_REQUEST = 0;
    public int mSelect = 0;

    private Button favoritBtn1;
    private Button favoritBtn2;
    private Button favoritBtn3;
    private Button favoritBtn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        System.out.println("=============================================== App Start ======================================================================");
        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        //DB가 새로 생성이 되었으면 이전 데이타를 DB에 넣고 Flag를 N 처리함
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if ( "Y".equals(prefs.getString("db_new", "N")) ) {
            Log.d(CommConstants.tag, "backup data import");

            try {
                File file = getBaseContext().getFileStreamPath(CommConstants.infoFileName);
                if ( file.exists() ) {
                    Log.d(CommConstants.tag, "old data import");
                    //이전걸로 import
                    DicUtils.readInfoFromFile(this, db, "");

                    //기존 VOC는 삭제한다.
                    DicDb.vocToMyVoc(db);

                    //파일 삭제
                    file.delete();
                } else {
                    Log.d(CommConstants.tag, "new data import");
                    DicUtils.readExcelBackup(getApplicationContext(), db, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("db_new", "N");
            editor.commit();
        };

        checkPermission();

        //즐겨찾기 버튼 세팅
        favoritBtn1 = ((Button) findViewById(R.id.my_b_1));
        favoritBtn2 = ((Button) findViewById(R.id.my_b_2));
        favoritBtn3 = ((Button) findViewById(R.id.my_b_3));
        favoritBtn4 = ((Button) findViewById(R.id.my_b_4));

        if ( prefs.getInt("favorites1", -1) != -1 ) {
            favoritBtn1.setTag(prefs.getInt("favorites1", -1));
            favoritBtn1.setText(prefs.getString("favorites1_text", "-"));
        } else {
            favoritBtn1.setTag(-1);
        }
        if ( prefs.getInt("favorites2", -1) != -1 ) {
            favoritBtn2.setTag(prefs.getInt("favorites2", -1));
            favoritBtn2.setText(prefs.getString("favorites2_text", "-"));
        } else {
            favoritBtn2.setTag(-1);
        }
        if ( prefs.getInt("favorites3", -1) != -1 ) {
            favoritBtn3.setTag(prefs.getInt("favorites3", -1));
            favoritBtn3.setText(prefs.getString("favorites3_text", "-"));
        } else {
            favoritBtn3.setTag(-1);
        }
        if ( CommConstants.isFreeApp ) {
            favoritBtn4.setText("최고의 영어학습\n유료 설치");
        } else {
            if (prefs.getInt("favorites4", -1) != -1) {
                favoritBtn4.setTag(prefs.getInt("favorites4", -1));
                favoritBtn4.setText(prefs.getString("favorites4_text", "-"));
            } else {
                favoritBtn4.setTag(-1);
            }
        }

        favoritBtn1.setOnClickListener(this);
        favoritBtn2.setOnClickListener(this);
        favoritBtn3.setOnClickListener(this);
        favoritBtn4.setOnClickListener(this);

        favoritBtn1.setOnLongClickListener(this);
        favoritBtn2.setOnLongClickListener(this);
        favoritBtn3.setOnLongClickListener(this);
        favoritBtn4.setOnLongClickListener(this);

        DicUtils.setAdView(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 상단 메뉴 구성
        //getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.my_b_1:
                if ( (int)favoritBtn1.getTag() == -1 ) {
                    selectFavoriteBtn(favoritBtn1, "favorites1");
                } else {
                    btnSelect((int)favoritBtn1.getTag());
                }
                break;
            case R.id.my_b_2:
                if ( (int)favoritBtn2.getTag() == -1 ) {
                    selectFavoriteBtn(favoritBtn2, "favorites2");
                } else {
                    btnSelect((int)favoritBtn2.getTag());
                }
                break;
            case R.id.my_b_3:
                if ( (int)favoritBtn3.getTag() == -1 ) {
                    selectFavoriteBtn(favoritBtn3, "favorites3");
                } else {
                    btnSelect((int)favoritBtn3.getTag());
                }
                break;
            case R.id.my_b_4:
                if ( CommConstants.isFreeApp ) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.sleepingbear.english")));
                } else {
                    if ((int) favoritBtn4.getTag() == -1) {
                        selectFavoriteBtn(favoritBtn4, "favorites4");
                    } else {
                        btnSelect((int) favoritBtn4.getTag());
                    }
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.my_b_1:
                selectFavoriteBtn(favoritBtn1, "favorites1");
                break;
            case R.id.my_b_2:
                selectFavoriteBtn(favoritBtn2, "favorites2");
                break;
            case R.id.my_b_3:
                selectFavoriteBtn(favoritBtn3, "favorites3");
                break;
            case R.id.my_b_4:
                if ( !CommConstants.isFreeApp ) {
                    selectFavoriteBtn(favoritBtn4, "favorites4");
                }
                break;
        }

        return true;
    }

    public void selectFavoriteBtn(final Button btn, final String favoritesBtn) {
        final int[] kindCodes = new int[21];
        final String[] kindCodeNames = new String[21];

        int idx = 0;
        kindCodes[idx] = R.id.nav_dic;                      kindCodeNames[idx++] = "영어 사전";
        kindCodes[idx] = R.id.nav_search_history;             kindCodeNames[idx++] = "검색 History";
        kindCodes[idx] = R.id.nav_web_voc;              kindCodeNames[idx++] = "Web 사전";
        kindCodes[idx] = R.id.nav_web_trans;             kindCodeNames[idx++] = "Web 번역";
        kindCodes[idx] = R.id.nav_news1;                kindCodeNames[idx++] = "영어 신문 Ver.1";
        kindCodes[idx] = R.id.nav_news2;                kindCodeNames[idx++] = "영어 신문 Ver.2";
        kindCodes[idx] = R.id.nav_novel;             kindCodeNames[idx++] = "영어 소설";
        kindCodes[idx] = R.id.nav_click_word;             kindCodeNames[idx++] = "뉴스/소설 클릭 단어";
        kindCodes[idx] = R.id.nav_conversation;             kindCodeNames[idx++] = "회화 학습";
        kindCodes[idx] = R.id.nav_conv_search;             kindCodeNames[idx++] = "회화 검색";
        kindCodes[idx] = R.id.nav_pattern;             kindCodeNames[idx++] = "회화 패턴";
        kindCodes[idx] = R.id.nav_conv_note;             kindCodeNames[idx++] = "회화 노트";
        kindCodes[idx] = R.id.nav_drama;             kindCodeNames[idx++] = "미드 자막";
        kindCodes[idx] = R.id.nav_grammar;             kindCodeNames[idx++] = "문법";
        kindCodes[idx] = R.id.nav_idiom;             kindCodeNames[idx++] = "숙어";
        kindCodes[idx] = R.id.nav_naver_conv;             kindCodeNames[idx++] = "네이버 회화";
        kindCodes[idx] = R.id.nav_daum;             kindCodeNames[idx++] = "Daum 단어장";
        kindCodes[idx] = R.id.nav_toady;             kindCodeNames[idx++] = "오늘의 단어";
        kindCodes[idx] = R.id.nav_voc;             kindCodeNames[idx++] = "단어장";
        kindCodes[idx] = R.id.nav_voc_study;             kindCodeNames[idx++] = "단어 학습";
        kindCodes[idx] = R.id.nav_card_study;             kindCodeNames[idx++] = "Card 학습";

        final android.support.v7.app.AlertDialog.Builder dlg = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        dlg.setTitle("메뉴 선택");
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
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(favoritesBtn, kindCodes[mSelect]);
                editor.putString(favoritesBtn + "_text", kindCodeNames[mSelect]);
                editor.commit();

                btn.setTag(kindCodes[mSelect]);
                btn.setText(kindCodeNames[mSelect]);

                btnSelect(kindCodes[mSelect]);
            }
        });
        dlg.show();
    }

    public void btnSelect(int id) {
        Bundle bundle = new Bundle();
        if (id == R.id.nav_dic) {
            bundle.putString("KIND", CommConstants.dictionaryKind_f);

            Intent englishIntent = new Intent(getApplication(), DictionaryActivity.class);
            englishIntent.putExtras(bundle);
            startActivity(englishIntent);
        } else if (id == R.id.nav_search_history) {
            Intent dicHistoryIntent = new Intent(getApplication(), DictionaryHistoryActivity.class);
            dicHistoryIntent.putExtras(bundle);
            startActivity(dicHistoryIntent);
        } else if (id == R.id.nav_web_voc) {
            Intent webIntent = new Intent(getApplication(), WebDictionaryActivity.class);
            webIntent.putExtras(bundle);
            startActivity(webIntent);
        } else if (id == R.id.nav_web_trans) {
            Intent webTranslateIntent = new Intent(getApplication(), WebTranslateActivity.class);
            webTranslateIntent.putExtras(bundle);
            startActivity(webTranslateIntent);
        } else if (id == R.id.nav_news1) {
            Intent newsIntent = new Intent(getApplication(), NewsActivity.class);
            newsIntent.putExtras(bundle);
            startActivity(newsIntent);
        } else if (id == R.id.nav_news2) {
            Intent news2Intent = new Intent(getApplication(), News2Activity.class);
            news2Intent.putExtras(bundle);
            startActivity(news2Intent);
        } else if (id == R.id.nav_novel) {
            Intent novelIntent = new Intent(getApplication(), MyNovelActivity.class);
            novelIntent.putExtras(bundle);
            startActivity(novelIntent);
        } else if (id == R.id.nav_click_word) {
            Intent newClickWordIntent = new Intent(getApplication(), NewsClickWordActivity.class);
            newClickWordIntent.putExtras(bundle);
            startActivity(newClickWordIntent);
        } else if (id == R.id.nav_conversation) {
            startActivity(new Intent(getApplication(), ConversationStudyActivity.class));
        } else if (id == R.id.nav_conv_search) {
            startActivity(new Intent(getApplication(), ConversationActivity.class));
        } else if (id == R.id.nav_pattern) {
            startActivity(new Intent(getApplication(), PatternActivity.class));
        } else if (id == R.id.nav_conv_note) {
            startActivity(new Intent(getApplication(), ConversationNoteActivity.class));
        } else if (id == R.id.nav_drama) {
            startActivity(new Intent(getApplication(), CaptionActivity.class));
        } else if (id == R.id.nav_grammar) {
            startActivity(new Intent(getApplication(), GrammarActivity.class));
        } else if (id == R.id.nav_idiom) {
            startActivity(new Intent(getApplication(), IdiomActivity.class));
        } else if (id == R.id.nav_naver_conv) {
            startActivity(new Intent(getApplication(), NaverConversationActivity.class));
        } else if (id == R.id.nav_daum) {
            startActivity(new Intent(getApplication(), DaumVocabularyActivity.class));
        } else if (id == R.id.nav_toady) {
            startActivity(new Intent(getApplication(), TodayActivity.class));
        } else if (id == R.id.nav_voc) {
            startActivity(new Intent(getApplication(), VocabularyActivity.class));
        } else if (id == R.id.nav_voc_study) {
            startActivity(new Intent(getApplication(), StudyActivity.class));
        } else if (id == R.id.nav_card_study) {
            startActivity(new Intent(getApplication(), CardStudyActivity.class));
        } else if (id == R.id.nav_patch) {
            startActivity(new Intent(getApplication(), PatchActivity.class));
        } else if (id == R.id.nav_help) {
            Intent helpIntent = new Intent(getApplication(), HelpActivity.class);
            helpIntent.putExtras(bundle);
            startActivity(helpIntent);
        } else if (id == R.id.nav_setting) {
            startActivityForResult(new Intent(getApplication(), SettingsActivity.class), CommConstants.a_setting);
        } else if (id == R.id.nav_share) {
            Intent msg = new Intent(Intent.ACTION_SEND);
            msg.addCategory(Intent.CATEGORY_DEFAULT);
            msg.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            msg.putExtra(Intent.EXTRA_TEXT, "영어.. 참 어렵죠? '최고의 영어학습' 어플을 사용해 보세요. https://play.google.com/store/apps/details?id=com.sleepingbear.penglish");
            msg.setType("text/plain");
            startActivity(Intent.createChooser(msg, "어플 공유"));
        } else if (id == R.id.nav_review) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } else if (id == R.id.nav_mail) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            intent.putExtra(Intent.EXTRA_TEXT, "어플관련 문제점을 적어 주세요.\n빠른 시간 안에 수정을 하겠습니다.\n감사합니다.");
            intent.setData(Uri.parse("mailto:limsm9449@gmail.com"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        btnSelect(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean checkPermission() {
        Log.d(CommConstants.tag, "checkPermission");
        boolean isCheck = false;
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ) {
            Log.d(CommConstants.tag, "권한 없음");
            if ( ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
                //Toast.makeText(this, "(중요)파일로 내보내기, 가져오기를 하기 위해서 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
            Log.d(CommConstants.tag, "2222");
        } else {
            Log.d(CommConstants.tag, "권한 있음");
            isCheck = true;
        }

        return isCheck;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(CommConstants.tag, "권한 허가");
                } else {
                    Log.d(CommConstants.tag, "권한 거부");
                    Toast.makeText(this, "파일 권한이 없기 때문에 파일 내보내기, 가져오기를 할 수 없습니다.\n만일 권한 팝업이 안열리면 '다시 묻지 않기'를 선택하셨기 때문입니다.\n어플을 지우고 다시 설치하셔야 합니다.", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }

    private long backKeyPressedTime = 0;
    @Override
    public void onBackPressed() {
        //종료 시점에 변경 사항을 기록한다.
        if ( "Y".equals(DicUtils.getDbChange(getApplicationContext())) ) {
            DicUtils.writeExcelBackup(this, db, "");
            DicUtils.clearDbChange(this);
        }

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();

            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
        }
    }
}
