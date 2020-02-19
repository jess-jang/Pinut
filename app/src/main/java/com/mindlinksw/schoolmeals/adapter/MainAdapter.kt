package com.mindlinksw.schoolmeals.adapter

/**
 * Created by N16326 on 2018. 9. 4..
 */

import android.app.Activity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mindlinksw.schoolmeals.BR
import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.consts.Style
import com.mindlinksw.schoolmeals.databinding.MainItemBannerBinding
import com.mindlinksw.schoolmeals.databinding.MainItemBoardGeneralBinding
import com.mindlinksw.schoolmeals.databinding.MainItemMealsBinding
import com.mindlinksw.schoolmeals.model.BoardItem
import com.mindlinksw.schoolmeals.viewholder.BannerViewHolder
import com.mindlinksw.schoolmeals.viewholder.BoardViewHolder
import com.mindlinksw.schoolmeals.viewholder.DateMealsViewHolder
import com.mocoplex.adlib.AdlibManager
import java.util.*


/**
 * 메인 어뎁터
 */
class MainAdapter constructor(
        @NonNull private val mActivity: Activity,
        @NonNull private val mList: ArrayList<BoardItem>?,
        @NonNull private val mFragmentManager: FragmentManager
)
    : androidx.recyclerview.widget.RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    private val TAG = MainAdapter::class.java.name
    private var mAdlibManager: AdlibManager? = null


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
                Style.MAIN.BANNER ->
                    layoutId = R.layout.main_item_banner
                Style.MAIN.MEALS ->
                    layoutId = R.layout.main_item_meals
                Style.MAIN.BORAD ->
                    layoutId = R.layout.main_item_board_general
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

                Style.MAIN.BANNER -> holder.banner!!.onBindView()
                Style.MAIN.MEALS -> holder.dateMeals!!.onBindView()
                Style.MAIN.BORAD -> holder.board!!.onBindView(position, item!!)


                else -> {
                    // 그냥 바인딩 시켜줘도 되는 친구들
                    val viewModel = ViewModel()
                    viewModel.mModel = item
                    holder.binding!!.setVariable(BR.viewModel, viewModel)
                }
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

        var banner: BannerViewHolder? = null // 상단 배너
        var dateMeals: DateMealsViewHolder? = null // 급식
        var board: BoardViewHolder? = null


        init {

            binding = DataBindingUtil.bind(mItemView!!)

            when (viewType) {
                Style.MAIN.BANNER -> // 상단 배너
                    banner = BannerViewHolder(mActivity, binding as MainItemBannerBinding, mFragmentManager)
                Style.MAIN.MEALS ->  // 식단
                    dateMeals = DateMealsViewHolder(mActivity, binding as MainItemMealsBinding)
                Style.MAIN.BORAD -> // 게시판
                    board = BoardViewHolder(mActivity, binding as MainItemBoardGeneralBinding, mFragmentManager, this@MainAdapter)
            }
        }
    }

    inner class ViewModel {

        public var mModel: BoardItem? = null

    }
}
