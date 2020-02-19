package com.mindlinksw.test

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mindlinksw.schoolmeals.R
import com.mindlinksw.schoolmeals.databinding.TestActivityBinding
import com.mindlinksw.schoolmeals.utils.ObjectUtils
import com.mindlinksw.schoolmeals.utils.Utils


class TestActivity : AppCompatActivity() {

    // ViewModel, DataBinding
    private var mBinding: TestActivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.test_activity)


    }

    object Test {

        val TAG: String = TestActivity::class.java.name

        @JvmStatic
        @BindingAdapter("testloadImage")
        fun loadImage(view: ImageView, url: String) {

            try {

                val testUrl = "https://s3.ap-northeast-2.amazonaws.com/mindlink/schoolmeal/201901110918140650"

                view.post {


                    Glide.with(view.context)
                        .asBitmap()
                        .load(testUrl)
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
                                        if (width > maxWH) {
                                            val newRato = Integer.valueOf(maxWH)!!.toFloat() / Integer.valueOf(width)!!.toFloat()
                                            matrix.postScale(newRato, newRato)
                                        } else if (height > maxWH) {
                                            val newRato = Integer.valueOf(maxWH)!!.toFloat() / Integer.valueOf(height)!!.toFloat()
                                            matrix.postScale(newRato, newRato)
                                        }

                                        val newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)

                                        view.setImageBitmap(newBitmap)
                                    } else {
                                        view.setImageBitmap(bitmap)
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

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


    }
}