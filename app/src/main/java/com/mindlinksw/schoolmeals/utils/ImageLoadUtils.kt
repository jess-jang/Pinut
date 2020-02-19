package com.mindlinksw.schoolmeals.utils


import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

/**
 * Created by N16326 on 2018. 11. 5..
 */

object ImageLoadUtils {

    private val TAG = ImageLoadUtils::class.java.name

    /**
     * @param view ImageView
     * @param url  이미지 주소
     * @author 장세진
     * @description 단순 이미지 로드
     */
    @JvmStatic
    @BindingAdapter("loadImage")
    fun setLoadImage(view: ImageView, url: Any?) {
        try {
            loadImage(view, url)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * @param view ImageView
     * @param url  이미지 주소
     * @author 장세진
     * @description 단순 이미지 로드
     */
    @JvmStatic
    @BindingAdapter("loadImage")
    fun setLoadImage(view: ImageView, url: Int?) {
        try {
            loadImage(view, url)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * @param view ImageView
     * @param url  이미지 주소
     * @author 장세진
     * @description 단순 이미지 로드
     */
    @JvmStatic
    fun loadImage(view: ImageView, url: Any?) {

        try {

            if (ObjectUtils.isEmpty(url)) {
                return
            }

            Glide.with(view)
                    .load(url)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            Logger.e(TAG, e!!.message + " / " + url)
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .into(view)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * width, height, uri 이미지 로더
     *
     * @param view
     * @param uri
     * @param width
     * @param height
     */
    @JvmStatic
    @BindingAdapter("loadImage", "width", "height")
    fun setLoadImage(view: ImageView, uri: Uri, width: Float, height: Float) {
        loadImage(view, uri, width, height)
    }

    /**
     * width, height, uri 이미지 로더
     *
     * @param view
     * @param url
     * @param width
     * @param height
     */
    @JvmStatic
    @BindingAdapter("loadImage", "width", "height")
    fun setLoadImage(view: ImageView, url: String, width: Float, height: Float) {
        loadImage(view, url, width, height)
    }

    /**
     * @param view
     * @param url
     * @param width
     * @param height
     */
    private fun loadImage(view: ImageView, url: Any?, width: Float, height: Float) {


        try {

            if (ObjectUtils.isEmpty(url)) {
                return
            }

            Glide.with(view)
                    .load(url)
                    .apply(RequestOptions()
                            .override(width.toInt(), height.toInt())
                            .centerCrop())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            Logger.e(TAG, e!!.message + " / " + url)
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .into(view)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 이미지 로드 (원 형태 이미지)
     *
     * @param view
     * @param url
     */
    @JvmStatic
    @BindingAdapter("loadImageCircle")
    fun setLoadImageCircle(view: ImageView, url: String) {

        try {

            if (ObjectUtils.isEmpty(url)) {
                return
            }

            Glide.with(view)
                    .load(url)
                    .apply(RequestOptions().centerCrop().circleCrop())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            Logger.e(TAG, e!!.message + " / " + url)
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            return false
                        }
                    })
                    .into(view)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 이미지 로드 - 상세 페이지에서 사용
     *
     * @param view
     * @param url
     */
    @JvmStatic
    @BindingAdapter("loadImageDetail")
    fun setLoadImageDetail(view: ImageView, url: String) {

        if (ObjectUtils.isEmpty(url)) {
            return
        }

        view.post {

            Glide.with(view.context)
                .asBitmap()
                .load(url)
                .listener(object : RequestListener<Bitmap> {

                    override fun onResourceReady(bitmap: Bitmap, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                        try {

                            if (ObjectUtils.isEmpty(bitmap)) {
                                return false
                            }

                            val width = bitmap.width
                            val height = bitmap.height
                            val maxWH = Utils.getMaxTextureSize(view.context)

                            if (width > maxWH || height > maxWH) {

                                val matrix = Matrix()
                                var newRato: Float = 0.0F
                                if (width > maxWH) {
                                    newRato = Integer.valueOf(maxWH)!!.toFloat() / Integer.valueOf(width)!!.toFloat()
                                    Logger.e(TAG, "newRato : $newRato")
                                } else if (height > maxWH) {
                                    newRato = Integer.valueOf(maxWH)!!.toFloat() / Integer.valueOf(height)!!.toFloat()
                                }

                                Logger.e(TAG, "newRato : $newRato")
                                matrix.postScale(newRato, newRato)

                                val newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
                                setLoadImage(view, newBitmap)
                            } else {
                                setLoadImage(view, bitmap)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        return true
                    }

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                        return false
                    }
                }).submit()
        }
    }
}
