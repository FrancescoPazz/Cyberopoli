package com.unibo.cyberopoli.util

import androidx.compose.ui.graphics.Color
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode

object ARHelper {
    fun createAnchorNode(
        engine: Engine,
        modelLoader: ModelLoader,
        materialLoader: MaterialLoader,
        modelInstance: MutableList<ModelInstance>,
        anchor: Anchor,
        model: String,
    ): AnchorNode {
        val anchorNode = AnchorNode(engine = engine, anchor = anchor)
        val modelNode =
            ModelNode(
                modelInstance =
                    modelInstance.apply {
                        if (isEmpty()) {
                            this += modelLoader.createInstancedModel(model, 10)
                        }
                    }.removeAt(
                        modelInstance.apply {
                            if (isEmpty()) {
                                this += modelLoader.createInstancedModel(model, 10)
                            }
                        }.lastIndex,
                    ),
                scaleToUnits = 0.2f,
            ).apply {
                isEditable = true
            }
        val boundingBox =
            CubeNode(
                engine = engine,
                size = modelNode.extents,
                center = modelNode.center,
                materialInstance = materialLoader.createColorInstance(Color.White),
            ).apply {
                isVisible = false
            }
        modelNode.addChildNode(boundingBox)
        anchorNode.addChildNode(modelNode)
        listOf(modelNode, anchorNode).forEach {
            it.onEditingChanged = { editingTransforms ->
                boundingBox.isVisible = editingTransforms.isNotEmpty()
            }
        }
        return anchorNode
    }
}

fun getOffsetForPerimeterIndex(perimeterIndex: Int): Pair<Float, Float> {
    return when (perimeterIndex) {
        // First line
        8  -> Pair(-0.066f,  -0.0652f)
        9  -> Pair(-0.03675f, -0.0652f)
        10 -> Pair(-0.00752f, -0.0652f)
        11 -> Pair(+0.02173f, -0.0652f)
        12 -> Pair(+0.05098f, -0.0652f)

        // Left board
        15 -> Pair(-0.066f,  -0.03595f)
        22 -> Pair(-0.066f,  -0.00300f)
        29 -> Pair(-0.066f,  +0.02400f)

        // Right board
        19 -> Pair(+0.05098f, -0.03595f)
        26 -> Pair(+0.05098f, -0.00300f)
        33 -> Pair(+0.05098f, +0.02400f)

        // Last line
        36 -> Pair(-0.066f,  +0.05098f)
        37 -> Pair(-0.03675f, +0.05098f)
        38 -> Pair(-0.00752f, +0.05098f)
        39 -> Pair(+0.02173f, +0.05098f)
        40 -> Pair(+0.05098f, +0.05098f)

        else -> Pair(0f, 0f)
    }
}