package sugar6400.github.io.so2support;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("Welcome!");
        sliderPage1.setDescription("このアプリは「SOLD OUT 2」の支援アプリだよ！");
        sliderPage1.setImageDrawable(R.drawable.ic_launcher_160);
        sliderPage1.setBgColor(Color.parseColor("#FF65ace4"));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("時給計算");
        sliderPage2.setDescription("「次はどの作業をしようかな～」\n 時給計算機能にお任せ");
        sliderPage2.setImageDrawable(R.drawable.ic_attach_money_white_256dp);
        sliderPage2.setBgColor(Color.parseColor("#ffa979ad"));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("価格はオンラインで取得");
        sliderPage3.setDescription("市場価格を解析して\n最安値や一週間の平均価格で計算できるよ");
        sliderPage3.setImageDrawable(R.drawable.ic_cloud_download_white_256dp);
        sliderPage3.setBgColor(Color.parseColor("#ffd5848b"));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle("Let's Try");
        sliderPage4.setDescription("");
        sliderPage4.setImageDrawable(R.drawable.ic_directions_run_white_192dp);
        sliderPage4.setBgColor(Color.parseColor("#ff70b062"));
        addSlide(AppIntroFragment.newInstance(sliderPage4));

        setDepthAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }
}
