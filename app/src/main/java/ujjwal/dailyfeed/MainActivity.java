package ujjwal.dailyfeed;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import Model.Feed;


public class MainActivity extends ActionBarActivity {

    private List<Feed> feedList;
    private ProgressBar progressBar ;
    private ListView feedListView;
    private String[] spnr = {"All","World","Education","Science","Technology","Food","Sports"};
    private Spinner spinner;
    public static ArrayList<Feed> bookmarks = new ArrayList<Feed>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        String url = " http://dailyhunt.0x10.info/api/dailyhunt?type=json&query=list_news";
        new DownloadFeedTask().execute(url);
    }

    public void updateList(){
        feedListView = (ListView)findViewById(R.id.custom_list);
        feedListView.setTextFilterEnabled(true);
        feedListView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        final CustomListAdapter customListAdapter = new CustomListAdapter(this,feedList);
        feedListView.setAdapter(customListAdapter);

        EditText search = (EditText)findViewById(R.id.inputSearch);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                customListAdapter.getFilter().filter(s);
                customListAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.simple_spinner_item,spnr);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    customListAdapter.getCategories(spnr[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        feedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Feed  feedItem=(Feed)feedListView.getItemAtPosition(position);//get the position in the array
                Intent intent = new Intent(MainActivity.this,FeedDetailsActivity.class);
                intent.putExtra("feed",feedItem);
                startActivity(intent);
            }
        });

    }
    private class DownloadFeedTask extends AsyncTask<String,Integer,Void> {

        @Override
        protected Void doInBackground(String... params) {
            String url = params[0];
            JSONObject json = getJSONFromURL(url);
            parseJSON(json);
            return null;
        }

        public JSONObject getJSONFromURL(String url){
            JSONObject jObj = null;
            String json = null;
            try{
                URL u = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection)u.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder  builder = new StringBuilder();
                String line;
                while((line=reader.readLine())!= null){
                    System.out.println(line);
                    builder.append(line+"\n");
                }
                reader.close();
                json=builder.toString();
            }catch (Exception e){
                Log.e("Error",e.getMessage());
            }
            try{
                jObj=new JSONObject(json);
            }catch (JSONException e){
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            return jObj;
        }

        public void parseJSON(JSONObject jObj){
            try{
                JSONArray articles = jObj.getJSONArray("articles");
                feedList = new ArrayList<Feed>();
                for(int i=0;i<articles.length();i++){
                    JSONObject post = (JSONObject) articles.get(i);
                    Feed feedItem = new Feed();
                    feedItem.setTitle(post.getString("title"));
                    feedItem.setSource(post.getString("source"));
                    feedItem.setCategory(post.getString("category"));
                    feedItem.setImageURL(post.getString("image"));
                    feedItem.setContent(post.getString("content"));
                    feedItem.setSourceURL(post.getString("url"));

                    feedList.add(feedItem);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        @Override
        protected void onPostExecute(Void result) {
            if (null != feedList) {
                updateList();
            }
        }
    }

}
