package pl.marchuck.facecrawler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Lukasz Marczak
 * @since 03.05.16.
 */
public class FaceAdapter extends RecyclerView.Adapter<FaceAdapter.VH> {
    private static int lastPosition = -1;
    private final Listener listener;
    List<String> items = new ArrayList<>();

    public interface Listener {
        void onClicked(int j);
    }
    public FaceAdapter(Listener listener) {
        this.listener = listener;
        items.add("Like");
        items.add("Post something");
        items.add("Share photo");
        items.add("Share status update");
        items.add("About");
        items.add("Exit");
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item, null);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        if (position == lastPosition) {
            holder.text.setTextColor(App.instance.getResources().getColor(android.R.color.black));
        } else {
            holder.text.setTextColor(App.instance.getResources().getColor(android.R.color.white));
        }

        holder.text.setText(items.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClicked(position);
                lastPosition = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        @Bind(R.id.textView)
        TextView text;

        public VH(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}