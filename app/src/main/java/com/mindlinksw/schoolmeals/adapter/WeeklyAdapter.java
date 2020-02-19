package com.mindlinksw.schoolmeals.adapter;

import android.app.Activity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.library.baseAdapters.BR;
import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.consts.Style;
import com.mindlinksw.schoolmeals.model.WeeklyDisplayModel;
import com.mindlinksw.schoolmeals.utils.TextFormatUtils;

import java.util.ArrayList;

public class WeeklyAdapter extends RecyclerView.Adapter<WeeklyAdapter.ViewHolder> {

    // Variables
    private Activity mActivity;
    private ArrayList<WeeklyDisplayModel> mList;

    public WeeklyAdapter(Activity Activity, ArrayList<WeeklyDisplayModel> list) {
        this.mActivity = Activity;
        this.mList = list;
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getStyle();
    }

    @Override
    public WeeklyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutId = R.layout.common_empty;

        if (viewType == Style.WEEKLY.TITLE) {
            layoutId = R.layout.weekly_item_title;
        } else if (viewType == Style.WEEKLY.MEAL) {
            layoutId = R.layout.weekly_item;
        }

        View view = DataBindingUtil.inflate(LayoutInflater.from(mActivity), layoutId, parent, false).getRoot();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WeeklyAdapter.ViewHolder holder, final int position) {

        try {

            WeeklyDisplayModel item = mList.get(position);

            ViewModel viewModel = new ViewModel();
            viewModel.mModel.set(item);
            viewModel.getMealType();

            holder.getBinding().setVariable(BR.viewModel, viewModel);
            holder.getBinding().executePendingBindings();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }

    /**
     * ViewModel
     */
    public class ViewModel {

        public ObservableField<WeeklyDisplayModel> mModel = new ObservableField<>();
        public ObservableField<String> mMealType = new ObservableField<>();
        public ObservableInt mMealIcon = new ObservableInt();

        /**
         * 아침, 점심, 저녁 판단
         */
        public void getMealType() {

            try {

                mMealType.set(TextFormatUtils.getMealType(mModel.get().getMealType()));

                if ("B".equals(mModel.get().getMealType())) {
                    mMealIcon.set(R.drawable.ico_weekly_breakfast);
                } else if ("L".equals(mModel.get().getMealType())) {
                    mMealIcon.set(R.drawable.ico_weekly_lunch);
                } else if ("D".equals(mModel.get().getMealType())) {
                    mMealIcon.set(R.drawable.ico_weekly_dinner);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}