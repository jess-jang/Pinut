package com.mindlinksw.schoolmeals.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.adapter.MainAdapter
import com.mindlinksw.schoolmeals.consts.DataConst
import com.mindlinksw.schoolmeals.consts.HostConst
import com.mindlinksw.schoolmeals.consts.RequestConst
import com.mindlinksw.schoolmeals.consts.Style
import com.mindlinksw.schoolmeals.databinding.MainActivityBinding
import com.mindlinksw.schoolmeals.interfaces.DistributeListener
import com.mindlinksw.schoolmeals.interfaces.Initialize
import com.mindlinksw.schoolmeals.manager.SchemeManager
import com.mindlinksw.schoolmeals.manager.WelcomePopupManager
import com.mindlinksw.schoolmeals.model.BoardItem
import com.mindlinksw.schoolmeals.model.BoardModel
import com.mindlinksw.schoolmeals.model.NotificationStatusModel
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService
import com.mindlinksw.schoolmeals.singleton.SessionSingleton
import com.mindlinksw.schoolmeals.utils.*
import com.mindlinksw.schoolmeals.viewholder.DateMealsViewHolder
import kotlinx.android.synthetic.main.main_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : BaseActivity(), Initialize, SwipeRefreshLayout.OnRefreshListener {

    private val TAG: String = MainActivity::class.java.name

    // ViewModel, DataBinding
    private lateinit var mBinding: MainActivityBinding
    private val mViewModel = ViewModel()

    // Adapter
    private lateinit var mAdapter: MainAdapter
    private var mLayoutManager: LinearLayoutManager = LinearLayoutManager(this)

    // Variable
    private val mBackPressHandler: BackPressCloseHandler = BackPressCloseHandler(this)
    private var mList: ArrayList<BoardItem> = ArrayList()
    private var mCurPageNo: Int = 0 // 페이지 넘버
    private val mPageSize: Int = 10 // 가져올 아이템 갯수
    private var mIsLoading: Boolean = false
    private var mIsLast: Boolean = false // 마지막 페이지 여부
    private var mLastItemVisibleFlag: Boolean = false
    private var mNeedRefresh: Boolean = false
    private var mShareText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        WelcomePopupManager.run(this)
        initDataBinding()
        initLayout(null)
        initVariable()
        initEventListener()
        tracker()
        initIntent()
        onRefresh()

    }

    override fun onBackPressed() {
        // 뒤로가기 핸들러
        mBackPressHandler.onBackPressed()
    }

    private fun tracker() {
        if (Utils.isTest()) {
            // App Center
            Distribute.setListener(DistributeListener())
            Distribute.setEnabled(true)
            AppCenter.start(application, getString(R.string.hockey_app_id),
                    Analytics::class.java, Crashes::class.java, Distribute::class.java)
        }
    }

    private fun initIntent() {
        tryCatch {
            val intent = intent
            setIntent(Intent())
            // 공유 받기
            mShareText = intent.getStringExtra(Intent.EXTRA_TEXT)
            // 스킴 실행
            SchemeManager.run(this, intent)
        }
    }

    /**
     * 새로고침
     */
    override fun onRefresh() {
        // 데이터 초기화
        mCurPageNo = 1
        getData(true)
    }

    override fun onDestroy() {
        DateMealsViewHolder.isRefresh = true
        super.onDestroy()
    }

    override fun initDataBinding() {
        mBinding.run {
            viewModel = mViewModel
            icTabbar.viewModel = mViewModel // tabbar layout
            icHeader.viewModel = mViewModel // header layout
        }
    }

    override fun initLayout(view: View?) {

        // 리스트
        rv_main.layoutManager = mLayoutManager
        rv_main.itemAnimator = null

        // 리프레시
        refresh.setOnRefreshListener(this)

    }

    override fun initVariable() {

        // adapter
        mAdapter = MainAdapter(this, mList, supportFragmentManager)
        rv_main.adapter = mAdapter

        mList.add(BoardItem(Style.MAIN.MEALS))
        mList.add(BoardItem(Style.MAIN.BANNER))

        DateMealsViewHolder.isRefresh = true

        // 알림여부
        mViewModel.getNotificationStatus()

    }

    override fun initEventListener() {

        // 리스트 이벤트
        rv_main.addOnScrollListener(object :
                androidx.recyclerview.widget.RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE && mLastItemVisibleFlag) {
                    if (!mIsLoading && !mIsLast) {
                        getData(false)
                    }
                }
            }

            override fun onScrolled(
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    dx: Int,
                    dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)

                val y = recyclerView.computeVerticalScrollOffset()
                val visibleItemCount = mLayoutManager.childCount
                val totalItemCount = mLayoutManager.itemCount
                val firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition()

                if (!mIsLoading) {

                    // 새로고침
                    if (y == 0 && mNeedRefresh) {
                        mNeedRefresh = false
                        onRefresh()
                    }

                    mLastItemVisibleFlag = if (dy > 0) {
                        //현재 화면에 보이는 첫번째 리스트 아이템의 번호(firstVisibleItem) + 현재 화면에 보이는 리스트 아이템의 갯수(visibleItemCount)가 리스트 전체의 갯수(totalItemCount) -1 보다 크거나 같을때
                        totalItemCount > 0 && firstVisibleItem + visibleItemCount >= totalItemCount
                    } else {
                        // 전체 카운트랑 화면에 보여지는 카운트랑 같을때
                        visibleItemCount == totalItemCount
                    }
                }
            }
        })
    }


    private fun getData(isRefresh: Boolean) {

        mIsLoading = true
        refresh.isRefreshing = true

        val service = RetrofitRequest.createRetrofitJSONService(this,
                SchoolMealsService::class.java,
                HostConst.apiHost())
        val call = service.reqBoard(
                "BBS_00000001",
                mCurPageNo,
                mPageSize,
                mPageSize)

        RetrofitCall.enqueueWithRetry(call, object : Callback<BoardModel> {

            override fun onResponse(
                    call: Call<BoardModel>,
                    response: Response<BoardModel>
            ) {

                try {

                    if (isRefresh) {
                        mList.clear()
                        mList.add(BoardItem(Style.MAIN.MEALS))
                        mList.add(BoardItem(Style.MAIN.BANNER))
                    }

                    if (response.isSuccessful) {
                        val resData = response.body()

                        if (resData != null) {
                            mList.addAll(resData.list)

                            // 마지막페이지인지 체크
                            mIsLast = resData.curPage == resData.totalPage

                            mCurPageNo++
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mAdapter.notifyItemRangeChanged(0, mList.size)

                refresh.isRefreshing = false
                mIsLoading = false

            }

            override fun onFailure(
                    call: Call<BoardModel>,
                    t: Throwable
            ) {
                Logger.e(TAG, "onFailure : " + t.message)
                mIsLoading = false
            }
        })

    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {

        try {

            if (resultCode != Activity.RESULT_OK) {
                return
            }

            when (requestCode) {
                
                RequestConst.REFRESH -> {
                    // 새로고침
                    onRefresh()
                }

                RequestConst.LOGIN_REDIRECT -> {
                    // 로그인 후 리다이렉트 처리
                    onRefresh()
                    mViewModel.onClick(mRedirectView)
                }

                RequestConst.INTENT_MYPAGE -> {
                    mAdapter.notifyItemChanged(0)
                }

                RequestConst.INTENT_DETAIL -> {
                    if (data != null) {
                        val action = data.action
                        val boardModel = data.getSerializableExtra(BoardItem::class.java.name) as? BoardItem

                        if (ObjectUtils.isEmpty(boardModel)) {
                            return
                        }

                        val position: Int = boardModel!!.position
                        if (position > -1) {
                            if (action == (RequestConst.INTENT_DETAIL_DELETE).toString()) {
                                // 삭제
                                mList.removeAt(position)
                                mAdapter.notifyDataSetChanged()
                            } else if (action == (RequestConst.INTENT_DETAIL_UPDATE).toString()) {
                                // 아이템 업데이트
                                mList[position] = boardModel
                                mAdapter.notifyItemChanged(position)
                            }
                        }
                    }
                }
                
                RequestConst.LOGIN_RESULT_NOTIFICATION -> {
                    // 알람리스트
                    onRefresh()
                    startActivityForResult(Intent(this@MainActivity, LoginActivity::class.java),
                            RequestConst.LOGIN_RESULT_NOTIFICATION)
                    // 읽음처리
                    mViewModel.mNotificationStatus.set(false)
                }

                RequestConst.LOGIN_RESULT_WRITE -> {
                    // 글쓰기
                    onRefresh()
                    val intent = Intent(this@MainActivity, WriteActivity::class.java)
                    intent.putExtra(DataConst.TYPE, RequestConst.INTENT_WRITE)
                    intent.putExtra(Intent.EXTRA_TEXT, mShareText)
                    startActivityForResult(intent, RequestConst.REFRESH)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class ViewModel {

        val mNotificationStatus: ObservableBoolean = ObservableBoolean()

        fun onClick(v: View) {
            mRedirectView = v

            when (v.id) {
                /**
                 * header
                 */
                R.id.iv_logo -> {
                    // firebase analytics
                    recordAnalytics("iv_logo", "main_top_logo", "button")
                    rv_main.smoothScrollToPosition(0)
                    mNeedRefresh = true
                }

                R.id.rl_notification -> {
                    // firebase analytics
                    recordAnalytics("rl_notification", "notification_activity", "button")
                    // 알림리스트
                    if (SessionSingleton.getInstance(this@MainActivity).isExist) {
                        startActivity(Intent(this@MainActivity, NotificationActivity::class.java))
                        // 읽음처리
                        mNotificationStatus.set(false)
                    } else {
                        startActivityForResult(Intent(this@MainActivity, LoginActivity::class.java),
                                RequestConst.LOGIN_RESULT_NOTIFICATION)
                    }
                }

                /**
                 * tabbar
                 */
                R.id.iv_home -> {
                    // firebase analytics
                    recordAnalytics("iv_home", "tabbar_home", "button")
                    rv_main.smoothScrollToPosition(0)
                }

                R.id.iv_video -> {
                    // firebase analytics
                    recordAnalytics("iv_video", "tabbar_video", "button")
                    // 비디오
                    val intent = Intent(this@MainActivity, VideoActivity::class.java)
                    startActivity(intent)
                }

                R.id.iv_write -> {
                    // firebase analytics
                    recordAnalytics("iv_write", "tabbar_video", "button")
                    // 글쓰기
                    if (SessionSingleton.getInstance(this@MainActivity).isExist) {
                        val intent = Intent(this@MainActivity, WriteActivity::class.java)
                        intent.putExtra(DataConst.TYPE, RequestConst.INTENT_WRITE)
                        startActivityForResult(intent, RequestConst.REFRESH)
                    } else {
                        startActivityForResult(Intent(this@MainActivity, LoginActivity::class.java),
                                RequestConst.LOGIN_RESULT_WRITE)
                    }
                }

                R.id.iv_bookmark -> {
                    // firebase analytics
                    recordAnalytics("iv_bookmark", "tabbar_bookmark", "button")
                    // 북마크
                    if (SessionSingleton.getInstance(this@MainActivity).isExist) {
                        startActivityForResult(Intent(this@MainActivity,
                                BookmarkActivity::class.java), RequestConst.REFRESH)
                    } else {
                        startActivityForResult(Intent(this@MainActivity, LoginActivity::class.java),
                                RequestConst.LOGIN_REDIRECT)
                    }
                }

                R.id.iv_mypage -> {
                    // firebase analytics
                    recordAnalytics("iv_mypage", "tabbar_mypage", "button")
                    // 마이페이지
                    if (SessionSingleton.getInstance(this@MainActivity).isExist) {
                        startActivityForResult(Intent(this@MainActivity,
                                MyPageActivity::class.java), RequestConst.INTENT_MYPAGE)
                    } else {
                        startActivityForResult(Intent(this@MainActivity, LoginActivity::class.java),
                                RequestConst.LOGIN_REDIRECT)
                    }
                }
            }
        }

        /**
         * 알림 여부
         */
        public fun getNotificationStatus() {

            val service = RetrofitRequest.createRetrofitJSONService(this@MainActivity,
                    SchoolMealsService::class.java,
                    HostConst.apiHost())
            val call = service.reqNotificationStatus()

            RetrofitCall.enqueueWithRetry(call, object : Callback<NotificationStatusModel> {

                override fun onResponse(
                        call: Call<NotificationStatusModel>,
                        response: Response<NotificationStatusModel>
                ) {

                    try {

                        if (response.isSuccessful) {
                            val resData = response.body()

                            if (resData != null) {
                                mNotificationStatus.set(resData.alertYn == "Y")
                            }
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(
                        call: Call<NotificationStatusModel>,
                        t: Throwable
                ) {
                    Logger.e(TAG, "onFailure : " + t.message)
                }
            })
        }
    }
}
