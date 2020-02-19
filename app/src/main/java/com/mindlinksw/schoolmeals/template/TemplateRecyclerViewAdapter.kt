package com.mindlinksw.schoolmeals.template

import android.app.Activity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.model.MainModel
import java.util.ArrayList

/**
 * 메인 어뎁터
 */
class TemplateRecyclerViewAdapter(private val mActivity: Activity, private val mList: ArrayList<MainModel>?) : androidx.recyclerview.widget.RecyclerView.Adapter<TemplateRecyclerViewAdapter.ViewHolder>() {

    private val TAG = TemplateRecyclerViewAdapter::class.java.name

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(mActivity), R.layout.template_item, parent, false).root
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        private var binding: ViewDataBinding? = null

        init {
            binding = DataBindingUtil.bind(itemView)
        }
    }
}