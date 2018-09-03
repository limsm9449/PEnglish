package com.sleepingbear.penglish;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class PatchActivity extends AppCompatActivity {
    private int fontSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patch);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        StringBuffer patch = new StringBuffer();

        patch.append("* 패치 내역" + CommConstants.sqlCR);
        patch.append("- 회화패턴, 숙어, 네이버 회화, 단어장, Daum 단어장 학습을 위한 Card 학습 기능 추가" + CommConstants.sqlCR);
        patch.append(CommConstants.sqlCR);
        patch.append(CommConstants.sqlCR);
        patch.append("- 몇몇 핸드폰에서 회화학습의 클릭 버튼의 사이즈가 잘 안나오는 문제가 있어서 설정화면을 추가" + CommConstants.sqlCR);
        patch.append("- 미드 자막 학습 기능 추가" + CommConstants.sqlCR);
        patch.append("- 영어 신문 Ver.2 개발" + CommConstants.sqlCR);
        patch.append("- Daum 단어장 기능 개선" + CommConstants.sqlCR);
        patch.append("- 단어장 기능 개선 - db 변경으로 데이타가 삭제될 수 있습니다." + CommConstants.sqlCR);
        patch.append("- 회화패턴, 숙어에 검색 기능 추가" + CommConstants.sqlCR);
        patch.append("- 단어상세에서 백버튼으로 돌아가도록 수정" + CommConstants.sqlCR);
        patch.append("- 환경설정에서 단어상세 화면의 상단에 있는 콤보값을 설정하도록 수정(Naver, Daum, 예제)" + CommConstants.sqlCR);
        patch.append("- 영문 소설 기능 수정 : 페이지 단위로 보도록 수정, 무료 영문소설 사이트 추가" + CommConstants.sqlCR);
        patch.append("- 영문 소설 보는 기능 추가" + CommConstants.sqlCR);
        patch.append("- 사전에서 한글 검색시 단어체크 여부에 상관없이 한글 검색이 되도록 수정" + CommConstants.sqlCR);
        patch.append("- 영한사전, 한영 사전을 한 화면으로 통합" + CommConstants.sqlCR);
        patch.append("- Daum 단어장에 동기화 안되는 문제점 수정" + CommConstants.sqlCR);
        patch.append("- 오늘의 단어 기능 추가" + CommConstants.sqlCR);
        patch.append("- 숙어 모음 및 예제 보기 기능 추가" + CommConstants.sqlCR);
        patch.append("- 2017.05.01 : 영어 학습 어플 통합 개발" + CommConstants.sqlCR);

        ((TextView) this.findViewById(R.id.my_c_patch_tv1)).setText(patch.toString());

        fontSize = Integer.parseInt( DicUtils.getPreferencesValue( this, CommConstants.preferences_font ) );
        ((TextView) this.findViewById(R.id.my_c_patch_tv1)).setTextSize(fontSize);
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
