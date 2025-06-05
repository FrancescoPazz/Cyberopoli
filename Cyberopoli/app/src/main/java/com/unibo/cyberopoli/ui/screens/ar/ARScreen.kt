package com.unibo.cyberopoli.ui.screens.ar

import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.android.filament.Engine
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.TrackingFailureReason
import com.unibo.cyberopoli.ui.screens.ar.composables.Reticle
import com.unibo.cyberopoli.util.ARHelper
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
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
fun ARScreen() {
    val engine = rememberEngine()
    val view = rememberView(engine)
    val childNodes = rememberNodes()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val cameraNode = rememberARCameraNode(engine)
    val collisionSystem = rememberCollisionSystem(view)

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val modelInstances = remember { mutableListOf<ModelInstance>() }
    var currentFrame by remember { mutableStateOf<Frame?>(null) }
    var trackingFailure by remember { mutableStateOf<TrackingFailureReason?>(null) }
    val showPlanes by remember { mutableStateOf(true) }

    val sessionConfig: (session: Any, config: Config) -> Unit = { session, config ->
        config.apply {
            depthMode = if ((session as com.google.ar.core.Session).isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                Config.DepthMode.AUTOMATIC
            } else {
                Config.DepthMode.DISABLED
            }
            lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
        }
    }

    val gestureListener = rememberOnGestureListener(
        onSingleTapConfirmed = { event: MotionEvent, node: Node? ->
            if (node == null) {
                placeModelAtPosition(
                    frame = currentFrame,
                    x = event.x,
                    y = event.y,
                    engine = engine,
                    modelLoader = modelLoader,
                    materialLoader = materialLoader,
                    modelInstances = modelInstances,
                    childNodes = childNodes
                )
            }
        }
    )

    val placeModelAtCenter = {
        clearModels(childNodes, modelInstances)

        val centerOffset = with(density) {
            Offset(
                x = configuration.screenWidthDp.dp.toPx() / 2f,
                y = configuration.screenHeightDp.dp.toPx() / 2f
            )
        }

        placeModelAtPosition(
            frame = currentFrame,
            x = centerOffset.x,
            y = centerOffset.y,
            engine = engine,
            modelLoader = modelLoader,
            materialLoader = materialLoader,
            modelInstances = modelInstances,
            childNodes = childNodes
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            childNodes = childNodes,
            engine = engine,
            view = view,
            modelLoader = modelLoader,
            collisionSystem = collisionSystem,
            planeRenderer = showPlanes,
            cameraNode = cameraNode,
            materialLoader = materialLoader,
            onTrackingFailureChanged = { trackingFailure = it },
            onSessionUpdated = { _, frame -> currentFrame = frame },
            sessionConfiguration = sessionConfig,
            onGestureListener = gestureListener
        )

        Reticle(modifier = Modifier.align(Alignment.Center))

        FloatingActionButton(
            onClick = placeModelAtCenter,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Model"
            )
        }
    }
}

private fun placeModelAtPosition(
    frame: Frame?,
    x: Float,
    y: Float,
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    modelInstances: MutableList<ModelInstance>,
    childNodes: MutableList<Node>
) {
    frame?.hitTest(x, y)
        ?.firstOrNull { it.isValid(depthPoint = false, point = false) }
        ?.createAnchorOrNull()
        ?.let { anchor ->
            val modelNode = ARHelper.createAnchorNode(
                engine = engine,
                modelLoader = modelLoader,
                materialLoader = materialLoader,
                modelInstance = modelInstances,
                anchor = anchor,
                model = "models/board.glb"
            )
            childNodes += modelNode
        }
}

private fun clearModels(
    childNodes: MutableList<Node>,
    modelInstances: MutableList<ModelInstance>
) {
    childNodes.clear()
    modelInstances.clear()
}