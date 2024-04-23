package otus.homework.customview

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.lang.reflect.Type

object Data {
    fun makeCategoriesList(context: Context, colors: ArrayList<Int>): List<CategoryEntity> {
        val spendList = makeSpendingsList(context)
        val categoryMap = mutableMapOf<String, Int>()
        for (s in spendList) {
            categoryMap[s.category] = (categoryMap[s.category] ?: 0) + (s.amount)
        }
        val categoriesList = mutableListOf<CategoryEntity>()
        var catNum = 0
        categoryMap.forEach { entry ->
            categoriesList.add(CategoryEntity(entry.key, entry.value, colors[catNum], null))
            catNum++
        }
        return categoriesList.toList()
    }

    fun makeSpendingsList(context: Context): List<Spendings> {
        val spendList = mutableListOf<Spendings>()
        var br: BufferedReader? = null
        try {
            val inputStream = context.resources.openRawResource(R.raw.payload)
            br = BufferedReader(inputStream.reader())
            val type: Type = object : TypeToken<List<Spendings?>?>() {}.type
            spendList.addAll(GsonBuilder().create().fromJson(br, type))
        }
        //error when exception occurs
        catch (e: Exception) {
            Log.d("MyLog", "getAllWords exception: $e")
        } finally {
            br?.close()
        }
        return spendList
    }
}

fun getColors(): ArrayList<Int> {
    return arrayListOf(
        Color.GREEN, Color.CYAN, Color.MAGENTA, Color.BLUE, Color.RED,
        Color.YELLOW, Color.GRAY, Color.BLACK, Color.DKGRAY, Color.LTGRAY
    )
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
    val color: Int,
    val time: Long?
)
