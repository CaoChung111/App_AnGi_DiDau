package com.example.angi_didau.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.angi_didau.R;
import com.example.angi_didau.data.model.SavedPlan;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SavedPlansAdapter extends RecyclerView.Adapter<SavedPlansAdapter.ViewHolder> {

    private List<SavedPlan> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SavedPlan plan);
    }

    public SavedPlansAdapter(List<SavedPlan> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedPlan item = items.get(position);
        holder.tvPlanTitle.setText(item.getTitle());
        holder.tvPlanCost.setText(item.getTotalCost());
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvPlanDate.setText(sdf.format(new Date(item.getTimestamp())));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void updateData(List<SavedPlan> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlanTitle, tvPlanCost, tvPlanDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPlanTitle = itemView.findViewById(R.id.tvPlanTitle);
            tvPlanCost = itemView.findViewById(R.id.tvPlanCost);
            tvPlanDate = itemView.findViewById(R.id.tvPlanDate);
        }
    }
}
