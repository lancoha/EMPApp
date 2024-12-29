package com.example.empapp

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.Locale

class MyMarkerView(context: Context, layoutResource: Int, private val data: List<Pair<String, Entry>>) :
    MarkerView(context, layoutResource) {

    private val textView: TextView = findViewById(R.id.marker_text)
    private val dateFormat = SimpleDateFormat("d.M.yyyy", Locale.getDefault())

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            val index = e.x.toInt()
            if (index in data.indices) {
                val date = data[index].first
                val price = e.y
                textView.text = "Datum: ${dateFormat.format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)!!)}\nCena: $$price"
            }
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}
