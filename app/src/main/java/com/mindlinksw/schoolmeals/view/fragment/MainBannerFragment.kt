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
import com.mindlinksw.schoolmeals.databinding.MainItemBannerItemBinding
import com.mindlinksw.schoolmeals.interfaces.Initialize
import com.mindlinksw.schoolmeals.manager.SchemeManager
import com.mindlinksw.schoolmeals.model.BannerItem

class MainBannerFragment : Fragment(), Initialize {

    private val TAG = MainBannerFragment::class.java.name

    private var mBinding: MainItemBannerItemBinding? = null
    private val mViewModel: ViewModel = ViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater,
            R.layout.main_item_banner_item,
            container,
            false)
        return mBinding!!.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        initDataBinding()
        initLayout(view)
        initVariable()
        initEventListener()

    }

    override fun initDataBinding() {
        mBinding!!.viewModel = mViewModel
    }

    override fun initLayout(view: View?) {

    }

    override fun initVariable() {

        try {
            if (arguments != null) {
                mViewModel.mModel.set(arguments!!.getSerializable(BannerItem::class.java.name) as BannerItem)
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun initEventListener() {

    }

    inner class ViewModel {

        val mModel: ObservableField<BannerItem> = ObservableField()

        fun onClick(
            v: View,
            model: BannerItem
        ) {
            when (v.id) {
                R.id.iv_banner -> {
                    SchemeManager.run(activity as Activity, Uri.parse(model.adLink))
                }
            }
        }

    }
}