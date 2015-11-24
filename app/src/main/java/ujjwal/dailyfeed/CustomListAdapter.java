package ujjwal.dailyfeed;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import AsyncTask.ImageDownloaderTask;
import Model.Feed;

/**
 * Created by Ujjwal on 18-10-2015.
 */
public class CustomListAdapter extends ArrayAdapter {
    private List<Feed> feedList;
    private List<Feed> postingList = new ArrayList<Feed>();
    private LayoutInflater layoutInflater;
    private Context mContext;

    public CustomListAdapter(Context mContext,List<Feed> feedList){
        super(mContext,0);
        this.feedList=feedList;
        this.mContext=mContext;
        layoutInflater = layoutInflater.from(mContext);
        this.postingList=feedList;

    }

    @Override
    public int getCount() {
        return postingList.size();
    }

    @Override
    public Object getItem(int position) {
        return postingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            convertView = layoutInflater.inflate(R.layout.list_row_layout,null);
            viewHolder = new ViewHolder();
            viewHolder.title=(TextView)convertView.findViewById(R.id.title);
            viewHolder.thumbImage=(ImageView)convertView.findViewById(R.id.thumbImage);

            convertView.setTag(viewHolder);
        }
        else
            viewHolder = (ViewHolder)convertView.getTag();
        Feed feed = (Feed)postingList.get(position);
        viewHolder.title.setText(feed.getTitle());

        if(viewHolder.thumbImage != null)
            new ImageDownloaderTask(viewHolder.thumbImage).execute(feed.getImageURL());
        return convertView;
    }

    //inner class holds one or more reference to the outer class by default so making it static prevents this hence also prevents
    //memory leak
    static class ViewHolder{
        TextView title;
        ImageView thumbImage;
    }

    public void setData(ArrayList<Feed> mPpst) {
        postingList = mPpst;//contains class items data.
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count >= 0)
                    postingList=(ArrayList<Feed>)results.values;//if results of search is null set the searched results data
                else
                    postingList = feedList;// set original values

                notifyDataSetInvalidated();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults result = new FilterResults();
                if (!TextUtils.isEmpty(constraint)) {
                    constraint = constraint.toString().toLowerCase();
                    ArrayList<Feed> foundItems = new ArrayList<Feed>();
                    if(feedList!=null)
                    {
                        for(int i=0;i<feedList.size();i++)
                        {
                            System.out.println("!!!!!!!!!!!!!!!!!!!"+constraint);
                            //If mTitle contains the string entered in Editext
                            if (feedList.get(i).getTitle().toString().toLowerCase().contains(constraint.toString().toLowerCase()) || feedList.get(i).getSource().toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                System.out.println(feedList.get(i).getSource()+"!!!!!!!!!!"+feedList.get(i).getTitle());
                                foundItems.add(feedList.get(i));
                            }
                        }
                    }
                    result.count = foundItems.size();//search results found return count
                    result.values = foundItems;// return values
                }
                else
                {
                    result.count=-1;// no search results found
                }
                return result;
            }
        };
    }
    public void getCategories(String category){
        ArrayList<Feed> sortByCategory = new ArrayList<Feed>();
        if(category.equals("ALL"))
            this.postingList = this.feedList;
        else {
            for (Feed f : feedList) {
                if (f.getCategory().equals(category))
                    sortByCategory.add(f);
            }
            this.postingList = sortByCategory;
        }
    }
}
