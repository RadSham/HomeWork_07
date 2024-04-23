package otus.homework.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Parcelable
import android.util.AttributeSet
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
    private var diameter: Float = 0f
    private var colors: ArrayList<Int>
    private var bitmap: Bitmap? = null
    private var payloadCategoriesList: List<CategoryEntity>
    private var lastSelectedCategory: Int = 0

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
                val newW = Integer.min((payloadCategoriesList.size * 50.dp).toInt(), wSize)
                setMeasuredDimension(newW, hSize)
            }

            MeasureSpec.UNSPECIFIED -> setMeasuredDimension(
                (payloadCategoriesList.size * 50.dp).toInt(),
                hSize
            )
        }
    }

    //https://stackoverflow.com/questions/8981996/click-event-on-pie-chart-in-android
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        diameter = if (width < height) (width / 2).toFloat() else (height / 2).toFloat()
        startX = (width / 2).toFloat() - diameter / 2
        startY = (height / 2).toFloat() - diameter / 2
        var c = Canvas()
        bitmap = Bitmap.createBitmap(
            canvas.width, canvas.height,
            Bitmap.Config.ARGB_8888
        )
        if (bitmap != null) {
            c = Canvas(bitmap!!)
        }

        var offset = 0f
        var amountsTotalSum = 0f
        for (a in payloadCategoriesList.indices) {
            amountsTotalSum += payloadCategoriesList[a].amount
        }
        val angle = (360 / amountsTotalSum)

        val rectF = RectF()
        rectF[startX, startY, (startX + diameter)] =
            (startY + diameter)

        for (i in payloadCategoriesList.indices) {
            p.color = payloadCategoriesList[i].color

            canvas.drawArc(rectF, offset, payloadCategoriesList[i].amount * angle, true, p)
            c.drawArc(rectF, offset, payloadCategoriesList[i].amount * angle, true, p)

            //text https://stackoverflow.com/questions/73316391/android-canvas-draw-text-on-circle-radius-based
            val arcCenter = offset + ((payloadCategoriesList[i].amount * angle) / 2)
            //middle point radius is half of the radius of the circle
            val pointRadius = diameter * 0.6
            //Calculate the x & y coordinates
            val x =
                ((pointRadius * cos(Math.toRadians(arcCenter.toDouble()))) +
                        width / 2).toFloat()
            val y =
                ((pointRadius * sin(Math.toRadians(arcCenter.toDouble()))) +
                        height / 2).toFloat()

            canvas.drawText(
                "%.2f".format(payloadCategoriesList[i].amount / amountsTotalSum * 100) + "%",
                x,
                y,
                p
            )
            offset += payloadCategoriesList[i].amount * angle
        }
        canvas.save()
    }

    // https://www.netguru.com/blog/how-to-correctly-save-the-state-of-a-custom-view-in-android
    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        println("Hi")
        return SelectedCategoryState(superState).apply {
            selectedCagetory = lastSelectedCategory
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        when (state) {
            is SelectedCategoryState -> {
                super.onRestoreInstanceState(state.superState)
                lastSelectedCategory = state.selectedCagetory
            }
            else -> super.onRestoreInstanceState(state)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val color = bitmap!!.getPixel(event.x.toInt(), event.y.toInt())
        if (colors.contains(color)) {
            Toast.makeText(
                mContext,
                payloadCategoriesList[colors.indexOf(color)].category,
                Toast.LENGTH_SHORT
            ).show()
            lastSelectedCategory = colors.indexOf(color)
        }
        return super.onTouchEvent(event)
    }
}
