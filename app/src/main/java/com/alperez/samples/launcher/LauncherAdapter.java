package com.alperez.samples.launcher;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alperez.samples.R;
import com.alperez.samples.databinding.LayncherListItemBinding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by stanislav.perchenko on 10/16/2018
 */
public abstract class LauncherAdapter extends RecyclerView.Adapter<LauncherAdapter.VH> {


    private final Context context;
    private final LayoutInflater inflater;
    private final List<LauncherScreenItem> mData = new ArrayList<>();

    public LauncherAdapter(Context context, @Nullable Collection<LauncherScreenItem> data) {
        inflater = LayoutInflater.from(this.context = context);
        if (data != null) mData.addAll(data);
    }

    public void addAll(Collection<LauncherScreenItem> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public void setData(Collection<LauncherScreenItem> data) {
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(DataBindingUtil.inflate(inflater, R.layout.layncher_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.bindData(position, mData.get(position));
    }

    /**********************************************************************************************/
    public abstract void onItemClicked(int position, LauncherScreenItem item);

    class VH extends RecyclerView.ViewHolder {
        private int position = -1;
        private LayncherListItemBinding itemBinding;

        public VH(LayncherListItemBinding itemBinding) {
            super(itemBinding.getRoot());
            (this.itemBinding = itemBinding).setItemClicker(v -> {
                if (position >= 0) onItemClicked(position, itemBinding.getDataItem());
            });
        }

        public void bindData(int position, @NonNull LauncherScreenItem data) {
            this.position = position;
            itemBinding.setDataItem(data);
        }
    }
}
