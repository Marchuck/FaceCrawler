package pl.marchuck.facecrawler.utils;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.marchuck.facecrawler.R;
import pl.marchuck.facecrawler.thirdPartyApis.common.EditableDialog;

/**
 * @author Lukasz Marczak
 * @since 19.05.16.
 */
public class TagAdapter extends RecyclerView.Adapter<TagAdapter.VH> {
    static TagAdapter instance;

    public Activity currentActivity;

    public static TagAdapter getInstance() {
        return instance == null ? new TagAdapter() : instance;
    }

    public List<String> dataset = new ArrayList<>();

    private TagAdapter() {
        dataset.add("automata");
        dataset.add("mechanical");
        dataset.add("electrical");
        dataset.add("programming");
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tagg, null, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        holder.tv.setTextColor(Color.WHITE);
        holder.tv.setText(dataset.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EditableDialog(dataset.get(position), currentActivity, new EditableDialog.EditCallback() {
                    @Override
                    public void onEdit(String s) {
                        dataset.set(position, s);
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv;

        public VH(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}
