package com.co.brasso.feature.shared.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.co.brasso.R

class SpinnerAdapter(context: Context,var list:MutableList<String>,layout:Int) : ArrayAdapter<String>(context,layout,list) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    private fun createItemView(position: Int, recycledView: View?, parent: ViewGroup):View {
        val textView: TextView
        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.spinner_dropdown_item,
            parent,
            false
        )
        textView= view.findViewById<TextView>(R.id.txvSpinnerProfession)
        if(position==0){
          textView?.setTextColor(ContextCompat.getColor(context,R.color.tab_unselected))
        }else{
            textView?.setTextColor(ContextCompat.getColor(context,R.color.black))
        }
       textView.text= list[position]
        return view
    }
}


