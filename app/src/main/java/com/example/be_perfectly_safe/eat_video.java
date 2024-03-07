package com.example.be_perfectly_safe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class eat_video extends AppCompatActivity {

    private WebView webView;
    TextView ingredients;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat_video);

        //設定步驟文字
        String Ingredients ;
        Ingredients =
                "1. 台灣鯛去腥完成，洗淨切2cm片狀，加米酒1大匙、鹽1/3小匙醃漬。"+"\n"+
                        "2. 乾鍋小火炒香花椒粒或花椒粉，盛出備用。"+"\n"+
                        "3. 裡加2大匙油中火燒熱，雕魚肉下鍋兩面煎黃，盛出。"+"\n"+
                        "4. 另加1大匙油小火炒香蒜末、蔥白。"+"\n"+
                        "5. 改中大火，大白菜下鍋翻炒軟化出水。"+"\n"+
                        "6. 改中火煮避免大火讓湯汁被燒乾。"+"\n"+
                        "7. 加入花椒、辣椒燒煮。"+"\n"+
                        "8. 白菜軟化，鯛魚肉加入煮熟。"+"\n"
        ;

        //綁定元件
        webView = findViewById(R.id.webview);
        ingredients = findViewById(R.id.ingredients);

        //設定文字
        ingredients.setText(Ingredients);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // 啟用JavaScript
        webView.setWebChromeClient(new WebChromeClient()); // 支援播放YouTube影片

        // 載入YouTube影片
        String videoId = "vxxD8MK8ig0"; // 將YOUR_YOUTUBE_VIDEO_ID替換為實際的YouTube影片ID
        String embedUrl = "https://www.youtube.com/embed/" + videoId;
        webView.loadUrl(embedUrl);

    }
}
