package com.mindlinksw.schoolmeals.viewholder

import android.app.Activity
import androidx.databinding.ObservableInt
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.adapter.GenericViewPagerAdapter
import com.mindlinksw.schoolmeals.consts.DataConst
import com.mindlinksw.schoolmeals.consts.HostConst
import com.mindlinksw.schoolmeals.databinding.MainItemBannerBinding
import com.mindlinksw.schoolmeals.interfaces.Initialize
import com.mindlinksw.schoolmeals.model.*
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService
import com.mindlinksw.schoolmeals.utils.Logger
import com.mindlinksw.schoolmeals.view.fragment.MainBannerFragment
import kotlinx.android.synthetic.main.main_item_banner.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 메인 상단 배너
 */
class BannerViewHolder(
    private val mActivity: Activity,
    private val mBinding: MainItemBannerBinding,
    private val mFragmentManager: FragmentManager
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mBinding.root), Initialize {

    private val TAG: String = BannerViewHolder::class.java.name


    // ViewModel
    private val mView: View = mBinding.root
    private val mViewModel: ViewModel = ViewModel()
    private var mAdapter: GenericViewPagerAdapter? = null
    private val mList: ArrayList<BannerItem> = ArrayList()
    private val mDotList: ArrayList<View> = ArrayList()

    init {

        initDataBinding()
        initLayout(mView)
        initEventListener()
        initVariable()

    }

    override fun initDataBinding() {

    }

    override fun initLayout(view: View?) {

    }

    override fun initEventListener() {

    }

    override fun initVariable() {

        try {

            // getData
            mViewModel.getData()

        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun onBindView() {

        try {

            val viewModel = ViewModel()
            mBinding.viewModel = viewModel

        }
        catch (e: Exception) {
            e.printStackTrace()
        }

    }

    inner class ViewModel {

        val mPosition: ObservableInt = ObservableInt()

        fun onClick(v: View) {
            when (v.id) {

            }
        }

        /***
         * ViewPager 생성
         */
        fun setAdapter() {
            // ViewPager
            mActivity.runOnUiThread {

                mAdapter = GenericViewPagerAdapter(mFragmentManager)

                mList.forEachIndexed { index, item ->

                    val bundle = Bundle()
                    bundle.putSerializable(DataConst.POSITION, index)
                    bundle.putSerializable(BannerItem::class.java.name, item)

                    val fragment = MainBannerFragment()
                    fragment.arguments = bundle

                    mAdapter!!.addFragment(fragment)

                }
                mView.vp_banner.adapter = mAdapter

            }

        }

        /**
         * 닷 아이콘 생성
         */
        fun setDotIcon() {

            // dot list
            if (mDotList.size < 1) {

                // icon size
                val size: Int = mActivity.resources.getDimensionPixelSize(R.dimen.banner_dot)
                val margin: Int = mActivity.resources.getDimensionPixelSize(R.dimen.banner_dot_margin)

                for (i in 0..(mList.size - 1)) {

                    // imageView 속성
                    val view = View(mActivity)
                    view.setBackgroundResource(R.drawable.dot_banner_off)

                    val params = LinearLayout.LayoutParams(size, size)
                    params.rightMargin = margin

                    // addView
                    mView.ll_count.addView(view, params)
                    mView.ll_count.requestLayout()

                    mDotList.add(view)

                }
            }
        }

        /**
         * 닷 아이콘 On Off
         *
         * @param position - 현재 포시젼
         *
         */
        fun setDotEnable(position: Int = 0) {

            try {

                mDotList.forEachIndexed { index, view ->

                    view.setBackgroundResource(R.drawable.dot_banner_off)

                    if (index == position) {
                        view.setBackgroundResource(R.drawable.dot_banner_on)
                    }

                }

            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * 배너 리스트
         */
        fun getData() {

            if (mList.size > 0) {
                return
            }

            val service: SchoolMealsService = RetrofitRequest.createRetrofitJSONService(mActivity,
                SchoolMealsService::class.java,
                HostConst.apiHost())

            val call: Call<BannerListModel> = service.reqBannerList()
            RetrofitCall.enqueueWithRetry(call, object : Callback<BannerListModel> {

                override fun onResponse(
                    call: Call<BannerListModel>,
                    response: Response<BannerListModel>
                ) {

                    try {

                        if (response.isSuccessful) {
                            val resData: BannerListModel = response.body() as BannerListModel
                            Log.e(TAG, resData.toString())

                            mList.clear()
                            mList.addAll(resData.list)

                            // layout 생성
                            setAdapter()

                            if (mList.size > 1) {
                                setDotIcon()
                                setDotEnable()
                            }

                        }

                    }
                    catch (e: java.lang.Exception) {
                        e.printStackTrace()
                        Log.e(TAG, e.message)
                    }

                }

                override fun onFailure(
                    call: Call<BannerListModel>,
                    t: Throwable
                ) {
                    Logger.e(TAG, t.message)
                }
            })
        }

        /***
         * View Pager Listener
         */
        var mOnPageChangeListener: ViewPager.OnPageChangeListener = object :
            ViewPager.OnPageChangeListener {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageSelected(position: Int) {
                mPosition.set(position)
                mViewModel.setDotEnable(position)
            }
        }


    }

}
