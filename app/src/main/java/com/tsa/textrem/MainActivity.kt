package com.tsa.textrem

import android.graphics.*
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    val insufficientText: List<String> = listOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var rect: Rect? = null
        val options = BitmapFactory.Options().apply {
            inScaled = true
            inMutable = true
        }

        var bitmap = BitmapFactory.decodeResource(resources, R.drawable.test, options)
        imageView.setImageBitmap(bitmap)
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient()
        val result = recognizer.process(image).addOnSuccessListener {
            //Log.d("SUPERTAG", "success: ${it.toString()}")
            val resultText = it.text
            for (block in it.textBlocks) {
                val blockText = block.text
                val blockCornerPoints = block.cornerPoints
                val blockFrame = block.boundingBox
                for (line in block.lines) {
                    val lineText = line.text
                    val lineCornerPoints = line.cornerPoints
                    val lineFrame = line.boundingBox
                    for (element in line.elements) {
                        val elementText = element.text
                        val elementCornerPoints = element.cornerPoints
                        val elementFrame = element.boundingBox
                        parseText(rect, elementFrame, elementText, bitmap)
                    }
                }
            }
        }.addOnFailureListener {
            Log.d("SUPERTAG", "failure: ${it.toString()}")
        }


    }

    private fun parseText(
        rect: Rect?,
        elementFrame: Rect?,
        elementText: String,
        bitmap: Bitmap
    ) {
        var rect1 = rect
        rect1 = elementFrame

        var targetText = elementText
        val small = targetText.toLowerCase(Locale.ROOT)
        val trimmed = small.trim()

        val baseStringSize = trimmed.length
        val badStringSize = "gdz.ltd".length
        val index = trimmed.indexOf("gdz.ltd")
        // Log.d("SUPERTAG", index.toString())
        if (index != -1) {
            if (badStringSize == baseStringSize) {
                drawRect(bitmap, rect1, 1.0, 0)
            } else {
                Log.d("SUPERTAG2", trimmed)
                Log.d(
                    "SUPERTAG",
                    "start index: $index\nbase size: $baseStringSize\nbad size: $badStringSize"
                )
                if (index == 0) {
                    val share = badStringSize.toDouble().div(baseStringSize)
                    Log.d("SUPERTAG", share.toString())
                    drawRect(bitmap, rect1, share, 11)
                } else if(index == (baseStringSize - badStringSize)){
                    val share = badStringSize.toDouble().div(baseStringSize)
                    Log.d("SUPERTAG", share.toString())
                    drawRect(bitmap, rect1, share, 22)
                }
            }
        }


        //Log.d("SUPERTAG", "all- $trimmed")
//        if(trimmed.contains("gdz.ltd")){
//            val result = trimmed.removePrefix("gdz.ltd")
//            drawRect(bitmap, rect1)
//        }
    }

    private fun drawRect(bitmap: Bitmap, rect1: Rect?, share: Double, type: Int) {
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        val paint = Paint().apply {
            style = Paint.Style.FILL
            color = Color.YELLOW
            isAntiAlias = true
        }
        if (share == 1.0) {
            canvas.drawRect(rect1!!, paint)
            imageView.setImageBitmap(bitmap)
        } else if (share < 1.0) {
            if(type == 11){
                rect1?.let {
                    Log.d("SUPERTAG33","left:${rect1.left}\ntop:${rect1.top}\nright:${rect1.right}\nbottom:${rect1.bottom}\n")

                    val newRect = Rect(
                        rect1.left,
                        rect1.top,
                        (rect1.right.toDouble() * share).roundToInt(),
                        rect1.bottom
                    )

                    canvas.drawRect(newRect, paint)
                    imageView.setImageBitmap(bitmap)
                }
            } else if(type == 22){
                rect1?.let {
                    Log.d("SUPERTAG3","left:${rect1.left + ((rect1.right - rect1.left).toDouble()*share).roundToInt()}\ntop:${rect1.top}\nright:${rect1.right}\nbottom:${rect1.bottom}\n")
                    val newRect = Rect(
                        rect1.left + ((rect1.right - rect1.left).toDouble()*share).roundToInt(),
                        rect1.top,
                        rect1.right,
                        rect1.bottom
                    )

                    canvas.drawRect(newRect, paint)
                    imageView.setImageBitmap(bitmap)
                }
            }



        }
    }
}