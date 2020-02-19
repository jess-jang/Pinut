package com.mindlinksw.schoolmeals.view.activity

import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.consts.CategoryConst
import com.mindlinksw.schoolmeals.databinding.*
import com.mindlinksw.schoolmeals.interfaces.Initialize
import com.mindlinksw.schoolmeals.model.BoardItem

class BridgeActivity : AppCompatActivity(), Initialize {

    // ViewModel, DataBinding
    private var mBinding: TemplateActivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.template_activity)

        initDataBinding()
        initLayout(null)
        initVariable()
        initEventListener()

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }

    override fun initDataBinding() {

    }

    override fun initLayout(view: View?) {

    }

    override fun initVariable() {

        try {


            val intent = intent
            setIntent(intent)

            val type = intent.getStringExtra("type")

            var i: Intent? = null
            when (type) {
                CategoryConst.BOARD -> {// 글
                    i = Intent(this@BridgeActivity, DetailActivity::class.java)
                    i.putExtra(BoardItem::class.java.name, intent.getSerializableExtra(BoardItem::class.java.name) as BoardItem)
                }

                CategoryConst.VIDEO -> { // 비디오
                    i = Intent(this@BridgeActivity, VideoDetailActivity::class.java)
                    i.putExtra(BoardItem::class.java.name, intent.getSerializableExtra(BoardItem::class.java.name) as BoardItem)
                }
            }

            startActivity(i)

            // 닫기
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun initEventListener() {

    }
}