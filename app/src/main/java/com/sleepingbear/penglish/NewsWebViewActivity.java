package com.sleepingbear.penglish;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class NewsWebViewActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnInitListener {
    private TextToSpeech myTTS;

    public SQLiteDatabase mDb;
    public ArrayAdapter urlAdapter;
    private WebView webView;
    private TextView mean;
    private RelativeLayout meanRl;
    private Bundle param;
    private String entryId = "";
    public int mSelect = 0;
    public int m2Select = 0;
    private String clickWord;

    private ImageButton addBtn;
    private ImageButton searchBtn;
    private ImageButton ttsBtn;
    private ImageButton listBtn;
    private ImageButton transBtn;
    private LinearLayout sentenceLl;

    private ActionMode mActionMode = null;

    private final Handler handler = new Handler();

    private ArrayList<NewsVo> enUrls;
    private NewsWebViewActivity.NewsVo currItem;

    private int clickType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_web_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myTTS = new TextToSpeech(this, this);

        // 영자신문 정보
        enUrls = new ArrayList<>();
        enUrls.add(new NewsVo("E027", "Arirang","http://www.arirang.co.kr/News/News_Index.asp"));
        enUrls.add(new NewsVo("E001", "Chosun","http://english.chosun.com/m/"));
        enUrls.add(new NewsVo("E002", "Joongang Daily","http://mengnews.joins.com/"));
        enUrls.add(new NewsVo("E003", "Korea Herald","http://m.koreaherald.com/"));
        enUrls.add(new NewsVo("E004", "The Korea Times","http://m.koreatimes.co.kr/phone/"));
        enUrls.add(new NewsVo("E005", "ABC","http://abcnews.go.com"));
        enUrls.add(new NewsVo("E022", "AudioEnglish","https://www.audioenglish.org/"));
        enUrls.add(new NewsVo("E006", "BBC","http://www.bbc.com/news"));
        enUrls.add(new NewsVo("E038", "Cambridge News","https://www.cambridge-news.co.uk/"));
        enUrls.add(new NewsVo("E031", "CBS News","https://www.cbsnews.com/"));
        enUrls.add(new NewsVo("E028", "Channel News Asia","https://www.channelnewsasia.com/news/international"));
        enUrls.add(new NewsVo("E032", "Chicago Tribune","http://www.chicagotribune.com/"));
        enUrls.add(new NewsVo("E007", "CNN","http://edition.cnn.com"));
        enUrls.add(new NewsVo("E015", "Fast Company","https://www.fastcompany.com/"));
        enUrls.add(new NewsVo("E034", "Guardian","https://www.theguardian.com/international"));
        enUrls.add(new NewsVo("E037", "Herald","http://www.heraldscotland.com/"));
        enUrls.add(new NewsVo("E035", "Independent","http://www.independent.co.uk/"));
        enUrls.add(new NewsVo("E026", "KBS World radio","http://world.kbs.co.kr/english/"));
        enUrls.add(new NewsVo("E008", "Los Angeles Times","http://www.latimes.com"));
        enUrls.add(new NewsVo("E036", "Metro","http://metro.co.uk/"));
        enUrls.add(new NewsVo("E013", "National Geographic","https://www.nationalgeographic.com/"));
        enUrls.add(new NewsVo("E033", "NewYork Post","https://nypost.com/"));
        enUrls.add(new NewsVo("E018", "People","http://people.com/"));
        enUrls.add(new NewsVo("E014", "Reader's digest","https://www.rd.com/magazine/"));
        enUrls.add(new NewsVo("E024", "Repeat after us","http://www.repeatafterus.com/"));
        enUrls.add(new NewsVo("E010", "Reuters","http://mobile.reuters.com/"));
        enUrls.add(new NewsVo("E020", "ShortList","https://www.shortlist.com/"));
        enUrls.add(new NewsVo("E021", "Sunset","https://www.sunset.com/"));
        enUrls.add(new NewsVo("E029", "The Economist","https://www.economist.com/"));
        enUrls.add(new NewsVo("E009", "The New Work Times","http://mobile.nytimes.com/?referer="));
        enUrls.add(new NewsVo("E025", "The Wall Street Journal","https://www.wsj.com/asia"));
        enUrls.add(new NewsVo("E016", "Time","http://time.com/"));
        enUrls.add(new NewsVo("E017", "Time for kids","https://www.timeforkids.com/"));
        enUrls.add(new NewsVo("E030", "USA Today","https://www.usatoday.com/"));
        enUrls.add(new NewsVo("E039", "Sunday People","https://www.mirror.co.uk/all-about/sunday-people"));
        enUrls.add(new NewsVo("E023", "VOA","https://learningenglish.voanews.com/"));
        enUrls.add(new NewsVo("E019", "Vogue","https://www.vogue.com/magazine"));
        enUrls.add(new NewsVo("E011", "Washingtone Post","https://www.washingtonpost.com"));
        enUrls.add(new NewsVo("E012", "ZDNet","http://www.zdnet.com/"));

        String currUrl = "";
        param = getIntent().getExtras();

        for ( int i = 0; i < enUrls.size(); i++ ) {
            DicUtils.dicLog(enUrls.get(i).getKind() + " : " + param.getString("kind"));
            if ( enUrls.get(i).getKind().equals(param.getString("kind")) ) {
                currItem = enUrls.get(i);
                currUrl = currItem.getUrl();
                break;
            }
        }

        if ( !"".equals(DicUtils.getString(param.getString("url"))) ) {
            DicUtils.dicLog("url param");

            /*
            if ( currUrl.indexOf("https") > -1 ) {
                currUrl = "https:\\" + param.getString("url");
            } else {
                currUrl = "http:\\" + param.getString("url");
            }
            */
            currUrl = param.getString("url");
        }

        DicUtils.dicLog(currUrl);

        ActionBar ab = (ActionBar) getSupportActionBar();
        //ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_CUSTOM);
        ab.setTitle(currItem.getName());
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        mDb = (new DbHelper(this)).getWritableDatabase();

        //하단 뜻 영역을 숨김
        meanRl = (RelativeLayout) this.findViewById(R.id.my_c_webview_rl);
        meanRl.setVisibility(View.GONE);
        meanRl.setClickable(true);  //클릭시 하단 광고가 클릭되는 문제로 rl이 클릭이 되게 해준다.

        //버튼 설정
        addBtn = (ImageButton) this.findViewById(R.id.my_c_webview_ib_add);
        addBtn.setVisibility(View.GONE);
        searchBtn = (ImageButton) this.findViewById(R.id.my_c_webview_ib_search);
        searchBtn.setVisibility(View.GONE);

        sentenceLl = (LinearLayout) this.findViewById(R.id.my_nwv_ll_1);
        sentenceLl.setVisibility(View.GONE);
        ttsBtn = (ImageButton) this.findViewById(R.id.my_c_webview_ib_tts);
        listBtn = (ImageButton) this.findViewById(R.id.my_c_webview_ib_list);
        transBtn = (ImageButton) this.findViewById(R.id.my_c_webview_ib_trans);

        //뜻 롱클릭시 단어 상세 보기
        mean = (TextView) this.findViewById(R.id.my_c_webview_mean);
        mean.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if ( clickType == 0 ) {
                    Intent intent = new Intent(getApplication(), WordViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("entryId", entryId);
                    intent.putExtras(bundle);

                    startActivity(intent);
                }

                return false;
            }
        });

        ((ImageButton) this.findViewById(R.id.my_c_webview_ib_add)).setOnClickListener(this);
        ((ImageButton) this.findViewById(R.id.my_c_webview_ib_search)).setOnClickListener(this);
        ((ImageButton) this.findViewById(R.id.my_c_webview_ib_tts)).setOnClickListener(this);
        ((ImageButton) this.findViewById(R.id.my_c_webview_ib_list)).setOnClickListener(this);
        ((ImageButton) this.findViewById(R.id.my_c_webview_ib_trans)).setOnClickListener(this);

        webView = (WebView) this.findViewById(R.id.my_c_webview_wv);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new NewsWebViewActivity.AndroidBridge(), "android");
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        //webView.setContextClickable(true);
        webView.setWebViewClient(new NewsWebViewActivity.MyWebViewClient());
        webView.loadUrl(currUrl);
        DicUtils.dicLog("First : " + currUrl);

        //registerForContextMenu(webView);

        Toast.makeText(getApplicationContext(), "모르는 단어를 길게 클릭하시면 하단에 단어의 뜻이나 문장을 볼 수 있습니다.", Toast.LENGTH_LONG).show();

        DicUtils.setAdView(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 상단 메뉴 구성
        getMenuInflater().inflate(R.menu.menu_news_view, menu);

        MenuItem item = menu.findItem(R.id.action_click_type);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.clickType, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                clickType = parent.getSelectedItemPosition();

                meanRl.setVisibility(View.GONE);

                webView.reload();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spinner.setSelection(0);

        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_web_dictionary) {
            Bundle bundle = new Bundle();

            Intent webIntent = new Intent(getApplication(), WebDictionaryActivity.class);
            webIntent.putExtras(bundle);
            startActivity(webIntent);
        } else if (id == R.id.action_help) {
            Bundle bundle = new Bundle();
            bundle.putString("SCREEN", CommConstants.screen_newsView);

            Intent intent = new Intent(getApplication(), HelpActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    myTTS.shutdown();

                    if ( webView.canGoBack() ) {
                        meanRl.setVisibility(View.GONE);
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
    private void getUrlSource(final String site)  {
        DicUtils.dicLog(site);

        new Thread(new Runnable() {
            public void run() {
                StringBuilder a = new StringBuilder();
                try {
                    //GNU Public, from ZunoZap Web Browser
                    URL url = new URL(site);
                    URLConnection urlc = url.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            urlc.getInputStream(), "UTF-8"));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        a.append(inputLine);
                        DicUtils.dicLog(inputLine);
                    }
                    in.close();
                } catch ( Exception e ) {
                    DicUtils.dicLog(e.toString());
                }
            }
        }).start();
    }
    */

    @Override
    public void onActionModeStarted(ActionMode mode) {
        DicUtils.dicLog("onActionModeStarted");
        if (mActionMode == null) {
            mActionMode = mode;
            Menu menu = mode.getMenu();

            // Remove the default menu items (select all, copy, paste, search)
            menu.clear();

            if ( clickType == 2 ) {
                // Inflate your own menu items
                mode.getMenuInflater().inflate(R.menu.menu_webview_cm, menu);

                //클릭시 onContextItemSelected를 호출해주도록 이벤트를 걸어준다.
                MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        onContextualMenuItemClicked(item);
                        return true;
                    }
                };
                for (int i = 0, n = menu.size(); i < n; i++) {
                    menu.getItem(i).setOnMenuItemClickListener(listener);
                }
            } else {
                mActionMode.finish();
            }
        }

        super.onActionModeStarted(mode);
    }

    public void onContextualMenuItemClicked(MenuItem item) {
        DicUtils.dicLog("onContextualMenuItemClicked");
        switch (item.getItemId()) {
            case R.id.action_copy:
                webView.loadUrl("javascript:window.android.action('COPY', window.getSelection().toString())");

                break;
            case R.id.action_word_view:
                webView.loadUrl("javascript:window.android.action('WORD', window.getSelection().toString())");

                break;
            case R.id.action_translate:
                webView.loadUrl("javascript:window.android.action('TRANSLATE', window.getSelection().toString())");

                break;
            case R.id.action_word_search:
                webView.loadUrl("javascript:window.android.action('WORD_SEARCH', window.getSelection().toString())");

                break;
            case R.id.action_sentence_view:
                webView.loadUrl("javascript:window.android.action('SENTENCE', window.getSelection().toString())");

                break;
            case R.id.action_tts:
                webView.loadUrl("javascript:window.android.action('TTS', window.getSelection().toString())");

                break;
            default:
                // ...
                break;
        }

        // This will likely always be true, but check it anyway, just in case
        if (mActionMode != null) {
            mActionMode.finish();
        }
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
        webView.stopLoading();
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        DicUtils.dicLog("onActionModeFinished");
        mActionMode = null;

        StringBuffer script = new StringBuffer();

        if ( clickType == 0 ) {
            script.append("var selection = window.getSelection();");
            script.append("if (selection.focusNode) {");
            script.append("    var fullStr = selection.focusNode.nodeValue;");

            //클릭한 곳의 단어의 위치를 찾는다.
            script.append("    var s = 0;");
            script.append("    var e = 0;");
            script.append("    for ( var i = selection.focusOffset - 1; i >= 0; i-- ) {");
            script.append("        if ( fullStr.substring(i, i+1) == \" \" ) {");
            script.append("            s = i;");
            script.append("            break;");
            script.append("        }");
            script.append("    }");
            script.append("    for ( var i = selection.focusOffset; i < fullStr.length; i++ ) {");
            script.append("        if ( fullStr.substring(i, i+1) == \" \" ) {");
            script.append("            e = i;");
            script.append("            break;");
            script.append("        }");
            script.append("    }");
            //다시 클릭시 단어만 나와서 마지막 길이를 구해줌.
            script.append("    if ( s == 0 && e == 0 ) {");
            script.append("        e = selection.focusNode.length;");
            script.append("    }");

            script.append("    var returnStr = fullStr.substring(s, e);");

            //단어를 선택한다.
            script.append("    var rangeToSelect = document.createRange();");
            script.append("    rangeToSelect.setStart(selection.focusNode, s);");
            script.append("    rangeToSelect.setEnd(selection.focusNode, e);");
            script.append("    selection.removeAllRanges();");
            script.append("    selection.addRange(rangeToSelect);");

            //글자색 변경
            script.append("    var tr = selection.getRangeAt(0);");
            script.append("    var span = document.createElement(\"span\");");
            script.append("    span.style.cssText = \"color:#ff0000\";");
            script.append("    tr.surroundContents(span);");
            //선택 해제
            script.append("    selection.removeAllRanges();");

            script.append("    window.android.action('SEL', returnStr);");
            script.append("}");

            webView.loadUrl("javascript:" + script.toString());
        } else if ( clickType == 1 ) {
            script.append("var selection = window.getSelection();");
            script.append("if (selection.focusNode) {");
            script.append("    var returnStr = selection.focusNode.nodeValue;");

            //클릭한 곳의 전체 문장을 선택
            script.append("    var rangeToSelect = document.createRange();");
            script.append("    rangeToSelect.selectNode(selection.focusNode);");
            script.append("    selection.removeAllRanges();");
            script.append("    selection.addRange(rangeToSelect);");

            //글자색 변경
            script.append("    var tr = selection.getRangeAt(0);");
            script.append("    var span = document.createElement(\"span\");");
            script.append("    span.style.cssText = \"color:#ff0000\";");
            script.append("    tr.surroundContents(span);");
            //선택 해제
            script.append("    selection.removeAllRanges();");

            script.append("    window.android.action('SEL', returnStr);");
            script.append("}");

            webView.loadUrl("javascript:" + script.toString());
        }

        super.onActionModeFinished(mode);
    }

    @Override
    public void onClick(View v) {
        if ( v.getId() == R.id.my_c_webview_ib_add ) {
            //메뉴 선택 다이얼로그 생성
            Cursor cursor = mDb.rawQuery(DicQuery.getSentenceViewContextMenu(), null);
            final String[] kindCodes = new String[cursor.getCount()];
            final String[] kindCodeNames = new String[cursor.getCount()];

            int idx = 0;
            while (cursor.moveToNext()) {
                kindCodes[idx] = cursor.getString(cursor.getColumnIndexOrThrow("KIND"));
                kindCodeNames[idx] = cursor.getString(cursor.getColumnIndexOrThrow("KIND_NAME"));
                idx++;
            }
            cursor.close();

            final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
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
                    DicDb.insDicVoc(mDb, entryId, kindCodes[mSelect]);

                    DicUtils.setDbChange(getApplicationContext());  //DB 변경 체크

                    Toast.makeText(getApplicationContext(), "단어장에 등록했습니다. 메인화면의 '단어장' 탭에서 내용을 확인하세요.", Toast.LENGTH_SHORT).show();
                }
            });
            dlg.show();
        } else if ( v.getId() == R.id.my_c_webview_ib_search ) {
            wordSearch();
        } else if ( v.getId() == R.id.my_c_webview_ib_tts ) {
            myTTS.speak(clickWord, TextToSpeech.QUEUE_FLUSH, null);
        } else if ( v.getId() == R.id.my_c_webview_ib_list ) {
            Intent intent = new Intent(getApplication(), SentenceViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("foreign", clickWord);
            bundle.putString("han", "");
            bundle.putString("sampleSeq", "");
            bundle.putString("onlyWordList", "Y");
            intent.putExtras(bundle);

            startActivity(intent);
        } else if ( v.getId() == R.id.my_c_webview_ib_trans ) {
            final String[] kindCodes = new String[]{"Naver","Google"};

            final AlertDialog.Builder dlg = new AlertDialog.Builder(NewsWebViewActivity.this);
            dlg.setTitle("번역 사이트 선택");
            dlg.setSingleChoiceItems(kindCodes, m2Select, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    m2Select = arg1;
                }
            });
            dlg.setNegativeButton("취소", null);
            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //클립보드에 복사
                /*
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("simple text", clickWord);
                clipboard.setPrimaryClip(clip);
*/
                    Bundle bundle = new Bundle();
                    bundle.putString("site", kindCodes[m2Select]);
                    bundle.putString("sentence", clickWord);

                    Intent intent = new Intent(getApplication(), WebTranslateActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            dlg.show();
        }
    }

    public void wordSearch() {
        final String[] kindCodes = new String[]{"Naver","Daum"};

        final AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("검색 사이트 선택");
        dlg.setSingleChoiceItems(kindCodes, m2Select, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                m2Select = arg1;
            }
        });
        dlg.setNegativeButton("취소", null);
        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle bundle = new Bundle();
                bundle.putString("kind", CommConstants.dictionaryKind_f);
                bundle.putString("site", kindCodes[m2Select]);
                bundle.putString("word", clickWord);

                Intent intent = new Intent(getApplication(), WebDictionaryActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        dlg.show();
    }

    private class NewsVo {
        private String kind;
        private String name;
        private String url;

        public NewsVo(String kind, String name, String url) {
            this.kind = kind;
            this.name = name;
            this.url = url;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //return super.shouldOverrideUrlLoading(view, url);

            //The New Work Times 에서 다음 url을 호출할때 화면이 안나오는 문제가 있음
            if ( "data:text/html,".equals(url) ) {
                return false;
            } else {
                DicUtils.dicLog("url = " + url);
                view.loadUrl(url);

                return true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            DicUtils.dicLog("onPageStarted : " + url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);


            DicUtils.dicLog("onReceivedError : " + error.toString());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            DicUtils.dicLog("onPageFinished : " + url);
        }
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setWord(final String arg) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    meanRl.setVisibility(View.VISIBLE);

                    clickWord = arg;

                    HashMap info = DicDb.getMean(mDb, arg);
                    mean.setText(arg + " " + DicUtils.getString((String)info.get("SPELLING")) + " : " + DicUtils.getString((String)info.get("MEAN")));

                    entryId = DicUtils.getString((String)info.get("ENTRY_ID"));
                    if ( !"".equals(entryId) ) {
                        DicDb.insDicClickWord(mDb, entryId, "");

                        DicUtils.setDbChange(getApplicationContext());  //DB 변경 체크

                        addBtn.setVisibility(View.VISIBLE);
                        searchBtn.setVisibility(View.GONE);
                    } else {
                        addBtn.setVisibility(View.GONE);
                        searchBtn.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        @JavascriptInterface
        public void action(final String kind, final String arg) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    DicUtils.dicLog(arg);
                    if ( "SEL".equals(kind) ) {
                        DicUtils.dicLog("SEL : " + arg);
                        meanRl.setVisibility(View.VISIBLE);

                        clickWord = arg.trim();

                        if ( clickType == 0 ) {
                            HashMap info = DicDb.getMean(mDb, clickWord.replaceAll("[\"“”.,]",""));
                            mean.setText(clickWord.replaceAll("[\"“”.,]","") + " " + DicUtils.getString((String) info.get("SPELLING")) + " : " + DicUtils.getString((String) info.get("MEAN")));

                            entryId = DicUtils.getString((String) info.get("ENTRY_ID"));
                            if (!"".equals(entryId)) {
                                DicDb.insDicClickWord(mDb, entryId, "");

                                DicUtils.setDbChange(getApplicationContext());  //DB 변경 체크

                                addBtn.setVisibility(View.VISIBLE);
                                searchBtn.setVisibility(View.GONE);
                            } else {
                                addBtn.setVisibility(View.GONE);
                                searchBtn.setVisibility(View.VISIBLE);
                            }
                            sentenceLl.setVisibility(View.GONE);
                        } else {
                            mean.setText(clickWord);
                            sentenceLl.setVisibility(View.VISIBLE);
                            addBtn.setVisibility(View.GONE);
                            searchBtn.setVisibility(View.GONE);
                        }
                    } else if ( "COPY".equals(kind) ) {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("simple text", arg);
                        clipboard.setPrimaryClip(clip);
                    } else if ( "WORD".equals(kind) ) {
                        HashMap info = DicDb.getMean(mDb, arg);

                        if ( info.containsKey("ENTRY_ID") ) {
                            Intent intent = new Intent(getApplication(), WordViewActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("entryId", (String) info.get("ENTRY_ID"));
                            intent.putExtras(bundle);

                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "등록된 단어가 아닙니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else if ( "WORD_SEARCH".equals(kind) ) {
                        clickWord = arg;
                        wordSearch();
                    } else if ( "SENTENCE".equals(kind) ) {
                        Intent intent = new Intent(getApplication(), SentenceViewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("foreign", arg);
                        bundle.putString("han", "");
                        intent.putExtras(bundle);

                        startActivity(intent);
                    } else if ( "TTS".equals(kind) ) {
                        if ( arg.length() > 4000 ) {
                            Toast.makeText(getApplicationContext(), "TTS는 4,000자 까지만 가능합니다.", Toast.LENGTH_SHORT).show();
                            myTTS.speak(arg.substring(0, 3900), TextToSpeech.QUEUE_FLUSH, null);
                        } else {
                            myTTS.speak(arg, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    } else if ( "TRANSLATE".equals(kind) ) {
                        final String[] kindCodes = new String[]{"Naver","Google"};

                        final AlertDialog.Builder dlg = new AlertDialog.Builder(NewsWebViewActivity.this);
                        dlg.setTitle("번역 사이트 선택");
                        dlg.setSingleChoiceItems(kindCodes, m2Select, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                m2Select = arg1;
                            }
                        });
                        dlg.setNegativeButton("취소", null);
                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //클립보드에 복사
                                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("simple text", arg);
                                clipboard.setPrimaryClip(clip);

                                Bundle bundle = new Bundle();
                                bundle.putString("site", kindCodes[m2Select]);
                                bundle.putString("sentence", arg);

                                Intent intent = new Intent(getApplication(), WebTranslateActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                        dlg.show();
                    }
                }
            });
        }
    }
}

//http://stackoverflow.com/questions/6058843/android-how-to-select-texts-from-webview
/*
webView.loadUrl("javascript:window.HybridApp.setMessage(window.getSelection().toString())");
 */

