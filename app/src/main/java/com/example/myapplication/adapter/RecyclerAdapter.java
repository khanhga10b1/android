package com.example.myapplication.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.items.RecyclerViewClickListener;
import com.example.myapplication.utils.Constants;
import com.example.myapplication.entities.Events;

import java.util.HashSet;
import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<Events> objects;
    public static Context mContext;
    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private static RecyclerViewClickListener itemListener;

    public RecyclerAdapter() {
    }

    public RecyclerAdapter(Context context, List<Events> cur, RecyclerViewClickListener listener) {
        mContext = context;
        objects = cur;
        itemListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitleName;
        public TextView txtTitleCount;
        public ImageView imgTitleEvent;
        public ImageView imgTitleArrow;

        public TextView txtContentName;
        public TextView txtContentDate;
        public TextView txtContentDiffDate;
        public TextView txtContentCategory;
        public Button btnContentModify;
        public Button btnContentDelete;
        public TextView txtContentAnnual;
        public LinearLayout lnVisible;

        public ViewHolder(View v) {
            super(v);
            txtTitleName =  v.findViewById(R.id.title_txt_name);
            txtTitleCount =  v.findViewById(R.id.title_txt_count);
            imgTitleEvent =  v.findViewById(R.id.title_image_event);
            imgTitleArrow =  v.findViewById(R.id.title_image_arrow);
            btnContentDelete =  v.findViewById(R.id.content_button_delete);
            btnContentModify = v.findViewById(R.id.content_button_modify);
            txtContentAnnual =  v.findViewById(R.id.content_annual);

            txtContentName = v.findViewById(R.id.content_txt_name);
            txtContentDate =  v.findViewById(R.id.content_txt_date);
            txtContentDiffDate =  v.findViewById(R.id.content_txt_diff_date);
            txtContentCategory =  v.findViewById(R.id.content_txt_category);
            lnVisible= v.findViewById(R.id.ln_visible);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lnVisible.getVisibility()==View.VISIBLE){
                                lnVisible.setVisibility(View.GONE);
                    }else {
                        lnVisible.setVisibility(View.VISIBLE);
                    }
                }
            });

            btnContentDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.recyclerViewListClicked(2, v,getAdapterPosition());
                }
            });
            btnContentModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.recyclerViewListClicked(1, v, getAdapterPosition());
                }
            });
        }


    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Events listViewItem = objects.get(position);
        viewHolder.txtTitleName.setText(listViewItem.getName());
        viewHolder.imgTitleEvent.setImageResource(Constants.background[listViewItem.getImg()]);
        switch (listViewItem.getKind()) {
            case Constants.EVENT_ANNIVERSARY:
                // viewHolder.imgTitleEvent.setImageResource(R.drawable.anniversary);
                viewHolder.txtContentCategory.setText("ANNIVERSARY");
                break;
            case Constants.EVENT_EDUCATION:
                //  viewHolder.imgTitleEvent.setImageResource(R.drawable.education);
                viewHolder.txtContentCategory.setText("EDUCATION");
                break;
            case Constants.EVENT_JOB:
                //  viewHolder.imgTitleEvent.setImageResource(R.drawable.job);
                viewHolder.txtContentCategory.setText("JOB");
                break;
            case Constants.EVENT_LIFE:
                // viewHolder.imgTitleEvent.setImageResource(R.drawable.life);
                viewHolder.txtContentCategory.setText("LIFE");
                break;
            case Constants.EVENT_TRIP:
                //  viewHolder.imgTitleEvent.setImageResource(R.drawable.trip);
                viewHolder.txtContentCategory.setText("TRIP");
                break;
            default:
                //  viewHolder.imgTitleEvent.setImageResource(R.drawable.other);
                viewHolder.txtContentCategory.setText("OTHERS");
                break;
        }


        if (listViewItem.getDiff() > 0) {
            if (MainActivity.sharedPreferences.getBoolean(MainActivity.DISPLAY_DAY, true))
                viewHolder.txtTitleCount.setText(String.valueOf(listViewItem.getDiff()));
            else {
                try {
                    viewHolder.txtTitleCount.setText(listViewItem.getDiffString(0));
                } catch (Exception e) {
                }
            }
            viewHolder.imgTitleArrow.setImageResource(R.drawable.arrow_right);
        } else if (listViewItem.getDiff() == 0) {
            viewHolder.imgTitleArrow.setVisibility(View.GONE);
            viewHolder.txtTitleCount.setText(String.valueOf(0));
        } else {
            if (MainActivity.sharedPreferences.getBoolean(MainActivity.DISPLAY_DAY, true))
                viewHolder.txtTitleCount.setText(String.valueOf(-listViewItem.getDiff()));
            else {
                try {
                    viewHolder.txtTitleCount.setText(listViewItem.getDiffString(0));
                } catch (Exception e) {
                }
            }
        }

        viewHolder.txtContentDate.setText(listViewItem.getDate());
        viewHolder.txtContentName.setText(listViewItem.getName());
        try {
            viewHolder.txtContentDiffDate.setText(listViewItem.getDiffString(1));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }




    public List<Events> removeAt(int position) {
        objects.remove(position);
        notifyItemRemoved(position);
        return objects;
        // notifyItemRangeRemoved(position, objects.size());
    }

    public void updateData(List<Events> events) {
        objects.clear();
        objects.addAll(events);
        notifyDataSetChanged();
    }

}


