package com.mindlinksw.schoolmeals.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.mindlinksw.schoolmeals.utils.ActivityTransitions

open class BaseActivity : AppCompatActivity() {

    private val TAG: String = BaseActivity::class.java.name

    // firebase analytics
    private lateinit var mFirebaseAnalytics: FirebaseAnalytics
    private var mActivityTransitions: ActivityTransitions.Companion.Type? = null
    public lateinit var mRedirectView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }

    public override fun onResume() {
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
    }

    public override fun onDestroy() {
        super.onDestroy()
    }

    override fun finish() {
        super.finish()

        // 액티비티 화면전환 여부
        if (mActivityTransitions != null) {
            ActivityTransitions.run(this, mActivityTransitions!!)
        }
    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {

    }

    public fun recordAnalytics(
            id: String,
            name: String,
            type: String
    ) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    /**
     * 액티비티 애니메이션
     * setContentView 이전에 실시
     *
     * @param type
     */
    public fun setActivityTransition(
            type: ActivityTransitions.Companion.Type?
    ) {
        if (type == null) {
            return
        }
        mActivityTransitions = type
        ActivityTransitions.run(this, type)

    }
}