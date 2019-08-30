package com.ex.arexperimnets

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.ex.arexperimnets.AppConstants.MIN_OPENGL_VERSION
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.math.Quaternion


class HelloARActivity : AppCompatActivity() {

    lateinit var arFragment:ArFragment
    lateinit  var manRenderable:ModelRenderable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello_ar)

        checkIsSupportedDeviceOrFinish(this)
        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment

        ModelRenderable.builder().setSource(this,R.raw.model).build().thenAccept {
            manRenderable = it
        }.exceptionally {
            Log.d("Test", it.toString())
            null
        }

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            if (::manRenderable.isInitialized){
                val anchor = hitResult.createAnchor()
                val anchorNode = AnchorNode(anchor)
                anchorNode.setParent(arFragment.arSceneView.scene)

                val manTransformableNode = TransformableNode(arFragment.transformationSystem)
                manTransformableNode.setParent(anchorNode)
                manTransformableNode.renderable = manRenderable
                manTransformableNode.localRotation = Quaternion.axisAngle(Vector3(1f, 0f, 0f), 180f)
                manTransformableNode.select()

            }


        }

    }

    fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        val openGlVersionString =
            (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                .show()
            activity.finish()
            return false
        }
        return true
    }


}
