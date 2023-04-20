package com.android.ar

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.android.ar.databinding.ActivityMainBinding
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import io.github.sceneview.math.Position
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class MainActivity : AppCompatActivity(), Listener {
	val DEBUG_TAG = "MakePhotoActivity"
	private val camera: Camera? = null
	private val cameraId = 0
	var photo: File? = null
	private lateinit var photoSaver: PhotoSaver

	private val objects = listOf(
		SelectableObject("Кристалл", R.drawable.crystal),
		SelectableObject("Бонсай", R.drawable.bonsai),
		SelectableObject("Тирекс", R.drawable.trex),
		SelectableObject("Стол", R.drawable.table),
		SelectableObject("Велосипед", R.drawable.bike),
		SelectableObject("Паук", R.drawable.spider),
		SelectableObject("Танк", R.drawable.tank),
		SelectableObject("Такси", R.drawable.taxi),
	)

	val models = listOf(
		Model("model/Crystal.glb",
			placementMode = PlacementMode.INSTANT.apply {
				keepRotation = true
			},
			scaleUnits = 0.5f
		),
		Model("model/Bonsai.glb",
			placementMode = PlacementMode.INSTANT.apply {
				keepRotation = true
			},
			scaleUnits = 0.5f
		),
		Model("model/T-Rex.glb",
			placementMode = PlacementMode.INSTANT.apply {
				keepRotation = true
			},
			scaleUnits = 0.5f
		),
		Model("model/Table.glb",
			placementMode = PlacementMode.INSTANT.apply {
				keepRotation = true
			},
			scaleUnits = 0.5f
		),
		Model("model/Bike.glb",
			placementMode = PlacementMode.INSTANT.apply {
				keepRotation = true
			},
			scaleUnits = 0.5f
		),
		Model("model/Spider.glb",
			placementMode = PlacementMode.INSTANT.apply {
				keepRotation = true
			},
			scaleUnits = 0.5f
		),
		Model("model/Tank.glb",
			placementMode = PlacementMode.INSTANT.apply {
				keepRotation = true
			},
			scaleUnits = 0.5f
		),
		Model("model/Taxi.glb",
			placementMode = PlacementMode.INSTANT.apply {
				keepRotation = true
			},
			scaleUnits = 0.5f
		),
	)

	private val binding by viewBinding(ActivityMainBinding::bind)
	private var modelNode: ArModelNode? = null
	lateinit var sceneView: ArSceneView
	lateinit var loadingView: View
	lateinit var placeModelButton: Button

	private val adapter =
		SelectableObjectAdapter(objects,this)

	var isLoading = false
		set(value) {
			field = value
			loadingView.isGone = !value
		}

	override fun onCreate(savedInstanceState: Bundle?) {
		WindowCompat.setDecorFitsSystemWindows(window, false)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		sceneView = binding.sceneView
		loadingView = binding.loadingView

		placeModelButton = binding.placeObject.apply{
			setOnClickListener { placeModelNode() }
		}

		binding.removeObjects.setOnClickListener {
			modelNode?.apply {
				binding.sceneView.removeChild(this)
				destroy()
			}
			binding.sceneView.planeRenderer.isEnabled = true
			modelNode = null
		}
		initPhotoSaver(sceneView)
		binding.imageView.setOnClickListener{
			photoSaver.takePhoto()
		}
		binding.recyclerSelectObject.adapter = adapter
	}

	private fun initPhotoSaver(sceneView: ArSceneView) {
		photoSaver = PhotoSaver(this).apply {
			setSceneView(sceneView)
			setPhotoSaveCallback(object : PhotoSaver.ShotCallback {
				override fun onSuccess(uri: Uri?) {
					if (uri == null) return
					runOnUiThread {
						Toast.makeText(this@MainActivity, "Photo saved", Toast.LENGTH_SHORT ).show()
					}
				}
				override fun onFail(msg: String) {
					Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT ).show()
				}
			})
		}
	}

	override fun onClick(modelIndex:Int) {
		isLoading = true
		val model = models[modelIndex]
		Log.i("My","The fie is ${model.fileLocation}")
		placeModelButton.isVisible = true
		modelNode = ArModelNode(
			placementMode = model.placementMode,
			hitPosition = Position(0.0f, 0.0f, -2.0f),
			followHitPosition = true,
			instantAnchor = false,
		).apply {
			loadModelAsync( //try to load the model... This is function of ArModelNOde
				context = this@MainActivity,
				lifecycle = lifecycle,
				glbFileLocation = model.fileLocation,
				autoAnimate = true,
				scaleToUnits = model.scaleUnits,
				// Place the model origin at the bottom center
				centerOrigin = Position(y = -1.0f)
			) { // after successfully loading the model then....
				sceneView.planeRenderer.isVisible = true
				isLoading = false
				Log.i("My","The fie is ${model.fileLocation}")
			}
		}
		modelNode!!.isScaleEditable = true
		modelNode!!.minEditableScale = 1.0f
		modelNode!!.maxEditableScale = 3.0f
		sceneView.addChild(modelNode!!)
		sceneView.selectedNode = modelNode
	}

	fun placeModelNode() {
		modelNode?.anchor()
		placeModelButton.isVisible = false
		sceneView.planeRenderer.isVisible = false //the many many small dots de
	}
}