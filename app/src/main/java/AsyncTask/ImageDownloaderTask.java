package AsyncTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import ujjwal.dailyfeed.R;

/**
 * Created by Ujjwal on 18-10-2015.
 */
public class ImageDownloaderTask extends AsyncTask<String,Void,Bitmap> {
    private final WeakReference<ImageView> imageViewReference;

    public ImageDownloaderTask(ImageView imageView){
        this.imageViewReference=new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String...params){
        return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap){
        if(isCancelled()) bitmap = null;
        if(imageViewReference!=null){
            ImageView imageView = imageViewReference.get();
            if(imageView!=null){
                if(bitmap!=null)
                    imageView.setImageBitmap(bitmap);
                else
                    imageView.setImageDrawable(imageView.getContext().getResources().getDrawable(R.drawable.list_placeholder));
            }
        }
    }
    static Bitmap downloadBitmap(String url) {
        InputStream inputStream = null;
        HttpURLConnection urlConnection=null;
        try {
            try {
                URL u = new URL(url);
                urlConnection = (HttpURLConnection) u.openConnection();
                inputStream = urlConnection.getInputStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (Exception e) {
                Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
            } finally {
                if(urlConnection!=null)
                    urlConnection.disconnect();
                inputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
