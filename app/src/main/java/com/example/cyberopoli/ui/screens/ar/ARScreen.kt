package com.example.cyberopoli.ui.screens.ar

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cyberopoli.util.ARHelper
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.Node
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView

@Composable
fun ARScreen(navController: NavController) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine = engine)
    val materialLoader = rememberMaterialLoader(engine = engine)
    val cameraNode = rememberARCameraNode(engine = engine)
    val childNodes = rememberNodes()
    val view = rememberView(engine = engine)
    val collisionSystem = rememberCollisionSystem(view = view)
    val planeRenderer = remember { mutableStateOf(true) }
    val modelInstance = remember { mutableListOf<ModelInstance>() }
    val trackingFailureReason = remember { mutableStateOf<TrackingFailureReason?>(null) }
    val frameState = remember { mutableStateOf<Frame?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(modifier = Modifier.fillMaxSize(),
            childNodes = childNodes,
            engine = engine,
            view = view,
            modelLoader = modelLoader,
            collisionSystem = collisionSystem,
            planeRenderer = planeRenderer.value,
            cameraNode = cameraNode,
            materialLoader = materialLoader,
            onTrackingFailureChanged = { trackingFailureReason.value = it },
            onSessionUpdated = { _, updatedFrame ->
                frameState.value = updatedFrame
            },
            sessionConfiguration = { session, config ->
                config.depthMode =
                    if (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) Config.DepthMode.AUTOMATIC else Config.DepthMode.DISABLED
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            },
            onGestureListener = rememberOnGestureListener(onSingleTapConfirmed = { e: MotionEvent, node: Node? ->
                if (node == null) {
                    val hitTestResult = frameState.value?.hitTest(e.x, e.y)
                    hitTestResult?.firstOrNull {
                        it.isValid(depthPoint = false, point = false)
                    }?.createAnchorOrNull()?.let { hitAnchor ->
                        val nodeModel = ARHelper.createAnchorNode(
                            engine = engine,
                            modelLoader = modelLoader,
                            materialLoader = materialLoader,
                            modelInstance = modelInstance,
                            anchor = hitAnchor,
                            model = "models/chicken_nugget.glb"
                        )
                        childNodes += nodeModel
                    }
                }
            }))

        Reticle(modifier = Modifier.align(Alignment.Center))

        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        FloatingActionButton(modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 100.dp), onClick = {
            childNodes.clear()
            modelInstance.clear()
            val centerCoordinates = with(density) {
                Offset(
                    x = (configuration.screenWidthDp.dp.toPx() / 2f),
                    y = (configuration.screenHeightDp.dp.toPx() / 2f)
                )
            }
            frameState.value?.hitTest(centerCoordinates.x, centerCoordinates.y)?.firstOrNull {
                    it.isValid(depthPoint = false, point = false)
                }?.createAnchorOrNull()?.let { centerAnchor ->
                    val nuggetNode = ARHelper.createAnchorNode(
                        engine = engine,
                        modelLoader = modelLoader,
                        materialLoader = materialLoader,
                        modelInstance = modelInstance,
                        anchor = centerAnchor,
                        model = "models/chicken_nugget.glb"
                    )
                    childNodes += nuggetNode
                }
        }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Nugget")
        }
    }
}

@Composable
fun Reticle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(30.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height / 2
            val lineLength = 10f
            val strokeWidth = 2f
            drawLine(
                color = Color.Red,
                start = Offset(cx - lineLength, cy),
                end = Offset(cx + lineLength, cy),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = Color.Red,
                start = Offset(cx, cy - lineLength),
                end = Offset(cx, cy + lineLength),
                strokeWidth = strokeWidth
            )
        }
    }
}
