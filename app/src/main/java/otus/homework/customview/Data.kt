package otus.homework.customview

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.lang.reflect.Type

object Data {
    fun makeCategoriesList(context: Context, colors: ArrayList<Int>): List<CategoryEntity> {
        val tempList = mutableListOf<Spendings>()
        var br: BufferedReader? = null
        try {
            val inputStream = context.resources.openRawResource(R.raw.payload)
            br = BufferedReader(inputStream.reader())
            val type: Type = object : TypeToken<List<Spendings?>?>() {}.type
            tempList.addAll(GsonBuilder().create().fromJson(br, type))
        }
        //error when exception occurs
        catch (e: Exception) {
            Log.d("MyLog", "getAllWords exception: $e")
        } finally {
            br?.close()
        }
        val categoryMap = mutableMapOf<String, Int>()
        for (s in tempList) {
            categoryMap[s.category] = s.amount
        }
        val categoriesList = mutableListOf<CategoryEntity>()
        var catNum = 0
        categoryMap.forEach{entry ->
            categoriesList.add(CategoryEntity(entry.key, entry.value, colors[catNum]))
            catNum++
        }
        println(categoriesList)
        return categoriesList.toList()
    }
}