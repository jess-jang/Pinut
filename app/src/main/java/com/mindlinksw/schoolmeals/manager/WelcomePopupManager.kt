package com.mindlinksw.schoolmeals.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.mindlinksw.schoolmeals.consts.DataConst
import com.mindlinksw.schoolmeals.consts.HostConst
import com.mindlinksw.schoolmeals.consts.RequestConst
import com.mindlinksw.schoolmeals.consts.SchemeConst
import com.mindlinksw.schoolmeals.model.BoardItem
import com.mindlinksw.schoolmeals.model.CommentItem
import com.mindlinksw.schoolmeals.model.WelcomeItem
import com.mindlinksw.schoolmeals.model.WelcomeModel
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest.createRetrofitJSONService
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService
import com.mindlinksw.schoolmeals.singleton.SessionSingleton
import com.mindlinksw.schoolmeals.utils.*
import com.mindlinksw.schoolmeals.view.activity.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class WelcomePopupManager {

    companion object {

        private val TAG: String = WelcomePopupManager::class.java.name

        @SuppressLint("CheckResult")
        fun run(
            activity: Activity
        ) {

            // 하루 안보는 조건
            if (SharedPreferencesUtils.read(activity,
                    DataConst.WELCOME_DISMISS_DAY, 0)
                == Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                return
            }

            val service = createRetrofitJSONService(activity,
                SchoolMealsService::class.java,
                HostConst.apiHost())

            service.reqWelcomeList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.list.isNotEmpty()) {
                        val intent = Intent(activity, WelcomePopupActivity::class.java)
                        intent.putExtra(WelcomeModel::class.java.name, it.list)
                        activity.startActivity(intent)
                    }
                }) {
                    Logger.e(TAG, it.message)
                }
        }
    }

}