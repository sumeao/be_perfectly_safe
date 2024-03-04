package com.example.be_perfectly_safe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class chatGPT extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView welcomeView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    MessageAdapter messageAdapter;
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    String go = "我生於50年代，有點懷念當時台中的景象";
    String ans1 = "那麼，您出生在50年代，一定有許多寶貴的回憶關於當時的台中。\n" +
            "在那個時代，台中也經歷了很多變化。\n" +
            "或許您可以分享一些您懷念的景象或回憶，讓我們一同回味一下過去的美好時光。";

    String ans2 = "您說的是位於台中火車站附近的第二市場內的'鹿港扁食'嗎?\n" +
            "他是一間有超過50年歷史的老店呢!他的鮮蝦扁食裡的蝦子是老闆每天親手現剝的喔!\n"+
            "除了這個您還有懷念甚麼食物呢?";

    String wait = "輸入中...";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_gpt);

        messageList = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        welcomeView = findViewById(R.id.welcome_text);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_btn);

        //setup recycler view
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setStackFromEnd(true);
        recyclerView.setLayoutManager(llm);

        addToChat(go,Message.SENT_BY_ME);
        addToChat(ans1,Message.SENT_BY_BOT);

        sendButton.setOnClickListener((v)->{
            String question = messageEditText.getText().toString().trim();
            addToChat(question,Message.SENT_BY_ME);
            messageEditText.setText("");

            addToChat(wait,Message.SENT_BY_BOT);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageList.remove(messageList.size()-1);
                            addToChat(ans2,Message.SENT_BY_BOT);
                        }
                    });
                }
            }).start();

//            callAPI(question);
        });
    }

    void addToChat(String message,String sentBy){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                messageList.add(new Message(message,sentBy));
                messageAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
            }
        });
    }

//    void addResponse(String response){
//        messageList.remove(messageList.size()-1);
//        addToChat(response,Message.SENT_BY_BOT);
//    }

//    void callAPI(String question){
//        //okhttp
//        messageList.add(new Message("Typing... ",Message.SENT_BY_BOT));
//
//        JSONObject jsonBody = new JSONObject();
//        try {
//            jsonBody.put("model","text-davinci-003");
//            jsonBody.put("prompt",question);
//            jsonBody.put("max_tokens",4000);
//            jsonBody.put("temperature",0);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        RequestBody body = RequestBody.create(jsonBody.toString(),JSON);
//        Request request = new Request.Builder()
//                .url("https://api.openai.com/v1/completions")
//                .header("Authorization","Bearer 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")
//                .post(body)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                addResponse("Failed to load response due to "+e.getMessage());
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if(response.isSuccessful()){
//                    JSONObject  jsonObject = null;
//                    try {
//                        jsonObject = new JSONObject(response.body().string());
//                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
//                        String result = jsonArray.getJSONObject(0).getString("text");
//                        addResponse(result.trim());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }else{
//                    addResponse("Failed to load response due to "+response.body().string());
//                }
//            }
//        });



//    }
}