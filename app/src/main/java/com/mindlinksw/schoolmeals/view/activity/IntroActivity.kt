package com.mindlinksw.schoolmeals.view.activity

import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.consts.HostConst
import com.mindlinksw.schoolmeals.databinding.IntroActivityBinding
import com.mindlinksw.schoolmeals.dialog.AlertDialog
import com.mindlinksw.schoolmeals.interfaces.OnLoginResponseListener
import com.mindlinksw.schoolmeals.model.LastUpdateVersionModel
import com.mindlinksw.schoolmeals.model.SessionModel
import com.mindlinksw.schoolmeals.network.FCMAPI
import com.mindlinksw.schoolmeals.network.LoginAPI
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitCall
import com.mindlinksw.schoolmeals.network.retrofit.RetrofitRequest
import com.mindlinksw.schoolmeals.network.retrofit.service.SchoolMealsService
import com.mindlinksw.schoolmeals.singleton.SessionSingleton
import com.mindlinksw.schoolmeals.utils.Logger
import com.mindlinksw.schoolmeals.utils.Utils
import io.fabric.sdk.android.Fabric
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class IntroActivity : AppCompatActivity() {

    private val TAG: String = IntroActivity::class.java.name

    // ViewModel, DataBinding
    private var mBinding: IntroActivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this@IntroActivity, R.layout.intro_activity)

        initIntro()

    }

    private fun initIntro() {

        // Hash
        Utils.getHashKey(this@IntroActivity)

        /**
         * Network 체크
         */
        if (!Utils.isNetworkConnected(this@IntroActivity)) {
            AlertDialog(this@IntroActivity)
                .setMessage(getString(R.string.intro_network))
                .setPositiveButton(getString(R.string.alert_yes)) {
                    finish()
                }
                .setAlertCancelable(false)
                .show()
            return
        }

        /**
         * 테스트모드 체크
         */
        if (Utils.isTest()) {
            Utils.getHashKey(this)
            Toast.makeText(this, "테스트 모드입니다.", Toast.LENGTH_SHORT).show()
        }

        /**
         * Start
         */
        initTracker()
        initStart()

    }

    private fun initTracker() {

        // FCM
        if (SessionSingleton.getInstance(this).isExist) {
            FCMAPI.registerToken(this, null)
        }

        // Fabric
        if (Utils.isTest()) {

        }
        else {
            // Fabric by Firebase
            Fabric.with(this, Crashlytics())
        }

    }

    private fun initStart() {

        userCheck()

    }

    /**
     * 사용자 체크
     */
    private fun userCheck() {

        try {

            if (!SessionSingleton.getInstance(this@IntroActivity).isExist) {
                appCheck()
                return
            }

            val socialType = SessionSingleton.getInstance(this@IntroActivity).select().userSocial
            val userId = SessionSingleton.getInstance(this@IntroActivity).select().userId
            val email = SessionSingleton.getInstance(this@IntroActivity).select().emailAdres


            LoginAPI.access(this@IntroActivity,
                socialType,
                userId,
                email,
                object : OnLoginResponseListener {

                    override fun onSuccess(session: SessionModel) {
                        appCheck()
                    }

                    override fun onFail(error: String) {

                        try {
                            LoginAPI.logout(this@IntroActivity)
                            appCheck()
                        }
                        catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                })
        }
        catch (e: Exception) {
            e.printStackTrace()
            appCheck()
        }
    }

    /**
     * 앱 체크
     */
    private fun appCheck() {

        val service: SchoolMealsService = RetrofitRequest.createRetrofitJSONService(this,
            SchoolMealsService::class.java,
            HostConst.apiHost())

        val call: Call<LastUpdateVersionModel> = service.reqLastUpdateVersion()
        RetrofitCall.enqueueWithRetry(call, object : Callback<LastUpdateVersionModel> {

            override fun onResponse(
                call: Call<LastUpdateVersionModel>,
                response: Response<LastUpdateVersionModel>
            ) {

                try {

                    if (response.isSuccessful) {
                        val resData: LastUpdateVersionModel = response.body() as LastUpdateVersionModel
                        Log.e(TAG, resData.toString())

                        when {

                            Utils.isVersionUpdated(resData.forciblyVersion) -> {
                                // Force Update
                                AlertDialog(this@IntroActivity)
                                    .setMessage(getString(R.string.intro_update_force))
                                    .setPositiveButton(getString(R.string.intro_update_force_yes)) {
                                        startActivity(Intent(Intent.ACTION_VIEW,
                                            Uri.parse("market://details?id=" + packageName)))
                                        finish()
                                    }
                                    .setNegativeButton(getString(R.string.intro_update_force_no)) { finish() }
                                    .setAlertCancelable(false)
                                    .show()
                            }

                            Utils.isVersionUpdated(resData.adviceVersion) -> {
                                // Recommend Update
                                AlertDialog(this@IntroActivity)
                                    .setMessage(getString(R.string.intro_update_force))
                                    .setPositiveButton(getString(R.string.intro_update_recommend)) {
                                        startActivity(Intent(Intent.ACTION_VIEW,
                                            Uri.parse("market://details?id=" + packageName)))
                                        finish()
                                    }
                                    .setNegativeButton(getString(R.string.intro_update_recommend_no)) { initIntent() }
                                    .setAlertCancelable(false)
                                    .show()
                            }

                            else -> {
                                // Not Update
                                initIntent()
                            }
                        }
                    }

                }
                catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, e.message)
                    initIntent()
                }

            }

            override fun onFailure(
                call: Call<LastUpdateVersionModel>,
                t: Throwable
            ) {
                Logger.e(TAG, t.message)
                initIntent()
            }
        })
    }

    private fun initIntent() {

        intent.setClass(this@IntroActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)

    }
}