package otus.homework.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlin.math.cos
import kotlin.math.sin


class PinChart(var mContext: Context, attrs: AttributeSet?) :
    View(mContext, attrs) {
    private val p: Paint = Paint()
    private var startX: Float = 0f
    private var startY: Float = 0f
    private var radius: Float = 0f
    private var colors: ArrayList<Int>
    private var bitmap: Bitmap? = null
    private var payloadCategoriesList: List<CategoryEntity>

    init {
        p.isAntiAlias = true
        p.textSize = 15.dp
        colors = getColors()
        payloadCategoriesList = Data.makeCategoriesList(mContext, colors)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)

        when (wMode) {
            MeasureSpec.EXACTLY -> setMeasuredDimension(wSize, hSize)
            MeasureSpec.AT_MOST -> {
                val newW = Integer.min((payloadCategoriesList.size * 100.dp).toInt(), wSize)
                setMeasuredDimension(newW, hSize)
            }

            MeasureSpec.UNSPECIFIED -> setMeasuredDimension(
                (payloadCategoriesList.size * 100.dp).toInt(),
                hSize
            )
        }
    }

    //https://stackoverflow.com/questions/8981996/click-event-on-pie-chart-in-android
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        startX = width / 4f
        startY = height / 4f
        radius = width / 2f
        var c = Canvas()
        bitmap = Bitmap.createBitmap(
            canvas.width, canvas.height,
            Bitmap.Config.ARGB_8888
        )
        if (bitmap != null) {
            c = Canvas(bitmap!!)
        }

        Log.d("MyLog", "onDraw() is called...")
        var offset = 0f
        var amountsTotalSum = 0f
        for (a in payloadCategoriesList.indices) {
            amountsTotalSum += payloadCategoriesList[a].amount
        }
        val angle = (360 / amountsTotalSum)
        Log.d("MyLog", "angle $angle")

        val rectF = RectF()
        Log.d("MyLog", "startX=$startX startY=$startY width=$width height=$height")
        rectF[startX, startY, (startX + radius)] =
            (startY + radius)

        for (i in payloadCategoriesList.indices) {
            p.color = payloadCategoriesList[i].color

            canvas.drawArc(rectF, offset, payloadCategoriesList[i].amount * angle, true, p)
            c.drawArc(rectF, offset, payloadCategoriesList[i].amount * angle, true, p)

            //text https://stackoverflow.com/questions/73316391/android-canvas-draw-text-on-circle-radius-based
            val arcCenter = offset + ((payloadCategoriesList[i].amount * angle) / 2)
            //middle point radius is half of the radius of the circle
            val pointRadius = radius*0.6
            //Calculate the x & y coordinates
            val x =
                ((pointRadius * cos(Math.toRadians(arcCenter.toDouble()))) +
                        width  / 2).toFloat()
            val y =
                ((pointRadius * sin(Math.toRadians(arcCenter.toDouble()))) +
                        height  / 2).toFloat()

            canvas.drawText(
                "%.2f".format(payloadCategoriesList[i].amount / amountsTotalSum * 100) + "%",
                x-radius/10,
                y-radius/5,
                p
            )
            offset += payloadCategoriesList[i].amount * angle
        }
        canvas.save()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val color = bitmap!!.getPixel(event.x.toInt(), event.y.toInt())
        Log.e("", "" + color)
        if (colors.contains(color)) {
            Toast.makeText(
                mContext,
                payloadCategoriesList[colors.indexOf(color)].category,
                Toast.LENGTH_SHORT
            ).show()
        }
        return super.onTouchEvent(event)
    }
}
