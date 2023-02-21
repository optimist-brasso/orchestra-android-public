package com.co.brasso.utils.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.preference.PreferenceManager
import com.co.brasso.utils.constant.Constants
import java.util.*

class LocaleHelper {
    companion object {
        private const val SELECTED_LANGUAGE = "Language.Helper.Selected.Language"

        // returns Context having application default locale for all activities
        fun onAttach(context: Context): Context {
            val lang = getPersistedLanguage(context, Locale.getDefault().language)
            return setLanguage(context, lang)
        }

        // returns language code persisted in preference manager
        fun getLanguage(context: Context): String {
            return getPersistedLanguage(context, Locale.getDefault().language)
        }

        // persists new language code change in preference manager and updates application default locale
        // returns Context having application default locale
        fun setLanguage(context: Context, language: String): Context {
            persist(context, language)
            val locale = getLocale(language)
            Locale.setDefault(locale)
            val configuration = Configuration()
            val displayMetrics = context.resources.displayMetrics
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                configuration.setLocale(locale)
            } else {
                configuration.locale = locale
            }
            context.resources.updateConfiguration(configuration, displayMetrics)
            context.applicationContext.resources.updateConfiguration(configuration, displayMetrics)
            return context
        }

        // returns language code persisted in preference manager
        private fun getPersistedLanguage(context: Context, defaultLanguage: String): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(SELECTED_LANGUAGE, defaultLanguage) ?: Constants.englishCode
        }

        // persists new language code in preference manager
        private fun persist(context: Context, language: String?) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()
            editor.putString(SELECTED_LANGUAGE, language)
            editor.apply()
        }

        fun isNepali(context: Context) = LocaleHelper.getLanguage(context).equals(Constants.japanCode)

        private fun getLocale(splitLocaleCode: String) =
            if (splitLocaleCode.contains("_")) {
                val arg = splitLocaleCode.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                Locale(arg[0], arg[1])
            } else {
                Locale(splitLocaleCode)
            }
    }
}
