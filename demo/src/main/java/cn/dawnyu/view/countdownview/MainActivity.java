package cn.dawnyu.view.countdownview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cn.dawnyu.view.library.CountdownView;
import cn.dawnyu.view.library.Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String timeFormat = "dd天hh小时mm分钟ss秒";
        String date = "2017-12-26 20:00:00";
        CountdownView countdownView = (CountdownView) findViewById(R.id.countdownView);
        countdownView.start(getDate(date).getTime() - System.currentTimeMillis(), timeFormat);
    }

    private Date getDate(String time) {
        SimpleDateFormat df;
        if (!Utils.isNullOrEmpty(time)) {
            if (13 < time.length()) {
                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            } else {
                if (time.contains("/") || time.contains("-")) {
                    df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                } else {
                    df = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
                }
            }
            try {
                return df.parse(time.replace("/", "-"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
