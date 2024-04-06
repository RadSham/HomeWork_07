package otus.homework.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import otus.homework.customview.Data.makeSpendingsList


class CategoryGraphic @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private var colors: ArrayList<Int>
    private val spendingsList = ArrayList<Spendings>()
    private var maxValue = 0.0
    private var minValue = 0.0

    init {
        colors = getColors()
        spendingsList.clear()
        spendingsList.addAll(makeSpendingsList(context))
        println(spendingsList.size)

        maxValue = spendingsList.maxOf { it.amount.toDouble() }
        minValue = spendingsList.minOf { it.amount.toDouble() }
    }

    private val paint = Paint().apply {
        color = Color.parseColor("#bd7ebe")
        strokeWidth = 8f
        style = Paint.Style.STROKE
        pathEffect = CornerPathEffect(60f)
    }
    private val path = Path()
    private var scale = 1f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)

        when (wMode) {
            MeasureSpec.EXACTLY -> setMeasuredDimension(wSize, hSize)
            MeasureSpec.AT_MOST -> {
                val newW = Integer.min((spendingsList.size * 100.dp).toInt(), wSize)
                setMeasuredDimension(newW, hSize)
            }
            MeasureSpec.UNSPECIFIED -> setMeasuredDimension((spendingsList.size * 100.dp).toInt(), hSize)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val wStep = measuredWidth / spendingsList.size
        val hStep = measuredHeight / (maxValue - minValue)

        path.reset()
        path.moveTo(0f, 0f)
        var x = 0f
        var y = 0f
        path.lineTo(x, y)

        for (i in 0 until spendingsList.size) {
            y = ((spendingsList[i].amount.toDouble() - minValue) * hStep).toFloat()
            path.lineTo(x, y)
            println("${spendingsList[i].amount} $x $y")
            x += wStep
        }

        canvas.scale(scale, -scale, width / 2f, height / 2f)
        canvas.drawPath(path, paint)
    }
}