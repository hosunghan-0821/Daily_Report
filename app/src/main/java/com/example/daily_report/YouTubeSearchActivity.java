package com.example.daily_report;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class YouTubeSearchActivity extends AppCompatActivity {

    static final String API_KEY="AIzaSyCpxh6a6mhKdQyWUH230LqG4dK5hsgYP0A";
    private EditText searchText;
    private TextView searchButton;
    private RecyclerView youtubeRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private YouTubeAdapter adapter;
    private ArrayList<YouTubeContent> youTubeContentArrayList;
    private SetYouTubeOnclickListener listener;
    private String videoId="";
    private AsyncTask<?,?,?> searchTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_tube_search);

        searchText=findViewById(R.id.search_keyword);
        searchButton=findViewById(R.id.search_text);

        youtubeRecyclerView=findViewById(R.id.youtube_recyclerview);
        linearLayoutManager=new LinearLayoutManager(YouTubeSearchActivity.this);
        youTubeContentArrayList=new ArrayList<>();

        youtubeRecyclerView.setLayoutManager(linearLayoutManager);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchText.getText().toString().equals("")){
                    Toast.makeText(YouTubeSearchActivity.this, "검색어를 제대로 입력하세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    searchTask = new SearchTask().execute();
                    //검색 시작 후, 검색 한 데이터를 이용하여 recyclerView 만들어내기.

                }
            }
        });

        listener = new SetYouTubeOnclickListener() {


            @Override
            public void setLongClick(int position) {
                AlertDialog.Builder builder =new AlertDialog.Builder(YouTubeSearchActivity.this);
                builder.setTitle("이 영상을 저장하시겠습니까?");
                builder.setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //여기서 저장하는 코드.
                        ArrayList<YouTubeContent> arrayList = MySharedPreference.getYoutubeArrayList(YouTubeSearchActivity.this,MainActivity.dateControl);
                        arrayList.add(youTubeContentArrayList.get(position));
                        MySharedPreference.setYoutubeArrayList(YouTubeSearchActivity.this,MainActivity.dateControl,arrayList);

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                builder.show();
            }

            @Override
            public void setOnClick(int position) {
                Intent intent = new Intent(YouTubeSearchActivity.this,YouTubePlayActivity.class);

                String videoId= youTubeContentArrayList.get(position).getVideoId();
                intent.putExtra("youtubeVideoId",videoId);
                startActivity(intent);
                //아이템 클릭하면 뭐할지 정의하기.
            }
        };

    }

    private class SearchTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                parsingJsonData(getYoutube());
            }catch(IOException| JSONException e){
                e.printStackTrace();
            }

            return null;

        }


        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            adapter= new YouTubeAdapter(youTubeContentArrayList);
            adapter.setYoutubeOnclickListener(listener);
            youtubeRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    public JSONObject getYoutube() throws IOException{


        String originUrl = "https://www.googleapis.com/youtube/v3/search?"
                + "part=snippet&q=" + searchText.getText().toString()
                + "&key="+ API_KEY+"&maxResults=10";

        String myUrl = String.format(originUrl);
        URL url = new URL(myUrl);

        //여기서 부터 통신 시작 의미 하나하나 무엇인지 알아보자

        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.connect();

        String line;
        String result="";
        InputStream inputStream=connection.getInputStream();
        BufferedReader reader =new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer response = new StringBuffer();

        while((line = reader.readLine())!=null){
            response.append(line);
        }
        result = response.toString();

        JSONObject jsonObject= new JSONObject();
        try{
            jsonObject=new JSONObject(result);
        }catch (JSONException e){
            e.printStackTrace();
        }

        return jsonObject;
    }

    private void parsingJsonData(JSONObject jsonObject) throws JSONException,IOException{

        youTubeContentArrayList.clear();
        JSONArray contacts = jsonObject.getJSONArray("items");

        for(int i=0;i<contacts.length();i++){
            JSONObject c = contacts.getJSONObject(i);
            String kind = c.getJSONObject("id").getString("kind");

            if(kind.equals("youtube#video")){
                videoId= c.getJSONObject("id").getString("videoId");
            }
            else{
                videoId=c.getJSONObject("id").getString("playlistId");
            }
            String title = c.getJSONObject("snippet").getString("title");
            String changedTitle = stringToHtmlSign(title);
            String channelTitle = c.getJSONObject("snippet").getString("channelTitle");
            //String likeNumber = c.getJSONObject("statistics").getString("likeCount");

            String date = c.getJSONObject("snippet").getString("publishedAt");
            String imgUrl = c.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url");
            //썸네일 이미지 URL값



            youTubeContentArrayList.add(new YouTubeContent(videoId,changedTitle,date,channelTitle,"1",imgUrl));
            //Video id를 이용해서 조회수,좋아요수 다시 찾아오기,
            getViewCountLikeCount(videoId,i);
        }
    }
    private String stringToHtmlSign(String title){
        return title.replaceAll("&amp;","[&]")
                .replaceAll("[<]]","&lt;")
                .replaceAll("[>]]","&gt;")
                .replaceAll("&quot","")
                .replaceAll("&#39","");

    }

    private void getViewCountLikeCount(String videoId,int position)throws IOException{
        String originUrl = "https://www.googleapis.com/youtube/v3/videos?"+"key="+API_KEY+"&part=statistics"+"&id="+videoId;
        String myUrl = String.format(originUrl);
        URL url = new URL(myUrl);


        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("GET");
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.connect();

        String line;
        String result="";
        InputStream inputStream=connection.getInputStream();
        BufferedReader reader =new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer response = new StringBuffer();

        while((line = reader.readLine())!=null){
            response.append(line);
        }
        result = response.toString();

        JSONObject jsonObject= new JSONObject();
        try{
            jsonObject=new JSONObject(result);
        }catch (JSONException e){
            e.printStackTrace();
        }

        try{
            JSONArray contacts = jsonObject.getJSONArray("items");
            String viewCount= contacts.getJSONObject(0).getJSONObject("statistics").getString("viewCount");
            String likeCount= contacts.getJSONObject(0).getJSONObject("statistics").getString("likeCount");

            youTubeContentArrayList.get(position).setLikeNumber(likeCount);
            youTubeContentArrayList.get(position).setViewCount(viewCount);

        }
        catch(JSONException e){
            e.printStackTrace();

        }





    }
}