package com.sleepingbear.penglish;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {
    private int fontSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        StringBuffer allSb = new StringBuffer();
        StringBuffer CurrentSb = new StringBuffer();
        StringBuffer tempSb = new StringBuffer();

        String screen = b.getString("SCREEN");
        if ( screen == null ) {
            screen = "";
        }
        String kind = b.getString("KIND");
        if ( kind == null ) {
            kind = "";
        }

        tempSb.delete(0, tempSb.length());
        if ( kind.equals(CommConstants.dictionaryKind_f) ) {
            tempSb.append("* 영한 사전" + CommConstants.sqlCR);
            tempSb.append("- 영한 사전을 검색합니다." + CommConstants.sqlCR);
            tempSb.append(" .단어를 클릭하시면 단어 상세를 보실 수 있습니다." + CommConstants.sqlCR);
            tempSb.append(" .단어를 길게 클릭하시면 웹사전 검색, 단어장에 추가할 수 있습니다." + CommConstants.sqlCR);
            tempSb.append(" .없는 단어일 경우 하단에 메세지가 나오고, 오른쪽 버튼을 클릭하시면 웹사전으로 검색하실 수 있습니다." + CommConstants.sqlCR);
            tempSb.append(" .오른쪽 단어를 선택하시면 단어만 검색을 합니다." + CommConstants.sqlCR);
            tempSb.append("" + CommConstants.sqlCR);
        } else {
            tempSb.append("* 한영 사전" + CommConstants.sqlCR);
            tempSb.append("- 한영 사전을 검색합니다." + CommConstants.sqlCR);
            tempSb.append("" + CommConstants.sqlCR);
        }
        if ( screen.equals(CommConstants.screen_dictionary) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append("* 영한 사전" + CommConstants.sqlCR);
            allSb.append("- 영한 사전을 검색합니다." + CommConstants.sqlCR);
            allSb.append(" .단어를 클릭하시면 단어 상세를 보실 수 있습니다." + CommConstants.sqlCR);
            allSb.append(" .단어를 길게 클릭하시면 웹사전 검색, 단어장에 추가할 수 있습니다." + CommConstants.sqlCR);
            allSb.append(" .없는 단어일 경우 하단에 메세지가 나오고, 오른쪽 버튼을 클릭하시면 웹사전으로 검색하실 수 있습니다." + CommConstants.sqlCR);
            allSb.append(" .오른쪽 단어를 선택하시면 단어만 검색을 합니다." + CommConstants.sqlCR);
            allSb.append("" + CommConstants.sqlCR);
            allSb.append("* 한영 사전" + CommConstants.sqlCR);
            allSb.append("- 한영 사전을 검색합니다." + CommConstants.sqlCR);
            allSb.append("" + CommConstants.sqlCR);
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 검색 History" + CommConstants.sqlCR);
        tempSb.append("- 영어 사전에서 검색한 단어를 조회합니다." + CommConstants.sqlCR);
        tempSb.append(" .단어를 클릭하시면 영한 사전으로 이동합니다." + CommConstants.sqlCR);
        tempSb.append(" .상단의 편집버튼(연필모양)을 클릭해서 검색 단어를 삭제하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_dictionaryHistory) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* Web 사전" + CommConstants.sqlCR);
        tempSb.append("- Naver, Daum 웹사전으로 검색을 합니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_webDictionary) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* Web 번역" + CommConstants.sqlCR);
        tempSb.append("- Naver, Google 을 사용하여 번역을 합니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_webTranslate) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 영어신문 Ver.1" + CommConstants.sqlCR);
        tempSb.append("- 11개의 영문 뉴스가 있습니다. " + CommConstants.sqlCR);
        tempSb.append(" .국내 영어뉴스는 로딩이 빠르지만 외국 영어뉴스는 로딩이 많이 느립니다. 참고하세요." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_news) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 뉴스 상세" + CommConstants.sqlCR);
        tempSb.append("- 영어뉴스를 보면서 필요한 단어 검색 기능이 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .뉴스를 보다가 모르는 단어를 클릭을 하면 하단에 클릭한 단어의 뜻이 보입니다. " + CommConstants.sqlCR);
        tempSb.append(" .클릭단어의 뜻이 없을경우 하단 오른쪽의 검색 버튼을 클릭하면 Naver,Daum에서 단어 검색을 할 수 있습니다. " + CommConstants.sqlCR);
        tempSb.append(" .하단 단어를 길게 클릭하시면 단어 상세를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .하단 단어 옆의 (+)를 클릭하시면 바로 단어장에 등록을 하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .뉴스의 단어를 길게 클릭하시면 단어보기, 단어검색(Naver,Daum), 번역, 문장보기, TTS, 전체TTS(4000자까지), 복사, 전체복사 기능을 사용하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .뉴스를 보면서 클릭한 단어는 '뉴스 클릭 단어' 화면에서 확인하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_newsView) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 영어신문 Ver.2" + CommConstants.sqlCR);
        tempSb.append(" .오른쪽 하단의 리스트 버튼을 클릭해서 뉴스를 선택하세요. " + CommConstants.sqlCR);
        tempSb.append(" .뉴스를 선택하면 뉴스별 카테고리가 변경됩니다. " + CommConstants.sqlCR);
        tempSb.append(" .카테고리를 선택하면 관련 뉴스를 조회합니다." + CommConstants.sqlCR);
        tempSb.append(" .뉴스를 클릭하시면 뉴스 상세를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .뉴스를 길게 클릭하시면 사이트 기사를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_news2) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 뉴스 상세" + CommConstants.sqlCR);
        tempSb.append("- 뉴스를 보면서 필요한 단어를 검색할 수 있는 기능이 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .뉴스를 보다가 모르는 단어를 클릭을 하면 하단에 클릭한 단어의 뜻이 보입니다. " + CommConstants.sqlCR);
        tempSb.append(" .클릭단어의 뜻이 없을경우 하단 오른쪽의 검색 버튼을 클릭하면 Naver,Daum에서 단어 검색을 할 수 있습니다. " + CommConstants.sqlCR);
        tempSb.append(" .하단 단어를 길게 클릭하시면 단어 상세를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .하단 단어 옆의 (+)를 클릭하시면 바로 단어장에 등록을 하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .뉴스의 단어를 길게 클릭하시면 단어보기, 단어검색(Naver,Daum), 번역, 문장보기, TTS, 전체TTS, 복사, 전체복사 기능을 사용하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .뉴스를 보면서 클릭한 단어는 '뉴스 클릭 단어' 화면에서 확인하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_news2View) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 영어 소설" + CommConstants.sqlCR);
        tempSb.append("- 내가 등록한 영문 소설 리스트 입니다." + CommConstants.sqlCR);
        tempSb.append(" .상단 '연필' 버튼을 클릭해서 리스트를 삭제할 수 있습니다.." + CommConstants.sqlCR);
        tempSb.append(" .하단 '+' 버튼을 클릭해서 웹에 있는 영문 소설을 검색해서 추가하거나 로컬에 있는 문서를 등록 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .리스트에서 소설을 길게 클릭하시면 메인화면에 즐겨찾시 소설로 등록할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_my_novel) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 영어 소설 검색" + CommConstants.sqlCR);
        tempSb.append("- 카테고리 별로 소설 리스트를 볼수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .소설 검색은 '영어 소설 사이트' 화면을 통해서 해당 사이트로 이동후에 검색을 해주세요." + CommConstants.sqlCR);
        tempSb.append(" .상단 콤보에서 제목에 대한 선택 범위를 선택하고, 하단에서 소설을 선택합니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_novel) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 영어 소설 상세" + CommConstants.sqlCR);
        tempSb.append("- 영어 소설을 보면서 필요한 단어를 검색할 수 있는 기능이 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .소설을 보다가 모르는 단어를 클릭을 하면 하단에 클릭한 단어의 뜻이 보입니다. " + CommConstants.sqlCR);
        tempSb.append(" .클릭단어의 뜻이 없을경우 하단 오른쪽의 검색 버튼을 클릭하면 Naver,Daum에서 단어 검색을 할 수 있습니다. " + CommConstants.sqlCR);
        tempSb.append(" .하단 단어를 길게 클릭하시면 단어 상세를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .하단 단어 옆의 (+)를 클릭하시면 바로 단어장에 등록을 하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .소설을 단어를 길게 클릭하시면 단어보기, 단어검색(Naver,Daum), 번역, 문장보기, TTS, 전체TTS(4000자까지), 복사, 전체복사 기능을 사용하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .소설을 보면서 클릭한 단어는 '뉴스 클릭 단어' 화면에서 확인하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_novelView) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 뉴스/소설 클릭 단어" + CommConstants.sqlCR);
        tempSb.append("- 영어 뉴스를 보면서 클릭한 단어들에 대하여 관리하는 화면입니다." + CommConstants.sqlCR);
        tempSb.append(" .상단 수정 버튼(연필모양)를 클릭하시면 단어를 선택, 삭제, 단어장에 저장, 신규 단어장에 저장할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .단어를 클릭하시면 단어상세를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_newsClickWord) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 회화 학습" + CommConstants.sqlCR);
        tempSb.append("- Easy, Normal, hard 별로 회화 학습을 할 수 있습니다. " + CommConstants.sqlCR);
        tempSb.append(" .해석을 보고 단어를 클릭해서 올바른 문장을 만드세요." + CommConstants.sqlCR);
        tempSb.append(" .오른쪽 상단 버튼의 눈 모양 버튼을 클릭하면 영어문장을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .학습한 회화는 '회화 노트' 화면의 '학습 회화'에서 일자별로 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_conversationStudy) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 회화 검색" + CommConstants.sqlCR);
        tempSb.append("- 검색어로 회화를 검색합니다." + CommConstants.sqlCR);
        tempSb.append(" .'A B'로 검색을 하면 A와 B가 들어간 회화를 검색합니다." + CommConstants.sqlCR);
        tempSb.append(" .'A B,C D'로 검색을 하면 A와 B가 들어간 회화와 C와 D가 들어간 회화를 검색합니다." + CommConstants.sqlCR);
        tempSb.append(" .회화를 클릭하면 영문을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .회화를 길게클릭하면 회화 학습, 문장 상세, 회화 노트에 추가, TTS 기능을 사용 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .오른쪽 상단 버튼의 눈 모양 버튼을 클릭하면 영어문장을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_conversation) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 회화 노트" + CommConstants.sqlCR);
        tempSb.append("- MY 회화, 학습 회화로 구성되어 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .'MY 회화'는 단어장 처럼 내 회화를 관리합니다." + CommConstants.sqlCR);
        tempSb.append(" .'학습 회화'는 매일 학습한 회화 내용입니다." + CommConstants.sqlCR);
        tempSb.append(" .노트를 길게 클릭하면 회화 학습, 회화 관리 기능을 사용 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .'회화 관리' 기능을 선택하면 노트 수정, 노트 삭제, 노트 내보내기, 노트 가져오기 기능을 사용 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_conversationNote) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 회화 노트 상세" + CommConstants.sqlCR);
        tempSb.append("- 회화 노트의 회화를 조회합니다." + CommConstants.sqlCR);
        tempSb.append(" .회화를 클릭하면 영문을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .회화를 길게 클릭하면 회화 학습, 문장 상세, 회화 노트에 추가, TTS 기능을 사용 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .오른쪽 상단 버튼의 눈 모양 버튼을 클릭하면 영어문장을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_conversationNoteView) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 회화 패턴" + CommConstants.sqlCR);
        tempSb.append("- 회화 패턴별로 회화를 조회 및 회화 학습을 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .패턴을 클릭하면 패턴이 들어간 회화를 조회 합니다. " + CommConstants.sqlCR);
        tempSb.append(" .패턴을 길게 클릭하면 패턴이 들어간 회화를 학습 할 수 있습니다. " + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_pattern) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 회화 패턴 예제" + CommConstants.sqlCR);
        tempSb.append("- 회화 패턴이 들어간 회화를 조회합니다.(예제에서 비슷한 패턴을 찾기 때문에 100% 정확하지는 않습니다.)" + CommConstants.sqlCR);
        tempSb.append(" .회화를 클릭하면 영문을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .회화를 길게 클릭하면 회화 학습, 문장 상세, 회화 노트에 추가, TTS 기능을 사용 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .오른쪽 상단 버튼의 눈 모양 버튼을 클릭하면 영어 문장을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_patternView) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 미드" + CommConstants.sqlCR);
        tempSb.append("- 미드의 자막으로 학습을 해볼 수 있도록 만들었습니다." + CommConstants.sqlCR);
        tempSb.append("주의사항) 1.다른 분들이 만들어준 자막이기 때문에 오역이 있을 수 있습니다.");
        tempSb.append(" 2.한 문장이 길 경우 초단위로 볼 때 순번이 틀릴수도 있으니 참고하세요." + CommConstants.sqlCR);
        tempSb.append(" .지금은 Friends, 24 미드의 자막만 등록을 했고 차차 다른 자막도 추가를 할 예정입니다." + CommConstants.sqlCR);
        tempSb.append(" .시즌별 드라마를 클릭하시면 자막을 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_caption) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 미드 자막" + CommConstants.sqlCR);
        tempSb.append(" .상단 버튼을 클릭하면 All(한글,영어) / 한글 / 영어 을 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .문장을 클릭하시면 문장상세에서 문장에 등록된 단어들을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_captionView) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 숙어" + CommConstants.sqlCR);
        tempSb.append("- 숙어별로 회화를 조회 및 회화 학습을 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .숙어를 클릭하면 숙어가 들어간 회화를 조회 합니다. " + CommConstants.sqlCR);
        tempSb.append(" .숙어를 길게 클릭하면 숙어가 들어간 회화를 학습 할 수 있습니다. " + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_idiom) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 숙어 예제" + CommConstants.sqlCR);
        tempSb.append("- 숙어가 들어간 회화를 조회합니다.(예제에서 비슷한 숙어를 찾기 때문에 100% 정확하지는 않습니다.)" + CommConstants.sqlCR);
        tempSb.append(" .회화를 클릭하면 영문을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .회화를 길게 클릭하면 회화 학습, 문장 상세, 회화 노트에 추가, TTS 기능을 사용 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .오른쪽 상단 버튼의 눈 모양 버튼을 클릭하면 영어 문장을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_idiomView) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 네이버 회화" + CommConstants.sqlCR);
        tempSb.append("- 네이버 회화를 카테고리를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_naverConversation) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 네이버 회화 상세" + CommConstants.sqlCR);
        tempSb.append("- 네이버 회화를 카테고리 별로 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .클릭하시면 영문을 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .상단 버튼을 클릭하시면 전체 영문을 보시거나, 영문을 숨길 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_naverConversationView) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* Daum 단어장" + CommConstants.sqlCR);
        tempSb.append("- Daum 단어장을 TOEIC,TOEFL,TEPS,수능영어,NEAT/NEPT,초중고영어,회화,기타 카테고리 별로 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .즐겨찾기, 최근수정, 제목 순으로 정렬할 수 있습니다. " + CommConstants.sqlCR);
        tempSb.append(" .상단 refresh 버튼을 클릭하시면 Daum 단어장과 동기화를 합니다." + CommConstants.sqlCR);
        tempSb.append(" .리스트를 길게 클릭하시면 기존 단어장, 신규 단어장에 등록을 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_daumVocabulary) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* Daum 단어장 상세" + CommConstants.sqlCR);
        tempSb.append("- Daum 사이트에 있는 단어장 내용을 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .처음 카테고리를 선택하고 들어갈 경우 Daum 단어장에서 단어를 가져와 보여줍니다." + CommConstants.sqlCR);
        tempSb.append(" .상단 refresh 버튼을 클릭하시면 Daum 단어장과 동기화를 합니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_daumVocabularyView) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 오늘의 단어" + CommConstants.sqlCR);
        tempSb.append("- 매일 랜덤하게 뽑은 10개의 단어를 학습할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_today) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 단어장" + CommConstants.sqlCR);
        tempSb.append("- 단어장 목록을 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .하단의 + 버튼을 클릭해서 신규 단어장을 추가할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .기존 단어장을 길게 클릭하시면 수정, 추가, 삭제,  내보내기, 가져오기를 하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .단어장을 클릭하시면 등록된 단어를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_vocabularyNote) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 단어장 상세" + CommConstants.sqlCR);
        tempSb.append("- 단어 목록 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .하단의 + 버튼을 클릭해서 단어를 6가지 방법으로 추가할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .상단 수정 버튼능 클릭하면 단어장을 편집(삭제,복사,이동) 할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .상단 TTS 버튼을 클릭하면 단어,뜻을 들을 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_vocabularyNoteView) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 단답 학습" + CommConstants.sqlCR);
        tempSb.append(" .단어를 클릭하시면 뜻을 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .별표를 클릭해서 암기여부를 표시합니다." + CommConstants.sqlCR);
        tempSb.append(" .단어를 길게 클릭하시면 단어 보기/전체 정답 보기를 선택하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_study1) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 4지선다 학습" + CommConstants.sqlCR);
        tempSb.append(" .별표를 클릭해서 암기여부를 표시합니다." + CommConstants.sqlCR);
        tempSb.append(" .단어를 길게 클릭하시면 정답 보기/ 단어 보기/전체 정답 보기를 선택하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_study2) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 카드형 학습" + CommConstants.sqlCR);
        tempSb.append("- 카드형 학습입니다." + CommConstants.sqlCR);
        tempSb.append(" .하단 Play 버튼을 클릭하시면 영어를 보여주고 잠시후에 뜻이 보여집니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_study3) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 카드형 OX 학습" + CommConstants.sqlCR);
        tempSb.append("- 카드형 OX 학습입니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_study4) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 카드형 4지선다 학습" + CommConstants.sqlCR);
        tempSb.append("- 카드형 4지선다 학습입니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_study5) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 카드형 4지선다 TTS 학습" + CommConstants.sqlCR);
        tempSb.append("- TTS를 이용하여 학습을 합니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_study6) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 단어 상세" + CommConstants.sqlCR);
        tempSb.append("- 상단 콤보 메뉴를 선택하시면 네이버 사전, 다음 사전, 예제를 보실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_wordView) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* 문장 상세" + CommConstants.sqlCR);
        tempSb.append("- 문장의 발음 및 관련 단어를 조회하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .단어를 클릭하시면 단어 보기 및 등록할 단어장을 선택 하실 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .단어를 길게 클릭하시면 등록할 단어장을 선택할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .별표를 클릭하시면 Default 단어장에 추가 됩니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_sentenceView) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        tempSb.delete(0, tempSb.length());
        tempSb.append("* Card 학습" + CommConstants.sqlCR);
        tempSb.append("- 회화패턴, 숙어, 네이버 회화, 단어장, Daum 단어장을 Card 형식으로 학습할 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .영어/한국어 Radio 버튼 : 영어 기준으로 학습을 할려면 영어를, 한국어 기준으로 학습을 할려면 한국어를 클릭하세요." + CommConstants.sqlCR);
        tempSb.append(" .영어를 클릭하시면 하단에 뜻이 보입니다." + CommConstants.sqlCR);
        tempSb.append(" .영어를 길게 클릭하시면 상세내용을 볼 수 있습니다." + CommConstants.sqlCR);
        tempSb.append(" .오른쪽에서 왼쪽으로 스크롤시 다음 문제가 보입니다. 왼쪽에서 오른쪽으로 스크롤시 이전 문제가 보입니다." + CommConstants.sqlCR);
        tempSb.append("" + CommConstants.sqlCR);
        if ( screen.equals(CommConstants.screen_cardStudy) ) {
            CurrentSb.append(tempSb.toString());
        } else {
            allSb.append(tempSb.toString());
        }

        if ( "ALL".equals(b.getString("SCREEN")) ) {
            ((TextView) this.findViewById(R.id.my_c_help_tv1)).setText(allSb.toString());
        } else {
            ((TextView) this.findViewById(R.id.my_c_help_tv1)).setText(CurrentSb.toString() + CommConstants.sqlCR + CommConstants.sqlCR + allSb.toString());
        }

        fontSize = Integer.parseInt( DicUtils.getPreferencesValue( this, CommConstants.preferences_font ) );
        ((TextView) this.findViewById(R.id.my_c_help_tv1)).setTextSize(fontSize);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
