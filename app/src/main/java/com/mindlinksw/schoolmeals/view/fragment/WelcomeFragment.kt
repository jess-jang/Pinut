package com.mindlinksw.schoolmeals.view.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.databinding.WelcomeItemBinding
import com.mindlinksw.schoolmeals.manager.SchemeManager
import com.mindlinksw.schoolmeals.model.WelcomeItem
import com.mindlinksw.schoolmeals.utils.tryCatch

class WelcomeFragment : Fragment() {

    private val TAG = WelcomeFragment::class.java.name

    private lateinit var mBinding: WelcomeItemBinding
    private var mViewModel: ViewModel = ViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.welcome_item, container, false)
        return mBinding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        initDataBinding()
        initVariable()
    }

    fun initDataBinding() {
        mBinding.viewModel = mViewModel
    }

    fun initVariable() {
        tryCatch {
            if (arguments != null) {
                mViewModel.mModel.set(arguments!!.getSerializable(WelcomeItem::class.java.name) as WelcomeItem)
            }
        }
    }

    inner class ViewModel {

        val mModel: ObservableField<WelcomeItem> = ObservableField()

        fun onClick(
            v: View,
            model: WelcomeItem
        ) {
            when (v.id) {
                R.id.iv_banner -> {
                    SchemeManager.run(activity as Activity, Uri.parse(model.adLink))
                    activity!!.finish()
                }
            }
        }
    }
}