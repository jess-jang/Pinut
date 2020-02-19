package com.mindlinksw.schoolmeals.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;

import com.google.android.material.snackbar.Snackbar;
import com.mindlinksw.schoolmeals.R;
import com.mindlinksw.schoolmeals.adapter.DetailCommentAdapter;
import com.mindlinksw.schoolmeals.adapter.GenericRecyclerViewAdapter;
import com.mindlinksw.schoolmeals.bottomsheet.DetailBottomSheet;
import com.mindlinksw.schoolmeals.consts.CategoryConst;
import com.mindlinksw.schoolmeals.consts.DataConst;
import com.mindlinksw.schoolmeals.consts.HostConst;
import com.mindlinksw.schoolmeals.consts.RequestConst;
import com.mindlinksw.schoolmeals.databinding.DetailActivityBinding;
import com.mindlinksw.schoolmeals.databinding.DetailPictureItemBinding;
import com.mindlinksw.schoolmeals.dialog.AlertDialog;
import com.mindlinksw.schoolmeals.interfaces.Initialize;
import com.mindlinksw.schoolmeals.interfaces.OnResponseListener;
import com.mindlinksw.schoolmeals.manager.SchemeManager;
import com.mindlinksw.schoolmeals.model.AttachItem;
import com.mindlinksw.schoolmeals.model.BoardData;
import com.mindlinksw.schoolmeals.model.BoardDetailModel;
import com.mindlinksw.schoolmeals.model.BoardInsertModel;
import com.mindlinksw.schoolmeals.model.BoardItem;
import com.mindlinksw.schoolmeals.model.BoardStatus;
import com.mindlinksw.schoolmeals.model.CommentItem;
import com.mindlinksw.schoolmeals.model.CommentListModel;
import com.mindlinksw.schoolmeals.model.ResponseModel;
import com.mindlinksw.schoolmeals.network.BoardAPI;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall;
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest;
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService;
import com.mindlinksw.schoolmeals.singleton.SessionSingleton;
import com.mindlinksw.schoolmeals.utils.Logger;
import com.mindlinksw.schoolmeals.utils.ObjectUtils;
import com.mindlinksw.schoolmeals.utils.SnackBarUtils;
import com.mindlinksw.schoolmeals.utils.Utils;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 게시물 상세조회
 */
public class DetailActivity extends BaseActivity implements Initialize, SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = DetailActivity.class.getName();

    private DetailActivityBinding mBinding;
    public ViewModel mViewModel;
    private int mBoardId;
    private BoardItem mBoardModel;

    private GenericRecyclerViewAdapter mAttachAdapter;
    private DetailCommentAdapter mCommentAdapter;
    private ArrayList<AttachItem> mAttachList;
    private ArrayList<CommentItem> mCommentList;

    private Handler mHandler;
    private boolean mIsDelete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.detail_activity);

        initDataBinding();
        initLayout(null);
        initVariable();
        initEventListener();

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void finish() {

        try {

            if (!mIsDelete) {

                // mBoardModel 에 데이터 세팅
                BoardData detail = mViewModel.mModel.get();
                if (!ObjectUtils.isEmpty(detail)) {
                    mBoardModel.setTit(detail.getTit());
                    mBoardModel.setContent(detail.getContent());
                    mBoardModel.setTimeAgo(detail.getTimeAgo());
                    // 좋아요
                    mBoardModel.setRecommendYn(detail.getRecommendYn());
                    mBoardModel.setRecommendCnt(detail.getRecommendCnt());
                    // 북마크
                    mBoardModel.setBkmrkSttus(detail.getBkmrkSttus());
                    // 댓글
                    mBoardModel.setCommentCnt(detail.getCommentCnt());

                    if (!ObjectUtils.isEmpty(mAttachList)) {
                        mBoardModel.setThumbnailUrl1(mAttachList.get(0).getFileUrl());
                        mBoardModel.setThumbnailCnt(mAttachList.size());
                    }

                    Intent result = new Intent();
                    result.setAction(String.valueOf(RequestConst.INTENT_DETAIL_UPDATE));
                    result.putExtra(BoardItem.class.getName(), mBoardModel);
                    setResult(Activity.RESULT_OK, result);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.finish();

    }

    @Override
    public void initDataBinding() {
        mViewModel = new ViewModel();
        mBinding.setViewModel(mViewModel);
    }

    @Override
    public void initLayout(@Nullable View view) {

        mBinding.refresh.setOnRefreshListener(this);
        mBinding.refresh.setRefreshing(true);

        // 이미지 리스트
        mAttachList = new ArrayList<>();
        mAttachAdapter = new GenericRecyclerViewAdapter(this, R.layout.detail_picture_item, mAttachList) {
            @Override
            public void onBindData(int position, Object model, Object dataBinding) {

                DetailPictureItemBinding binding = (DetailPictureItemBinding) dataBinding;
                ViewModel viewModel = new ViewModel();
                viewModel.mAttachModel.set((AttachItem) model);
                viewModel.mPosition.set(position);
                binding.setViewModel(viewModel);

            }
        };
        mBinding.rvAttach.setAdapter(mAttachAdapter);
        mBinding.rvAttach.setNestedScrollingEnabled(false);

        // 댓글 리스트
        mCommentList = new ArrayList<>();
        mCommentAdapter = new DetailCommentAdapter(this, getSupportFragmentManager(), mCommentList);
        mBinding.rvComment.setAdapter(mCommentAdapter);
        mBinding.rvComment.setNestedScrollingEnabled(false);

    }

    @Override
    public void initVariable() {

        try {

            Intent intent = getIntent();
            setIntent(intent);
            SchemeManager.Companion.run(this, intent);

            mBoardModel = (BoardItem) intent.getSerializableExtra(BoardItem.class.getName());

            if (mBoardModel != null) {
                // 데이터 불러옴
                mBoardId = mBoardModel.getBoardId();
                Logger.e(TAG, "mBoardId : " + mBoardId);
            } else {
                Snackbar.make(mBinding.getRoot(), getString(R.string.detail_read_fail), Snackbar.LENGTH_SHORT).show();
                finish();
                return;
            }

            mHandler = new Handler();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initEventListener() {
        recordAnalytics("DetailActivity", "DetailActivityView", "activity");
    }

    @Override
    public void onRefresh() {
        mBinding.refresh.setRefreshing(true);
        mViewModel.getDetail();
        mViewModel.getCommentList();
    }

    /**
     * View Model
     */
    public class ViewModel {

        public ObservableField<BoardData> mModel = new ObservableField<>();
        public ObservableField<AttachItem> mAttachModel = new ObservableField<>();
        public ObservableInt mPosition = new ObservableInt();
        public ObservableInt mLikeCount = new ObservableInt(); // 좋아요 갯수
        public ObservableBoolean mIsSelf = new ObservableBoolean(); // 자기 자신 체크
        public ObservableBoolean mIsLoading = new ObservableBoolean(); // 로딩중인지 체크

        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    finish();
                    break;

                case R.id.tv_comment_submit:
                    // 댓글 등록
                    if (SessionSingleton.getInstance(DetailActivity.this).isExist()) {
                        setComment(mBoardId, mBinding.etComment1dept.getText().toString());
                    } else {
                        SnackBarUtils.login(DetailActivity.this);
                    }
                    break;

                case R.id.iv_more:
                    DetailBottomSheet sheet = new DetailBottomSheet();
                    sheet.setOnDetailBottomSheetListener(new DetailBottomSheet.OnDetailBottomSheetListener() {
                        @Override
                        public void onDismiss(int code) {
                            switch (code) {
                                case DetailBottomSheet.CODE.MODIFY:
                                    // 수정
                                    Intent intent = new Intent(DetailActivity.this, WriteActivity.class);
                                    intent.putExtra(DataConst.TYPE, RequestConst.INTENT_MODIFY);
                                    intent.putExtra(BoardDetailModel.class.getName(), mModel.get());
                                    startActivityForResult(intent, RequestConst.INTENT_MODIFY);
                                    break;

                                case DetailBottomSheet.CODE.DELETE:
                                    // 삭제
                                    new AlertDialog(DetailActivity.this)
                                            .setMessage(getString(R.string.detail_delete_confirm))
                                            .setPositiveButton(getString(R.string.alert_yes), new AlertDialog.OnDialogClickListener() {
                                                @Override
                                                public void onClick() {
                                                    mViewModel.deleteBoard();
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

        public void onClick(View v, final BoardData model) {

            try {

                switch (v.getId()) {
                    case R.id.cb_like:
                        // 게시물 - 좋아요
                        if (SessionSingleton.getInstance(DetailActivity.this).isExist()) {
                            setLike((CheckBox) v, model);
                        } else {
                            CheckBox c = (CheckBox) v;
                            c.setChecked(false);
                            SnackBarUtils.login(DetailActivity.this);
                        }
                        break;

                    case R.id.cb_bookmark:
                        // 게시물 - 북마크
                        if (SessionSingleton.getInstance(DetailActivity.this).isExist()) {
                            setBookmark((CheckBox) v, model);
                        } else {
                            CheckBox c = (CheckBox) v;
                            c.setChecked(false);
                            SnackBarUtils.login(DetailActivity.this);
                        }
                        break;

                    case R.id.tv_prev: // 이전글

                        // 브릿지 이동
                        BoardItem prevItem = new BoardItem();
                        prevItem.setBoardId(model.getPrevBoardId());

                        Intent prevIntent = new Intent(DetailActivity.this, BridgeActivity.class);
                        prevIntent.putExtra("type", CategoryConst.BOARD);
                        prevIntent.putExtra(BoardItem.class.getName(), prevItem);
                        startActivity(prevIntent);

                        // 닫기
                        finish();
                        break;

                    case R.id.tv_next: // 다음글

                        // 브릿지 이동
                        BoardItem nextItem = new BoardItem();
                        nextItem.setBoardId(model.getNextBoardId());

                        Intent nextIntent = new Intent(DetailActivity.this, BridgeActivity.class);
                        nextIntent.putExtra("type", CategoryConst.BOARD);
                        nextIntent.putExtra(BoardItem.class.getName(), nextItem);
                        startActivity(nextIntent);

                        // 닫기
                        finish();
                        break;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 게시글 조회
         */
        public void getDetail() {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(DetailActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<BoardDetailModel> call = service.reqBoardDetail(mBoardId);

            RetrofitCall.enqueueWithRetry(call, new Callback<BoardDetailModel>() {

                @Override
                public void onResponse(Call<BoardDetailModel> call, Response<BoardDetailModel> response) {

                    try {

                        if (response.isSuccessful()) {

                            BoardDetailModel resData = (BoardDetailModel) response.body();

                            if (ObjectUtils.isEmpty(resData)) {
                                return;
                            }

                            // 삭제여부 판단
                            BoardStatus status = resData.getBoardStat();
                            if ("Y".equals(status.getDelYn())) {
                                // 삭제 메세지
                                String msg = status.getMsg();
                                if (ObjectUtils.isEmpty(msg)) {
                                    msg = getString(R.string.detail_delete);
                                }

                                new AlertDialog(DetailActivity.this)
                                        .setMessage(msg)
                                        .setPositiveButton(getString(R.string.alert_confirm), new AlertDialog.OnDialogClickListener() {
                                            @Override
                                            public void onClick() {
                                                finish();
                                            }
                                        })
                                        .show();

                                return;
                            }

                            mModel.set(resData.getBoardData());
                            mLikeCount.set(resData.getBoardData().getRecommendCnt());

                            // 자기가 등록한 글인지 확인
                            mIsSelf.set(SessionSingleton.getInstance(DetailActivity.this).isSelf(resData.getBoardData().getEsntlId()));

                            // 이미지
                            mAttachList.clear();
                            mAttachList.addAll(resData.getBoardData().getMediaList());
                            mAttachAdapter.notifyDataSetChanged();

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mBinding.refresh.setRefreshing(false);

                }

                @Override
                public void onFailure(Call<BoardDetailModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }

        /**
         * 게시글 삭제
         */
        public void deleteBoard() {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(DetailActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<ResponseModel> call = service.reqBoardDelete(mBoardId);

            RetrofitCall.enqueueWithRetry(call, new Callback<ResponseModel>() {

                @Override
                public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            ResponseModel resData = (ResponseModel) response.body();

                            if (resData != null) {
                                Logger.e(TAG, resData.toString());
                                if ("success".equals(resData.getCode())) {
                                    mIsDelete = true;
                                    Intent result = new Intent();
                                    result.setAction(String.valueOf(RequestConst.INTENT_DETAIL_DELETE));
                                    result.putExtra(BoardItem.class.getName(), mBoardModel);
                                    setResult(Activity.RESULT_OK, result);
                                    finish();
                                } else {
                                    Snackbar.make(mBinding.getRoot(), resData.getError(), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<ResponseModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                }
            });
        }

        /**
         * 댓글 조회
         */
        public void getCommentList() {

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(DetailActivity.this, SchoolMealsService.class, HostConst.apiHost());

            Call<CommentListModel> call = service.reqCommentList(
                    mBoardId,
                    1,
                    1000,
                    1000
            );

            RetrofitCall.enqueueWithRetry(call, new Callback<CommentListModel>() {

                @Override
                public void onResponse(Call<CommentListModel> call, Response<CommentListModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            CommentListModel resData = (CommentListModel) response.body();

                            if (resData != null) {
                                Logger.e(TAG, resData.toString());
                                mCommentList.clear();

                                ArrayList<CommentItem> list = new ArrayList<>();

                                for (CommentItem depth1 : resData.getList()) {

                                    if ("Y".equals(depth1.getDelYn())) {
                                        depth1.setDepth(98);
                                    }
                                    list.add(depth1);

                                    if (depth1.getList() != null) {

                                        // 2depth 삽입
                                        for (CommentItem depth2 : depth1.getList()) {

                                            if ("Y".equals(depth2.getDelYn())) {
                                                depth2.setDepth(98);
                                            }

                                            depth2.setParentId(depth1.getCommentId());
                                            list.add(depth2);
                                        }

                                        // 3개일 경우 댓글 더보기 생성
                                        if (depth1.getReplyCnt() > 3) {
                                            CommentItem more = new CommentItem(
                                                    depth1.getCommentId(),
                                                    depth1.getCommentId(),
                                                    99,
                                                    depth1.getReplyCnt() - 3,
                                                    depth1.getNckNm(),
                                                    depth1.getContent(),
                                                    depth1.getRecommendCnt(),
                                                    depth1.getTimeAgo());
                                            list.add(more);
                                        }
                                    }
                                }

                                mCommentList.addAll(list);

                                mCommentAdapter.setBoardId(mBoardId);
                                mCommentAdapter.notifyDataSetChanged();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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
         * @param content
         */
        public void setComment(int boardId, String content) {

            Logger.e(TAG, "content : " + content);

            if (ObjectUtils.isEmpty(content)) {
                Snackbar.make(mBinding.getRoot(), getString(R.string.detail_comment_empty), Snackbar.LENGTH_SHORT).show();
                return;
            }

            content = Utils.getMultiLineDisable(content, "\n\n");

            mIsLoading.set(true);

            SchoolMealsService service = RetrofitRequest.createRetrofitJSONService(DetailActivity.this, SchoolMealsService.class, HostConst.apiHost());
            Call<BoardInsertModel> call = service.reqCommentCreate(
                    boardId,
                    content);

            RetrofitCall.enqueueWithRetry(call, new Callback<BoardInsertModel>() {

                @Override
                public void onResponse(Call<BoardInsertModel> call, Response<BoardInsertModel> response) {

                    try {

                        if (response.isSuccessful()) {
                            BoardInsertModel resData = (BoardInsertModel) response.body();

                            if (resData != null) {
                                Logger.e(TAG, resData.toString());
                                if ("success".equals(resData.getCode())) {
                                    mBinding.etComment1dept.setText(null);
                                    onRefresh();

                                    // 키보드 내리기
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Utils.setKeyboardShowHide(DetailActivity.this, mBinding.getRoot(), false);
                                        }
                                    });

                                    // 스크롤 아래로
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mBinding.scroll.fullScroll(View.FOCUS_DOWN);
                                        }
                                    }, 300);

                                } else {
                                    Snackbar.make(mBinding.getRoot(), resData.getError(), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mIsLoading.set(false);

                }

                @Override
                public void onFailure(Call<BoardInsertModel> call, Throwable t) {
                    Logger.e(TAG, t.getMessage());
                    mIsLoading.set(false);
                }
            });
        }

        /**
         * 좋아요
         */
        public void setLike(final CheckBox checkBox, final BoardData model) {

            BoardAPI.setLike(DetailActivity.this, checkBox.isChecked(), model.getBoardId(), new OnResponseListener() {
                @Override
                public void onSuccess() {
                    int count = mLikeCount.get();
                    if (checkBox.isChecked()) {
                        count = count + 1;
                    } else {
                        if (count > 0) {
                            count = count - 1;
                        }
                    }

                    model.setRecommendCnt(count);
                    model.setRecommendYn(checkBox.isChecked() ? "Y" : "N");
                    mModel.set(model);

                    mLikeCount.set(count);
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
         * 북마크
         */
        public void setBookmark(final CheckBox checkBox, final BoardData model) {

            BoardAPI.setBookmark(DetailActivity.this, checkBox.isChecked(), model.getBoardId(), new OnResponseListener() {
                @Override
                public void onSuccess() {
                    model.setBkmrkSttus(checkBox.isChecked() ? "Y" : "N");
                    mModel.set(model);
                }

                @Override
                public void onFail(String error) {

                    checkBox.setChecked(!checkBox.isChecked());

                    if (!ObjectUtils.isEmpty(error)) {
                        Snackbar.make(mBinding.getRoot(), error, Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
