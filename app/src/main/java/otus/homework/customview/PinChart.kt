package otus.homework.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast


class PinChart(var mContext: Context, attrs: AttributeSet?) :
    View(mContext, attrs) {
    private val p: Paint = Paint()
    private var startX: Float
    private var startY: Float
    private var radius: Float
    private var colors: ArrayList<Int>
    private var bitmap: Bitmap? = null
    private var payloadCategoriesList: List<CategoryEntity>

    init {
        p.isAntiAlias = true
        colors = ArrayList()
        startX = width/2f+100
        startY = height/2f+100
        radius = 500f
        colors.add(Color.GREEN)
        colors.add(Color.CYAN)
        colors.add(Color.MAGENTA)
        colors.add(Color.BLUE)
        colors.add(Color.RED)
        colors.add(Color.YELLOW)
        colors.add(Color.GRAY)
        colors.add(Color.BLACK)
        colors.add(Color.DKGRAY)
        colors.add(Color.LTGRAY)
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
            MeasureSpec.UNSPECIFIED -> setMeasuredDimension((payloadCategoriesList.size * 100.dp).toInt(), hSize)
        }
    }

    //https://stackoverflow.com/questions/8981996/click-event-on-pie-chart-in-android
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var c = Canvas()
        bitmap = Bitmap.createBitmap(
            canvas.width, canvas.height,
            Bitmap.Config.ARGB_8888
        )
        if (bitmap != null) {
            c = Canvas(bitmap!!)
        }

        Log.e("", "onDraw() is called...")
        var offset = 0f
        var sum = 0f
        for (a in payloadCategoriesList.indices) {
            sum += payloadCategoriesList[a].amount
        }
        val angle = (360 / sum)
        Log.e("angle", "" + angle)
        val rectF = RectF()
        rectF[startX.toFloat(), startY.toFloat(), (startX + radius).toFloat()] =
            (startY + radius).toFloat()
        for (i in payloadCategoriesList.indices) {
            p.color = payloadCategoriesList[i].color
            if (i == 0) {
                canvas.drawArc(rectF, 0f, payloadCategoriesList[i].amount * angle, true, p)
                c.drawArc(rectF, 0f, payloadCategoriesList[i].amount * angle, true, p)
            } else {
                canvas.drawArc(rectF, offset, payloadCategoriesList[i].amount * angle, true, p)
                c.drawArc(rectF, offset, payloadCategoriesList[i].amount * angle, true, p)
            }
            offset += payloadCategoriesList[i].amount * angle
        }
        canvas.save()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val color = bitmap!!.getPixel(event.x.toInt(), event.y.toInt())
        Log.e("", "" + color)
        if (colors.contains(color)) {
            Toast.makeText(mContext, payloadCategoriesList[colors.indexOf(color)].category, Toast.LENGTH_SHORT).show()
        }
        return super.onTouchEvent(event)
    }
}

data class Spendings(
    val id: Int,
    val name: String,
    val amount: Int,
    val category: String,
    val time: Long
)

data class CategoryEntity(
    val category: String,
    val amount: Int,
    val color: Int
)

/*
class PinChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    data class Spendings(
        val id: Int,
        val name: String,
        val amount: Int,
        val category: String,
        val time: Long
    )

    init {
        Data.makeSpendingsList(context)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}*/
