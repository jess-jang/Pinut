package com.mindlinksw.schoolmeals.template

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.databinding.TemplateFragmentBinding
import com.mindlinksw.schoolmeals.interfaces.Initialize


class TemplateFragment : Fragment(), Initialize {

    private val TAG = TemplateFragment::class.java.name
    private var mBinding: TemplateFragmentBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.template_fragment, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDataBinding()
        initLayout(view)
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

}