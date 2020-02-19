package com.mindlinksw.schoolmeals.adapter

import android.app.Activity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.consts.Style
import com.mindlinksw.schoolmeals.databinding.VideoItemBoardGeneralBinding
import com.mindlinksw.schoolmeals.model.BoardItem
import com.mindlinksw.schoolmeals.viewholder.VideoBoardViewHolder
import java.util.*


/**
 * 비디오 리스트
 */
class VideoAdapter constructor(
        @NonNull private val mActivity: Activity,
        @NonNull private val mList: ArrayList<BoardItem>?,
        @NonNull private val mFragmentManager: FragmentManager
)
    : androidx.recyclerview.widget.RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    private val TAG = VideoAdapter::class.java.name

    init {
        setHasStableIds(true)
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        val item: BoardItem? = mList?.get(position)
        return item!!.type
    }

    override fun getItemId(position: Int): Long {
        return mList?.get(position)!!.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var layoutId: Int = R.layout.common_empty

        try {

            when (viewType) {
                Style.VIDEO.BORAD ->
                    layoutId = R.layout.video_item_board_general
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val view = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(mActivity), layoutId, parent, false).root
        return ViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {

            val item: BoardItem? = mList?.get(position)

            when (getItemViewType(position)) {

                Style.VIDEO.BORAD ->
                    holder.board!!.onBindView(position, item!!)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    inner class ViewHolder constructor(
            private val mItemView: View?,
            private val viewType: Int?)
        : androidx.recyclerview.widget.RecyclerView.ViewHolder(mItemView!!) {

        var binding: ViewDataBinding? = null

        var board: VideoBoardViewHolder? = null

        init {

            binding = DataBindingUtil.bind(mItemView!!)

            when (viewType) {
                Style.VIDEO.BORAD -> // 게시판
                    board = VideoBoardViewHolder(mActivity, binding as VideoItemBoardGeneralBinding, mFragmentManager, this@VideoAdapter)
            }

        }
    }

    inner class ViewModel {

    }
}
