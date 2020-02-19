package com.mindlinksw.schoolmeals.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.mindlinksw.schoolmeals.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.Random;

public abstract class GenericRecyclerViewAdapter<T, D> extends RecyclerView.Adapter<GenericRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private int mLayoutId;
    private ArrayList<T> mList;

    private Random mRandom = new Random();

    public abstract void onBindData(int position, T model, D dataBinding);

    public GenericRecyclerViewAdapter(Context context, int layoutId, ArrayList<T> arrayList) {
        this.mContext = context;
        this.mLayoutId = layoutId;
        this.mList = arrayList;

        setHasStableIds(true);
    }

    @Override
    public GenericRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), mLayoutId, parent, false);
        return new ViewHolder(dataBinding);
    }

    @Override
    public void onBindViewHolder(GenericRecyclerViewAdapter.ViewHolder holder, final int position) {

        try {
            onBindData(position, mList.get(position), ((ViewHolder) holder).mDataBinding);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return !ObjectUtils.isEmpty(mList) ? mList.size() : 0;
    }

    public void addItems(ArrayList<T> arrayList) {
        mList = arrayList;
        this.notifyDataSetChanged();
    }

    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        try {

            if (mList.get(position) != null) {
                return mList.get(position).hashCode();
            } else {
                return mRandom.nextInt();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected D mDataBinding;

        public ViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mDataBinding = (D) binding;
        }

    }
}
