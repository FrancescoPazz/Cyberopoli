package com.unibo.cyberopoli.ui.screens.ar.view

import android.content.res.Configuration
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.google.android.filament.Engine
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.TrackingFailureReason
import com.unibo.cyberopoli.R
import com.unibo.cyberopoli.data.models.game.GamePlayer
import com.unibo.cyberopoli.ui.screens.ar.view.composables.Reticle
import com.unibo.cyberopoli.util.ARHelper
import com.unibo.cyberopoli.util.getOffsetForPerimeterIndex
import dev.romainguy.kotlin.math.Float3
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
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun ARBox(
    players: List<GamePlayer>?
) {
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

    val boardNodeState = remember { mutableStateOf<Node?>(null) }
    val pieceNodes = remember { mutableListOf<Node>() }

    val sessionConfig: (session: Any, config: Config) -> Unit = { session, config ->
        config.apply {
            depthMode = if ((session as com.google.ar.core.Session).isDepthModeSupported(
                    Config.DepthMode.AUTOMATIC
                )
            ) {
                Config.DepthMode.AUTOMATIC
            } else {
                Config.DepthMode.DISABLED
            }
            lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
        }
    }

    LaunchedEffect(players) {
        if (currentFrame == null || players.isNullOrEmpty()) return@LaunchedEffect
        renderBoardAndPieces(
            frame = currentFrame,
            engine = engine,
            modelLoader = modelLoader,
            modelInstances = modelInstances,
            childNodes = childNodes,
            boardNodeState = boardNodeState,
            pieceNodes = pieceNodes,
            density = density,
            configuration = configuration,
            players = players
        )
    }

    val gestureListener =
        rememberOnGestureListener(onSingleTapConfirmed = { _: MotionEvent, node: Node? ->
            Log.d("ARBox", "Single tap detected on node: $node")
        })

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            childNodes = childNodes,
            engine = engine,
            view = view,
            modelLoader = modelLoader,
            collisionSystem = collisionSystem,
            planeRenderer = true,
            cameraNode = cameraNode,
            materialLoader = materialLoader,
            onTrackingFailureChanged = { trackingFailure = it },
            onSessionUpdated = { _, frame -> currentFrame = frame },
            sessionConfiguration = sessionConfig,
            onGestureListener = gestureListener
        )

        Reticle(modifier = Modifier.align(Alignment.Center))

        FloatingActionButton(
            onClick = {
                placeBoardAtCenter(
                    frame = currentFrame,
                    engine = engine,
                    modelLoader = modelLoader,
                    materialLoader = materialLoader,
                    modelInstances = modelInstances,
                    childNodes = childNodes,
                    boardNodeState = boardNodeState,
                    pieceNodes = pieceNodes,
                    density = density,
                    configuration = configuration
                )

                MainScope().launch {
                    players?.forEach { player ->
                        placePieceOnBoard(
                            frame = currentFrame,
                            engine = engine,
                            modelLoader = modelLoader,
                            modelInstances = modelInstances,
                            childNodes = childNodes,
                            boardNode = boardNodeState.value,
                            pieceNodes = pieceNodes,
                            density = density,
                            configuration = configuration,
                            cellPosition = player.cellPosition
                        )
                    }

                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 24.dp, bottom = 140.dp)
        ) {
            Text(
                text = stringResource(R.string.place_board), modifier = Modifier.padding(8.dp)
            )
        }
    }
}

private fun placeBoardAtCenter(
    frame: Frame?,
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    modelInstances: MutableList<ModelInstance>,
    childNodes: MutableList<Node>,
    boardNodeState: MutableState<Node?>,
    pieceNodes: MutableList<Node>,
    density: Density,
    configuration: Configuration
) {
    if (frame == null) return

    clearAll(childNodes, modelInstances, boardNodeState, pieceNodes)

    val centerOffset = with(density) {
        Offset(
            x = configuration.screenWidthDp.dp.toPx() / 2f,
            y = configuration.screenHeightDp.dp.toPx() / 2f
        )
    }

    frame.hitTest(centerOffset.x, centerOffset.y)
        .firstOrNull { it.isValid(depthPoint = false, point = false) }?.createAnchorOrNull()
        ?.let { anchor ->
            val boardNode = ARHelper.createAnchorNode(
                engine = engine,
                modelLoader = modelLoader,
                materialLoader = materialLoader,
                modelInstance = modelInstances,
                anchor = anchor,
                model = "models/board.glb"
            )
            childNodes += boardNode
            boardNodeState.value = boardNode
        }
}

private suspend fun placePieceOnBoard(
    frame: Frame?,
    engine: Engine,
    modelLoader: ModelLoader,
    modelInstances: MutableList<ModelInstance>,
    childNodes: MutableList<Node>,
    boardNode: Node?,
    pieceNodes: MutableList<Node>,
    density: Density,
    configuration: Configuration,
    cellPosition: Int
) {
    if (frame == null) return
    if (boardNode == null) {
        return
    }

    val centerOffset = with(density) {
        Offset(
            x = configuration.screenWidthDp.dp.toPx() / 2f,
            y = configuration.screenHeightDp.dp.toPx() / 2f
        )
    }

    frame.hitTest(centerOffset.x, centerOffset.y)
        .firstOrNull { it.isValid(depthPoint = false, point = false) }
        ?.let { hitResult: HitResult ->
            val hitPose = hitResult.hitPose
            val worldX = hitPose.tx()
            val worldY = hitPose.ty()
            val worldZ = hitPose.tz()

            val boardWorldPos = boardNode.worldPosition

            val pos = getOffsetForPerimeterIndex(cellPosition)


            val localX = worldX - boardWorldPos.x + pos.first
            val localY = worldY - boardWorldPos.y
            val localZ = worldZ - boardWorldPos.z + pos.second

            val pieceContainer = Node(engine = engine).apply {
                position = Float3(localX, localY, localZ)
                scale = Float3(0.02f, 0.02f, 0.02f)
            }

            val pawnModelPath = "models/yellow_pawn.glb"
            modelLoader.loadModelInstance(pawnModelPath)?.let { loadedPawn ->
                modelInstances.add(loadedPawn)
                val modelNode = io.github.sceneview.node.ModelNode(
                    modelInstance = loadedPawn
                )
                pieceContainer.addChildNode(modelNode)

                boardNode.addChildNode(pieceContainer)
                childNodes.add(pieceContainer)
                pieceNodes.add(pieceContainer)
            }
        }
}

private suspend fun renderBoardAndPieces(
    frame: Frame?,
    engine: Engine,
    modelLoader: ModelLoader,
    modelInstances: MutableList<ModelInstance>,
    childNodes: MutableList<Node>,
    boardNodeState: MutableState<Node?>,
    pieceNodes: MutableList<Node>,
    density: Density,
    configuration: Configuration,
    players: List<GamePlayer>
) {
    if (frame == null) return
    clearPieces(
        pieceNodes = pieceNodes, modelInstances = modelInstances
    )
    players.forEach { player ->
        placePieceOnBoard(
            frame,
            engine,
            modelLoader,
            modelInstances,
            childNodes,
            boardNodeState.value,
            pieceNodes,
            density,
            configuration,
            player.cellPosition
        )
    }
}

private fun clearPieces(
    pieceNodes: MutableList<Node>, modelInstances: MutableList<ModelInstance>
) {
    pieceNodes.forEach { node ->
        node.parent?.removeChildNode(node)
    }
    pieceNodes.clear()
    modelInstances.clear()
}


private fun clearAll(
    childNodes: MutableList<Node>,
    modelInstances: MutableList<ModelInstance>,
    boardNodeState: MutableState<Node?>,
    pieceNodes: MutableList<Node>
) {
    childNodes.forEach { it.parent?.removeChildNode(it) }
    childNodes.clear()
    pieceNodes.clear()
    modelInstances.clear()
    boardNodeState.value = null
}

