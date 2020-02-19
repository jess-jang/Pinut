package com.mindlinksw.schoolmeals.template

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.databinding.TemplateActivityBinding
import com.mindlinksw.schoolmeals.interfaces.Initialize

//class TemplateActivity : AppCompatActivity(), Initialize {
//
//    // ViewModel, DataBinding
//    private var mBinding: TemplateActivityBinding? = null
//    private var mViewModel: TemplateViewModel? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        mBinding = DataBindingUtil.setContentView(this, R.layout.template_activity)
//
//        initDataBinding()
//        initLayout(null)
//        initVariable()
//        initEventListener()
//
//    }
//
//    override fun initDataBinding() {
//        // ViewModel
//        mViewModel = TemplateViewModel(this)
//        mBinding!!.viewModel = mViewModel
//    }
//
//    override fun initLayout(view: View?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun initVariable() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//
//    override fun initEventListener() {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}