package com.mindlinksw.schoolmeals.provider

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.RemoteViews
import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.consts.APIConst
import com.mindlinksw.schoolmeals.consts.HostConst
import com.mindlinksw.schoolmeals.consts.SchemeConst
import com.mindlinksw.schoolmeals.model.DateMealItem
import com.mindlinksw.schoolmeals.model.DateMealModel
import com.mindlinksw.schoolmeals.singleton.SessionSingleton
import com.mindlinksw.schoolmeals.utils.Logger
import com.mindlinksw.schoolmeals.utils.ObjectUtils
import com.mindlinksw.schoolmeals.utils.TextFormatUtils
import com.mindlinksw.schoolmeals.utils.Utils
import com.mindlinksw.schoolmeals.view.activity.IntroActivity
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

/**
 * Implementation of App Widget functionality.
 */
class WidgetDateMealProvider : AppWidgetProvider() {

    val TAG = WidgetDateMealProvider::class.java.name


//    onUpdate()는 위젯 갱신 주기에 따라 위젯을 갱신할때 호출됩니다
//
//    onEnabled()는 위젯이 처음 생성될때 호출되며, 동일한 위젯의 경우 처음 호출됩니다
//
//    onDisabled()는 위젯의 마지막 인스턴스가 제거될때 호출됩니다
//
//    onDeleted()는 위젯이 사용자에 의해 제거될때 호출됩니다

    /**
     * 위젯 Refresh 및 UI 선택 action 처리
     * @param context
     * @param intent
     */
    @Override
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val action = intent.action
        if (action != null) {

            when (action) {
                SchemeConst.WIDGET_POWER_SAVE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val intentPowerSave = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
                        intentPowerSave.data = Uri.parse("package:" + context.packageName)
                        context.startActivity(intentPowerSave)
                    }
                }

                SchemeConst.WIDGET_DATA_SAVE -> {
                    val intentDataSave = Intent(Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS)
                    intentDataSave.data = Uri.parse("package:" + context.packageName)
                    context.startActivity(intentDataSave)
                }

                AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {

                    val appwidgetIds: IntArray? = intent.extras?.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                    if (appwidgetIds != null) {
                        onUpdate(context, AppWidgetManager.getInstance(context), appwidgetIds)
                    }
                }
            }

        }

    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetIds, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is createds
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray, appWidgetId: Int) {

        val views = RemoteViews(context.packageName, R.layout.widget_date_meal)

        initLayout(context, appWidgetManager, appWidgetId, views)
        setOnClickPendingIntent(context, views, appWidgetIds)

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)

    }

    /**
     * 절전모드 체크
     * */
    private fun initLayout(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, views: RemoteViews) {


        views.setViewVisibility(R.id.ll_empty_school, View.GONE)
        views.setViewVisibility(R.id.tv_power_save, View.GONE)
        views.setViewVisibility(R.id.tv_meals, View.GONE)
        views.setViewVisibility(R.id.ll_empty_meal, View.GONE)
        views.setViewVisibility(R.id.tv_data_save, View.GONE)
        views.setViewVisibility(R.id.tv_power_save, View.GONE)
        views.setViewVisibility(R.id.tv_meals, View.GONE)
        views.setViewVisibility(R.id.ll_empty_meal, View.GONE)

        val powerManager = context.getSystemService(POWER_SERVICE) as PowerManager
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && powerManager.isPowerSaveMode
                && !powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            // 전원절약
            views.setViewVisibility(R.id.tv_power_save, View.VISIBLE)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                && connectivityManager.restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED) {
            // 데이터절약
            views.setViewVisibility(R.id.tv_data_save, View.VISIBLE)
        } else {
            getData(context, appWidgetManager, appWidgetId, views)
        }

    }

    /**
     * Click Event
     */
    private fun setOnClickPendingIntent(context: Context, views: RemoteViews, appWidgetIds: IntArray) {

        var requestCode = 0

        // 새로고침
        val refresh = Intent(context, WidgetDateMealProvider::class.java)
        refresh.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        refresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
        views.setOnClickPendingIntent(R.id.iv_refresh, PendingIntent.getBroadcast(context, 0, refresh, PendingIntent.FLAG_UPDATE_CURRENT))

        // 절전모드
        setSystem(context, R.id.tv_power_save, views, requestCode++, SchemeConst.WIDGET_POWER_SAVE)
        // 데이터세이버모드
        setSystem(context, R.id.tv_power_save, views, requestCode++, SchemeConst.WIDGET_DATA_SAVE)

        // 학교선택
        setDeepLink(context, R.id.ll_empty_school, views, requestCode++, SchemeConst.SCHOOL_CHOOSE)
        // 주간식단
        setDeepLink(context, R.id.tv_meals, views, requestCode++, SchemeConst.WEEKLY_MEALS)
        // 홈
        setDeepLink(context, R.id.ll_community, views, requestCode++, SchemeConst.HOME)
        // 비디오 리스트
        setDeepLink(context, R.id.ll_video, views, requestCode++, SchemeConst.VIDEO_LIST)

    }

    /**
     * 시스템 설정
     */
    private fun setSystem(context: Context, resId: Int, views: RemoteViews, requestCode: Int, data: String) {
        val intent = Intent(context, WidgetDateMealProvider::class.java).apply {
            action = data
        }
        views.setOnClickPendingIntent(resId, PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT))
    }

    /**
     * Deep Link
     */
    private fun setDeepLink(context: Context, resId: Int, views: RemoteViews, requestCode: Int, host: String) {
        val intent = Intent(context, IntroActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            data = Uri.parse("${SchemeConst.SCHEME}://$host")
        }
        views.setOnClickPendingIntent(resId, PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT))
    }

    /**
     * 급식 데이터
     */
    private fun getData(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, views: RemoteViews) {

        // 날짜
        val currentTime = System.currentTimeMillis()
        val searchDate = Utils.getConvertDateFormat(currentTime, "yyyy-MM-dd")
        //views.setTextViewText(R.id.tv_date, Utils.getConvertDateFormat(currentTime, "MM.dd(E)"))

        // 학교 코드
        val insttCode = SessionSingleton.getInstance(context).insttCode

        // 학교 선택값 없음
        if (insttCode == null) {
            views.setViewVisibility(R.id.ll_empty_school, View.VISIBLE)
            return
        }

        Logger.e(TAG, "searchDate : $searchDate")
        Logger.e(TAG, "insttCode : $insttCode")

        val client = AsyncHttpClient()
        client.addHeader("apiKey", APIConst.apkKey())

        val userInfo = SessionSingleton.getInstance(context).header
        if (!ObjectUtils.isEmpty(userInfo)) {
            client.addHeader("userInfo", userInfo)
        }

        // RequestParams 생성
        val params = RequestParams()
        params.put("searchDate", searchDate)
        params.put("insttCode", insttCode)

        client.get(HostConst.apiHost() + "selectDateMenuSngl.do", params, object : JsonHttpResponseHandler() {

            override fun onSuccess(statusCode: Int, headers: Array<Header>, response: JSONObject) {
                // called when response HTTP status is "200 OK"

                try {

                    if (statusCode == 200) {
                        Logger.e(TAG, response.toString())
                        val gson = Gson()
                        var model = gson.fromJson(response.toString(), DateMealModel::class.java)

                        if (ObjectUtils.isEmpty(model)) {
                            return
                        }

                        views.setTextViewText(R.id.tv_meals, "")

                        var result = ""
                        if (model.menuList != null) {
                            model.menuList!!.forEachIndexed { index, item: DateMealItem ->

                                result += if (index < model.menuList!!.size - 1) {
                                    item.menuNm + ", "
                                } else {
                                    item.menuNm
                                }
                            }
                        }

                        Logger.e(TAG, result)

                        if (!ObjectUtils.isEmpty(result)) {
                            views.setViewVisibility(R.id.tv_meals, View.VISIBLE)
                            views.setTextViewText(R.id.tv_meals, result)

                            // 급식 타입
                            val mealType = TextFormatUtils.getMealType(model.mealType)

                            views.setTextViewText(R.id.tv_meal_type, mealType)
                            var mealTime: String = model.searchDate
                            var dweek: String = model.dweek
                            var dweekE: String = dweek.substring(0, 1)
                            var month: String = mealTime.substring(5, 7)
                            var day: String = mealTime.substring(8, 10)
                            var widgetThisDate: String = "$month.$day($dweekE)"
                            views.setTextViewText(R.id.tv_date, widgetThisDate)

                        } else {
                            views.setViewVisibility(R.id.ll_empty_meal, View.VISIBLE)
                            val mealType = TextFormatUtils.getMealType(model.mealType)
                            views.setTextViewText(R.id.tv_meal_type, mealType)
                            var mealTime: String = model.searchDate
                            var dweek: String = model.dweek
                            var dweekE: String = dweek.substring(0, 1)
                            var month: String = mealTime.substring(5, 7)
                            var day: String = mealTime.substring(8, 10)
                            var widgetThisDate: String = "$month.$day($dweekE)"
                            views.setTextViewText(R.id.tv_date, widgetThisDate)
                        }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    views.setViewVisibility(R.id.ll_empty_meal, View.VISIBLE)
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)

            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, errorResponse: String, e: Throwable) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                e.printStackTrace()
                Logger.e(TAG, "statusCode : $statusCode / ${e.message}")
            }

        })

    }
}

