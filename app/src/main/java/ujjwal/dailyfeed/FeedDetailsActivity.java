package ujjwal.dailyfeed;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import AsyncTask.ImageDownloaderTask;
import Model.Feed;

/**
 * Created by Ujjwal on 18-10-2015.
 */
public class FeedDetailsActivity extends ActionBarActivity {

    private Feed feed;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_details);
        feed = (Feed) this.getIntent().getSerializableExtra("feed");

        if (feed != null) {
            ImageView thumb = (ImageView) findViewById(R.id.featuredImg);
            new ImageDownloaderTask(thumb).execute(feed.getImageURL());

            TextView title = (TextView) findViewById(R.id.title);
            title.setText(feed.getTitle());

            TextView htmlTextView = (TextView) findViewById(R.id.content);
            htmlTextView.setText(Html.fromHtml(feed.getContent(), null, null));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_share:
                shareContent();
                return true;
            case R.id.menu_view:
                Intent intent = new Intent(FeedDetailsActivity.this , WebViewActivity.class);
                intent.putExtra("url",feed.getSourceURL());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareContent(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,feed.getTitle()+"\n"+feed.getSource()+"\n"+feed.getSourceURL());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent,"Share using"));
    }
}
