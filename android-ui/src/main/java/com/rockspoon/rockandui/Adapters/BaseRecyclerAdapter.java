package com.rockspoon.rockandui.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.rockspoon.rockandui.RecyclerTools.HolderWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter {

    protected List<HolderWrapper> holderWrappers = new ArrayList<>();
    private Map<String, HolderWrapper> holderWrapperTags = new HashMap<>();

    @Override
    public int getItemCount() {
        return holderWrappers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return holderWrappers.get(position).getType();
    }

    protected void addWrapper(@NonNull HolderWrapper holderWrapper) {
        holderWrappers.add(holderWrapper);
    }

    protected void addWrapper(@NonNull String key, @NonNull HolderWrapper holderWrapper) {
        holderWrappers.add(holderWrapper);
        holderWrapperTags.put(key, holderWrapper);
    }

    public HolderWrapper getWrapperByKey(String key) {
        return holderWrapperTags.get(key);
    }
}
