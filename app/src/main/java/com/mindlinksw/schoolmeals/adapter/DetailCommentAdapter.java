package com.mindlinksw.schoolmeals.adapter;

import android.app.Activity;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ViewDataBinding;
import com.google.android.material.snackbar.Snackbar;

import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.dialog.AlertDialog;
import com.mindlinksw.schoolmeals.bottomsheet.DetailBottomSheet;
import com.mindlinksw.schoolmeals.interfaces.OnResponseListener;
import com.mindlinksw.schoolmeals.model.CommentItem;
import com.mindlinksw.schoolmeals.network.CommentAPI;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.utils.SnackBarUtils;
import com.mindlinksw.schoolmeals.view.activity.CommentActivity;
import com.mindlinksw.schoolmeals.view.activity.DetailActivity;
import com.mindlinksw.schoolmeals.view.activity.VideoDetailActivity;

import java.util.ArrayList;

public class DetailCommentAdapter extends RecyclerView.Adapter<DetailCommentAdapter.ViewHolder> {

    private String TAG = DetailCommentAdapter.class.getName();

    // Variables
    private Activity mActivity;
    private FragmentManager mFragmentManager;
    private ArrayList<CommentItem> mList;

    private int mBoardId;

    public DetailCommentAdapter(Activity activity, FragmentManager fragmentManager, ArrayList<CommentItem> list) {
        this.mActivity = activity;
        this.mList = list;
        this.mFragmentManager = fragmentManager;
    }

    public void setBoardId(int boardId) {
        this.mBoardId = boardId;
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getDepth();
    }

    @Override
    public DetailCommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        int layoutId = R.layout.common_empty;

        switch (viewType) {
            case 1:
                layoutId = R.layout.detail_comment_depth1_item;
                break;
            case 2:
                layoutId = R.layout.detail_comment_depth2_item;
                break;
            case 98:
                layoutId = R.layout.detail_comment_delete;
                break;
            case 99:
                layoutId = R.layout.detail_comment_more;
                break;
        }

        View view = DataBindingUtil.inflate(LayoutInflater.from(mActivity), layoutId, parent, false).getRoot();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DetailCommentAdapter.ViewHolder holder, final int position) {

        try {

            CommentItem item = mList.get(position);

            ViewModel viewModel = new ViewModel();
            viewModel.mModel.set(item);
            viewModel.mPosition.set(position);

            viewModel.setCommentData();

            holder.getBinding().setVariable(BR.viewModel, viewModel);

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

    // Handlers
    public class ViewModel {

        public ObservableField<CommentItem> mModel = new ObservableField<>();
        public ObservableInt mPosition = new ObservableInt();

        public ObservableBoolean mIsSelf = new ObservableBoolean(); // 자기 자신 체크
        public ObservableBoolean mIsComment = new ObservableBoolean(); // 내가 좋아요 했는지 여부
        public ObservableBoolean mIsWriter = new ObservableBoolean(); // 작성자 체크
        public ObservableInt mCommentCnt = new ObservableInt();

        // xml 에 정의
        public void onClick(final View view) {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onClick(View v, CommentItem model) {

            switch (v.getId()) {
                case R.id.ll_comment:
                case R.id.tv_comment_depth:
                case R.id.ll_comment_more:
                    Intent intent = new Intent(mActivity, CommentActivity.class);
                    intent.putExtra(DataConst.TYPE, "read");
                    intent.putExtra("boardId", mBoardId);
                    intent.putExtra(CommentItem.class.getName(), model);
                    mActivity.startActivity(intent);
                    break;
            }
        }

        public void onClick(View v, final int position, final CommentItem model) {
            switch (v.getId()) {
                case R.id.ll_comment_like:
                    if (SessionSingleton.getInstance(mActivity).isExist()) {
                        setLike(v, position, model);
                    } else {
                        SnackBarUtils.login(mActivity);
                    }
                    break;

                case R.id.iv_comment_more:
                    DetailBottomSheet sheet = new DetailBottomSheet();
                    sheet.setOnDetailBottomSheetListener(new DetailBottomSheet.OnDetailBottomSheetListener() {
                        @Override
                        public void onDismiss(int code) {
                            switch (code) {
                                case DetailBottomSheet.CODE.MODIFY:
                                    Intent intent = new Intent(mActivity, CommentActivity.class);
                                    intent.putExtra(DataConst.TYPE, "modify");
                                    intent.putExtra("boardId", mBoardId);
                                    intent.putExtra(CommentItem.class.getName(), model);
                                    mActivity.startActivity(intent);
                                    break;

                                case DetailBottomSheet.CODE.DELETE:
                                    // 삭제
                                    new AlertDialog(mActivity)
                                            .setMessage(mActivity.getString(R.string.detail_delete_comment_confirm))
                                            .setPositiveButton(mActivity.getString(R.string.alert_yes), new AlertDialog.OnDialogClickListener() {
                                                @Override
                                                public void onClick() {
                                                    deleteComment(model);
                                                }
                                            })
                                            .setNegativeButton(mActivity.getString(R.string.alert_no), new AlertDialog.OnDialogClickListener() {
                                                @Override
                                                public void onClick() {

                                                }
                                            })
                                            .show();
                                    break;
                            }
                        }
                    });
                    sheet.show(mFragmentManager, DetailBottomSheet.class.getName());
                    break;
            }
        }

        /**
         * 댓글 데이터
         */
        public void setCommentData() {

            try {

                // 자기가 작성한 글인지 체크
                mIsSelf.set(SessionSingleton.getInstance(mActivity).isSelf(mModel.get().getEsntlId()));

                // 작성자 체크
                mIsComment.set("Y".equals(mModel.get().getRecommendYn()));
                mIsWriter.set("Y".equals(mModel.get().getWriterYn()));
                mCommentCnt.set(mModel.get().getRecommendCnt());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 좋아요
         *
         * @param model
         */
        public void setLike(final View v, final int position, final CommentItem model) {

            v.setClickable(false);

            final boolean isDelete = model.getRecommendYn().equals("Y");
            Logger.e(TAG, "isDelete : " + isDelete + " / id : " + model.getCommentId());

            CommentAPI.setLike(mActivity, !isDelete, model.getCommentId(), new OnResponseListener() {
                @Override
                public void onSuccess() {
                    try {

                        if (isDelete) {

                            model.setRecommendYn("N");

                            // -1
                            if (model.getRecommendCnt() > 0) {
                                model.setRecommendCnt(model.getRecommendCnt() - 1);
                            }

                        } else {
                            model.setRecommendYn("Y");
                            model.setRecommendCnt(model.getRecommendCnt() + 1);
                        }

                        mModel.set(model);
                        setCommentData();

                        notifyItemChanged(position);

                        v.setClickable(true);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFail(String error) {
                    if (!ObjectUtils.isEmpty(error)) {
                        Snackbar.make(mActivity.findViewById(android.R.id.content), error, Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }

        /**
         * 댓글 삭제
         *
         * @param model
         */
        public void deleteComment(final CommentItem model) {

            CommentAPI.setDelete(mActivity, model.getCommentId(), new OnResponseListener() {
                @Override
                public void onSuccess() {

                    try {

                        if (mActivity instanceof DetailActivity) {
                            ((DetailActivity) mActivity).mViewModel.getCommentList();
                        } else if (mActivity instanceof VideoDetailActivity) {
                            ((VideoDetailActivity) mActivity).mViewModel.getCommentList();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFail(String error) {
                    if (!ObjectUtils.isEmpty(error)) {
                        Snackbar.make(mActivity.findViewById(android.R.id.content), error, Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}