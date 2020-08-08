package com.durga.later

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.util.Rational
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel
import com.google.firebase.ml.vision.automl.FirebaseAutoMLRemoteModel
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    lateinit var chose: Button
    lateinit var image: ImageView
    lateinit var resultTv: TextView
    lateinit var labeler :FirebaseVisionImageLabeler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chose=findViewById(R.id.button)
        image=findViewById(R.id.imageView)
        resultTv=findViewById(R.id.textView)
        val localModel = FirebaseAutoMLLocalModel.Builder()
            .setAssetFilePath("model/manifest.json")
            .build()
        val options = FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel)
            .setConfidenceThreshold(0.0f)
            .build()
         labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(options)
        chose.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent,"Chose an image"),121)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==121)
        {
            image.setImageURI(data!!.data)
            val image: FirebaseVisionImage
            try {
                image = FirebaseVisionImage.fromFilePath(applicationContext, data.data!!)
                labeler.processImage(image)
                    .addOnSuccessListener { labels ->
                        for (label in labels) {
                            val text = label.text
                            val confidence = label.confidence
                            resultTv.append(text+"  "+confidence+"\n")
                        }
                    }
                    .addOnFailureListener { e ->

                    }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}