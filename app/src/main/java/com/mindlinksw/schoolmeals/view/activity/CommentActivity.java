package com.mindlinksw.schoolmeals.view.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.google.android.material.snackbar.Snackbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.ad.AdlibConstants;
import com.mindlinksw.schoolmeals.adapter.GenericRecyclerViewAdapter;
import com.mindlinksw.schoolmeals.bottomsheet.DetailBottomSheet;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.databinding.CommentActivityBinding;
import com.mindlinksw.schoolmeals.databinding.CommentItemBinding;
import com.mindlinksw.schoolmeals.dialog.AlertDialog;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.interfaces.OnResponseListener;
import com.mindlinksw.schoolmeals.model.CommentItem;
import com.mindlinksw.schoolmeals.model.CommentListModel;
import com.mindlinksw.schoolmeals.model.CommentModel;
import com.mindlinksw.schoolmeals.network.CommentAPI;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.utils.SnackBarUtils;
import com.mindlinksw.schoolmeals.utils.Utils;
import com.mocoplex.adlib.AdlibManager;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 댓글 상세
 */
public class CommentActivity extends BaseActivity implements Initialize, SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = CommentActivity.class.getName();

    private CommentActivityBinding mBinding;
    private ViewModel mViewModel;

    private GenericRecyclerViewAdapter mAdapter;
    private ArrayList<CommentItem> mList;

    private CommentItem mComment;
    private String mType;
    private int mBoardId; // 댓글을 등록하기 위한 용도
    private int mCommentId;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.comment_activity);

        initDataBinding();
        initLayout(null);
        initVariable();
        initEventListener();

    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void initDataBinding() {
        mViewModel = new ViewModel();
        mViewModel.mIsWriter.set(false);
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void initLayout(@Nullable View view) {

        mBinding.refresh.setOnRefreshListener(this);
        mBinding.refresh.setRefreshing(true);

        mViewModel.mIsWrite.set(true);

        // 댓글 리스트
        mList = new ArrayList<>();
        mAdapter = new GenericRecyclerViewAdapter(this, R.layout.comment_item, mList) {
            @Override
            public void onBindData(int position, Object model, Object dataBinding) {

                CommentItemBinding binding = (CommentItemBinding) dataBinding;
                ViewModel viewModel = new ViewModel();
                viewModel.mModel.set((CommentItem) model);
                viewModel.mIsWriter.set(false);
                viewModel.setCommentData();

                binding.setViewModel(viewModel);

            }
        };
        mBinding.rvComment.setAdapter(mAdapter);
    }

    @Override
    public void initVariable() {

        try {

            Intent intent = getIntent();
            mType = intent.getStringExtra(DataConst.TYPE);
            mComment = (CommentItem) intent.getSerializableExtra(CommentItem.class.getName());
            mBoardId = intent.getIntExtra("boardId", 0);
            Logger.e(TAG, "mComment : " + mComment.toString() + " / mBoardId : " + mBoardId);

            if (mComment != null && mBoardId != 0) {

                // commentId 추출
                mCommentId = mComment.getDepth() == 1 ? mComment.getCommentId() : mComment.getParentId();

                // 수정일때
                if (!ObjectUtils.isEmpty(mType) && "modify".equals(mType)) {
                    mViewModel.mModifyCommentId = mComment.getCommentId();
                    mViewModel.mIsWrite.set(false);
                    mBinding.etComment.setText(mComment.getContent());
                    mBinding.etComment.setSelection(mBinding.etComment.getText().toString().length());
                    Utils.setKeyboardShowHide(CommentActivity.this, mBinding.getRoot(), true);
                }

            } else {
                Snackbar.make(mBinding.getRoot(), getString(R.string.comment_read_fail), Snackbar.LENGTH_SHORT).show();
                finish();
                return;
            }

            // 스크롤 아래로
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBinding.scroll.fullScroll(View.FOCUS_DOWN);
                }
            }, 300);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initEventListener() {


    }

    @Override
    public void onRefresh() {

        mBinding.refresh.setRefreshing(true);
        mViewModel.getComment(mCommentId);
        mViewModel.getCommentList(mCommentId);

    }

    /**
     * View Model
     */
    public class ViewModel {

        public ObservableField<CommentItem> mModel = new ObservableField<>();
        public ObservableInt mPosition = new ObservableInt();

        public ObservableInt mCommentCnt = new ObservableInt();
        public ObservableBoolean mIsSelf = new ObservableBoolean(); // 자기 자신 체크
        public ObservableBoolean mIsWriter = new ObservableBoolean(); // 작성자 체크
        public ObservableBoolean mIsComment = new ObservableBoolean(); // 내가 좋아요 했는지 여부
        public ObservableBoolean mIsWrite = new ObservableBoolean(); // 일반작성모드
        public ObservableBoolean mIsDisable = new ObservableBoolean(); // 비활성화 여부
        public ObservableBoolean mIsLoading = new ObservableBoolean();

        public int mModifyCommentId = -1;

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;

                case R.id.tv_comment_submit:
                    if (SessionSingleton.getInstance(CommentActivity.this).isExist()) {
                        setComment(mBoardId, mComment.getCommentId(), mBinding.etComment.getText().toString());
                    } else {
                        SnackBarUtils.login(CommentActivity.this);
                    }
                    break;

                case R.id.tv_modify_submit:
                    // 수정
                    if (SessionSingleton.getInstance(CommentActivity.this).isExist()) {
                        if (mModifyCommentId > -1) {
                            modifyComment(mModifyCommentId, mBinding.etComment.getText().toString());
                            mBinding.etComment.setText(null);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.setKeyboardShowHide(CommentActivity.this, mBinding.getRoot(), false);
                                }
                            }, 500);
                        }
                    } else {
                        SnackBarUtils.login(CommentActivity.this);
                    }
                    break;

                case R.id.tv_modify_cancel:
                    // 수정취소
                    mModifyCommentId = -1;
                    mBinding.etComment.setText(null);
                    mIsWrite.set(true);
                    break;
            }
        }

        public void onClick(final View v, int position, final CommentItem model) {
            switch (v.getId()) {
                case R.id.ll_comment_like:
                    if (SessionSingleton.getInstance(CommentActivity.this).isExist()) {
                        setLike(v, position, model);
                    } else {
                        SnackBarUtils.login(CommentActivity.this);
                    }
                    break;

                case R.id.iv_comment_more:
                    DetailBottomSheet sheet = new DetailBottomSheet();
                    sheet.setOnDetailBottomSheetListener(new DetailBottomSheet.OnDetailBottomSheetListener() {
                        @Override
                        public void onDismiss(int code) {
                            switch (code) {
                                case DetailBottomSheet.CODE.MODIFY:
                                    // 수정
                                    mViewModel.mModifyCommentId = model.getCommentId();
                                    mViewModel.mIsWrite.set(false);
                                    mBinding.etComment.setText(model.getContent());
                                    mBinding.etComment.setSelection(mBinding.etComment.getText().toString().length());
                                    Utils.setKeyboardShowHide(CommentActivity.this, mBinding.getRoot(), true);
                                    break;

                                case DetailBottomSheet.CODE.DELETE:
                                    // 삭제
                                    new AlertDialog(CommentActivity.this)
                                            .setMessage(getString(R.string.detail_delete_comment_confirm))
                                            .setPositiveButton(CommentActivity.this.getString(R.string.alert_yes), new AlertDialog.OnDialogClickListener() {
                                                @Override
                                                public void onClick() {
                                                    deleteComment(model.getCommentId());
                                                }
                                            })
                                            .setNegativeButton(getString(R.string.alert_no), new AlertDialog.OnDialogClickListener() {
                                                @Override
                                                public void onClick() {

                                                }
                                            })
                                            .show();
                                    break;
                            }
                        }
                    });
                    sheet.show(getSupportFragmentManager(), DetailBottomSheet.class.getName());
                    break;
            }
        }

        /**
         * 댓글 정보 세팅
         */
        public void setCommentData() {
            try {

                // 자기 자신 체크
                mIsSelf.set(SessionSingleton.getInstance(CommentActivity.this).isSelf(mModel.get().getEsntlId()));

                // 댓글 정보
                mIsDisable.set("Y".equals(mModel.get().getDelYn()));
                mIsComment.set("Y".equals(mModel.get().getRecommendYn()));
                mIsWriter.set("Y".equals(mModel.get().getWriterYn()));
                mCommentCnt.set(mModel.get().getRecommendCnt());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 댓글 리스트 조회 - 단일건
         */
        public void getComment(int commentId) {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(CommentActivity.this, SchoolMealsService.class, HostConst.apiHost());

            Call<CommentModel> call = service.reqComment(commentId);

            RetrofitCall.enqueueWithRetry(call, new Callback<CommentModel>() {

                @Override
                public void onResponse(Call<CommentModel> call, Response<CommentModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            CommentModel resData = (CommentModel) response.body();

                            if (resData != null) {
                                mViewModel.mModel.set(resData.getResultMap());
                                mViewModel.setCommentData();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mBinding.refresh.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<CommentModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }


        /**
         * 댓글 리스트 조회
         */
        public void getCommentList(int commentId) {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(CommentActivity.this, SchoolMealsService.class, HostConst.apiHost());

            Call<CommentListModel> call = service.reqCommentDetailList(
                    commentId,
                    1,
                    1000,
                    1000);

            RetrofitCall.enqueueWithRetry(call, new Callback<CommentListModel>() {

                @Override
                public void onResponse(Call<CommentListModel> call, Response<CommentListModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            CommentListModel resData = (CommentListModel) response.body();

                            if (resData != null) {

                                mList.clear();
                                mList.addAll(resData.getList());
                                mAdapter.notifyItemRangeChanged(0, mList.size());
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mBinding.refresh.setRefreshing(false);
                }

                @Override
                public void onFailure(Call<CommentListModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }

        /**
         * 댓글 달기
         *
         * @param boardId
         * @param replyCommentId - 1depth 일 경우 필요하지 않음
         * @param content
         */
        public void setComment(int boardId, int replyCommentId, String content) {

            if (content.length() < 1) {
                Snackbar.make(mBinding.getRoot(), getString(R.string.detail_comment_empty), Snackbar.LENGTH_SHORT).show();
                return;
            }

            content = Utils.getMultiLineDisable(content, "\n\n");

            mIsLoading.set(true);

            CommentAPI.setWrite(CommentActivity.this, boardId, replyCommentId, content, new OnResponseListener() {
                @Override
                public void onSuccess() {

                    try {

                        onRefresh();

                        mBinding.etComment.setText(null);

                        // 키보드 내리기
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.setKeyboardShowHide(CommentActivity.this, mBinding.getRoot(), false);
                            }
                        });

                        // 스크롤 아래로
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (mAdapter.getItemCount() > 0) {
                                        mBinding.rvComment.smoothScrollToPosition(mAdapter.getItemCount() - 1);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 300);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mIsLoading.set(false);
                }

                @Override
                public void onFail(String error) {
                    if (!ObjectUtils.isEmpty(error)) {
                        Snackbar.make(mBinding.getRoot(), error, Snackbar.LENGTH_SHORT).show();
                    }

                    mIsLoading.set(false);
                }
            });

        }

        /**
         * 댓글 수정
         */
        public void modifyComment(final int commentId, String content) {

            try {

                if (ObjectUtils.isEmpty(commentId)) {
                    Snackbar.make(mBinding.getRoot(), getString(R.string.detail_comment_empty), Snackbar.LENGTH_SHORT).show();
                    return;
                }

                content = Utils.getMultiLineDisable(content, "\n\n");

                mIsLoading.set(true);

                CommentAPI.setModify(CommentActivity.this, commentId, content, new OnResponseListener() {
                    @Override
                    public void onSuccess() {

                        try {
                            mIsWrite.set(true);
                            onRefresh();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        mIsLoading.set(false);
                    }

                    @Override
                    public void onFail(String error) {
                        if (!ObjectUtils.isEmpty(error)) {
                            Snackbar.make(mBinding.getRoot(), error, Snackbar.LENGTH_SHORT).show();
                        }

                        mIsLoading.set(false);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 댓글 삭제
         */
        public void deleteComment(int commentId) {

            CommentAPI.setDelete(CommentActivity.this, commentId, new OnResponseListener() {
                @Override
                public void onSuccess() {
                    onRefresh();
                }

                @Override
                public void onFail(String error) {
                    if (!ObjectUtils.isEmpty(error)) {
                        Snackbar.make(mBinding.getRoot(), error, Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }

        /**
         * 좋아요
         */
        public void setLike(final View v, final int position, final CommentItem model) {

            try {

                v.setClickable(false);

                final boolean isDelete = model.getRecommendYn().equals("Y");
                Logger.e(TAG, "isDelete : " + isDelete + " / id : " + model.getCommentId());

                CommentAPI.setLike(CommentActivity.this, !isDelete, model.getCommentId(), new OnResponseListener() {
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


                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    mModel.set(model);
                                    setCommentData();

                                    mAdapter.notifyItemChanged(position);

                                    v.setClickable(true);

                                }
                            }, 150);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        if (!ObjectUtils.isEmpty(error)) {
                            Snackbar.make(mBinding.getRoot(), error, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}