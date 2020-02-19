package com.mindlinksw.schoolmeals.viewholder

import android.app.Activity
import android.content.Intent
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.adapter.VideoAdapter
import com.mindlinksw.schoolmeals.consts.RequestConst
import com.mindlinksw.schoolmeals.databinding.VideoItemBoardGeneralBinding
import com.mindlinksw.schoolmeals.interfaces.Initialize
import com.mindlinksw.schoolmeals.interfaces.OnResponseListener
import com.mindlinksw.schoolmeals.model.BoardItem
import com.mindlinksw.schoolmeals.network.BoardAPI
import com.mindlinksw.schoolmeals.singleton.SessionSingleton
import com.mindlinksw.schoolmeals.utils.Logger
import com.mindlinksw.schoolmeals.utils.ObjectUtils
import com.mindlinksw.schoolmeals.utils.SnackBarUtils
import com.mindlinksw.schoolmeals.view.activity.VideoDetailActivity


class VideoBoardViewHolder(private val mActivity: Activity,
                           private val mBinding: VideoItemBoardGeneralBinding,
                           private val mFragmentManager: FragmentManager,
                           private val mAdapter: VideoAdapter)
    : androidx.recyclerview.widget.RecyclerView.ViewHolder(mBinding.root), Initialize {

    public val TAG = VideoBoardViewHolder::class.java.name
    private val mView: View = mBinding.root

    init {

        initDataBinding()
        initLayout(mView)
        initVariable()
        initEventListener()
    }

    override fun initDataBinding() {

    }

    override fun initLayout(view: View?) {

    }

    override fun initVariable() {

    }

    override fun initEventListener() {

    }

    fun onBindView(position: Int, item: BoardItem) {

        try {

            val viewModel = ViewModel()

            mBinding.viewModel = viewModel
            viewModel.mModel = item
            viewModel.mPosition = position

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ViewModel {

        var mModel: BoardItem? = null
        var mPosition: Int = 0

        fun onClick(v: View, model: BoardItem) {
            when (v.id) {
                R.id.ll_board, R.id.ll_comment -> {
                    // 게시글 상세로 이동

                    // 현재 list position 삽입
                    model.position = mPosition

                    val intent = Intent(mActivity, VideoDetailActivity::class.java)
                    intent.putExtra(BoardItem::class.java.name, model)
                    mActivity.startActivityForResult(intent, RequestConst.INTENT_DETAIL)

                }
            }
        }

        fun onClick(v: View, position: Int, model: BoardItem) {
            when (v.id) {

                R.id.ll_like -> {
                    if (SessionSingleton.getInstance(mActivity).isExist) {
                        // 좋아요
                        setLike(v, position, model)
                    } else {
                        SnackBarUtils.login(mActivity)
                    }
                }

                R.id.ll_bookmark -> {
                    if (SessionSingleton.getInstance(mActivity).isExist) {
                        // 북마크
                        setBookMark(v, position, model)
                    } else {
                        SnackBarUtils.login(mActivity)
                    }
                }
            }
        }

        /**
         * 북마크
         */
        private fun setBookMark(v: View, position: Int, model: BoardItem) {

            v.isClickable = false

            val isDelete = model.bkmrkSttus == "Y"
            Logger.e(TAG, "isDelete : $isDelete")

            BoardAPI.setBookmark(mActivity, !isDelete, model.boardId, object : OnResponseListener {
                override fun onSuccess() {
                    // 상태값 변경
                    model.bkmrkSttus = if (isDelete) "N" else "Y"
                    mAdapter.notifyItemChanged(position)

                    v.isClickable = true
                }

                override fun onFail(error: String?) {
                    if (!ObjectUtils.isEmpty(error)) {
                        Snackbar.make(mBinding.root, error!!, Snackbar.LENGTH_SHORT).show()
                    }
                }
            })
        }

        /**
         * 좋아요
         */
        private fun setLike(v: View, position: Int, model: BoardItem) {

            v.isClickable = false

            val isDelete = model.recommendYn == "Y"
            Logger.e(TAG, "isDelete : $isDelete")

            BoardAPI.setLike(mActivity, !isDelete, model.boardId, object : OnResponseListener {
                override fun onSuccess() {

                    // 상태값 변경
                    if (isDelete) {
                        model.recommendYn = "N"

                        if (model.recommendCnt > 0) {
                            model.recommendCnt = model.recommendCnt - 1
                        }
                    } else {
                        model.recommendYn = "Y"
                        model.recommendCnt = model.recommendCnt + 1
                    }

                    mModel = model
                    mAdapter.notifyItemChanged(position)

                    v.isClickable = true

                }

                override fun onFail(error: String?) {
                    if (!ObjectUtils.isEmpty(error)) {
                        Snackbar.make(mBinding.root, error!!, Snackbar.LENGTH_SHORT).show()
                    }
                }
            })
        }

    }

}
