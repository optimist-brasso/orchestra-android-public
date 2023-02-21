package com.co.brasso.feature.download

import android.database.Cursor

fun Cursor.column(which: String) = this.getColumnIndex(which)
fun Cursor.intValue(which: String): Int = this.getInt(column(which))
fun Cursor.floatValue(which: String): Float = this.getFloat(column(which))
fun Cursor.stringValue(which: String): String = this.getString(column(which))
fun Cursor.doubleValue(which: String): Double = this.getDouble(column(which))