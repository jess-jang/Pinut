package com.mindlinksw.schoolmeals.view.activity

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.adapter.GenericViewPagerAdapter
import com.mindlinksw.schoolmeals.consts.DataConst
import com.mindlinksw.schoolmeals.model.WelcomeItem
import com.mindlinksw.schoolmeals.model.WelcomeModel
import com.mindlinksw.schoolmeals.utils.ActivityTransitions
import com.mindlinksw.schoolmeals.utils.SharedPreferencesUtils
import com.mindlinksw.schoolmeals.utils.tryCatch
import com.mindlinksw.schoolmeals.view.fragment.WelcomeFragment
import kotlinx.android.synthetic.main.welcome_activity.*
import java.util.*
import kotlin.collections.ArrayList

@Suppress("UNCHECKED_CAST")
class WelcomePopupActivity : BaseActivity(), View.OnClickListener {

    private var mList = ArrayList<WelcomeItem>()
    private var mDotList = ArrayList<View>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityTransition(ActivityTransitions.Companion.Type.FADE_IN_OUT)
        setContentView(R.layout.welcome_activity)

        initData()
        initLayout()
        initEventListener()

    }

    public override fun finish() {
        super.finish()
    }

    private fun initData() {
        tryCatch {
            mList = intent.getSerializableExtra(WelcomeModel::class.java.name) as ArrayList<WelcomeItem>
        }
    }

    /**
     * 레이아웃 구성
     */
    private fun initLayout() {

        // 클릭 이벤트
        tv_today_dismiss.setOnClickListener(this)
        tv_close.setOnClickListener(this)

        // viewpager
        val adapter = GenericViewPagerAdapter(supportFragmentManager)
        mList.forEachIndexed { index, item ->
            val bundle = Bundle()
            bundle.putSerializable(DataConst.POSITION, index)
            bundle.putSerializable(WelcomeItem::class.java.name, item)

            val fragment = WelcomeFragment()
            fragment.arguments = bundle

            adapter.addFragment(fragment)
        }

        vp_welcome.adapter = adapter

        setDotIcon()
        setDotEnable()
    }

    private fun initEventListener() {
        vp_welcome.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                setDotEnable(position)
            }

        })
    }

    /**
     * 닷 아이콘 생성
     */
    private fun setDotIcon() {

        if (mList.size > 1) {

            // icon size
            val size: Int = resources.getDimensionPixelSize(R.dimen.banner_dot)
            val margin: Int = resources.getDimensionPixelSize(R.dimen.banner_dot_margin)

            mList.forEach { _ ->

                // imageView 속성
                val view = View(this)
                view.setBackgroundResource(R.drawable.dot_banner_off)

                val params = LinearLayout.LayoutParams(size, size)
                params.rightMargin = margin

                // addView
                ll_count.addView(view, params)
                ll_count.requestLayout()

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
    private fun setDotEnable(position: Int = 0) {

        try {
            mDotList.forEachIndexed { index, view ->
                view.setBackgroundResource(R.drawable.dot_banner_off)
                if (index == position) {
                    view.setBackgroundResource(R.drawable.dot_banner_on)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_today_dismiss -> {
                SharedPreferencesUtils.save(this,
                        DataConst.WELCOME_DISMISS_DAY,
                        Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
            }

            R.id.tv_close -> {

            }
        }

        finish()
    }
}