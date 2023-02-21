package com.co.brasso.utils.router

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.co.brasso.R
import com.co.brasso.utils.constant.BundleConstant
import java.io.Serializable
import kotlin.reflect.KClass

object Router {
    fun navigateActivity(context: Context?, finish: Boolean, className: KClass<*>) {
        context?.startActivity(Intent(context, className.java))

        (context as? AppCompatActivity)?.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )

        if (finish)
            (context as? AppCompatActivity)?.finish()
    }

    fun navigateActivityWithData(
        context: Context,
        finish: Boolean,
        keyName: String?,
        intentData: String?,
        className: KClass<*>
    ) {
        val intent = Intent(context, className.java)
        intent.action = Intent.ACTION_VIEW
        intent.putExtra(keyName, intentData)
        context.startActivity(intent)

        (context as? AppCompatActivity)?.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )

        if (finish)
            (context as? AppCompatActivity)?.finish()
    }

    fun navigateActivityWithIntData(
        context: Context,
        finish: Boolean,
        keyName: String?,
        intentData: Int?,
        className: KClass<*>
    ) {
        val intent = Intent(context, className.java)
        intent.action = Intent.ACTION_VIEW
        intent.putExtra(keyName, intentData)
        context.startActivity(intent)

        (context as? AppCompatActivity)?.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )

        if (finish)
            (context as? AppCompatActivity)?.finish()
    }

    fun navigateActivityWithBooleanData(
        context: Context,
        finish: Boolean,
        keyName: String?,
        intentData: Boolean?,
        className: KClass<*>
    ) {
        val intent = Intent(context, className.java)
        intent.action = Intent.ACTION_VIEW
        intent.putExtra(keyName, intentData)
        context.startActivity(intent)

        (context as? AppCompatActivity)?.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )

        if (finish)
            (context as? AppCompatActivity)?.finish()
    }

    fun navigateClearingAllActivity(context: Context?, className: KClass<*>) {
        val intent = Intent(context, className.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context?.startActivity(intent)

        (context as? AppCompatActivity)?.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    fun navigateClearingAllActivityWithData(context: Context?, className: KClass<*>) {
        val intent = Intent(context, className.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(BundleConstant.showIcon, false)
        context?.startActivity(intent)

        (context as? AppCompatActivity)?.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )

    }

    fun navigateActivityWithParcelableData(
        context: Context?,
        finish: Boolean,
        keyName: String,
        any: Serializable?,
        className: KClass<*>
    ) {
        val intent = Intent(context, className.java)
        intent.putExtra(keyName, any)
        context?.startActivity(intent)

        (context as? AppCompatActivity)?.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )

        if (finish)
            (context as? AppCompatActivity)?.finish()
    }

    fun navigateActivityWithParcelableDataLandscape(
        context: Context?,
        finish: Boolean,
        keyName: String,
        any: Serializable?,
        className: KClass<*>
    ) {
        val intent = Intent(context, className.java)
        intent.putExtra(keyName, any)
        context?.startActivity(intent)

        (context as? AppCompatActivity)?.overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )

        if (finish)
            (context as? AppCompatActivity)?.finish()
    }

    fun attachFragment(
        manager: FragmentManager?, layoutId: Int, fragment: Fragment?
    ) {
        if (fragment == null) {
            return
        }

        try {
            manager?.beginTransaction()
                ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                ?.replace(layoutId, fragment, fragment.javaClass.name)
                ?.addToBackStack(null)
                ?.commitAllowingStateLoss()
        } catch (e: IllegalArgumentException) {
            Log.d("Brasso", e.message.toString())
        }
    }

    fun openShare(message: String?, context: Context?) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.type = "text/plain"
        context?.startActivity(Intent.createChooser(intent, "Share To:"))
    }

    fun navigateFragment(
        navController: NavController?,
        destinationFragmentId: Int
    ) {

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)

        navController?.navigate(destinationFragmentId, null, navBuilder.build())
    }

    fun navigateFragmentWithData(
        navController: NavController?,
        destinationFragmentId: Int,
        bundle: Bundle
    ) {

        val navBuilder = NavOptions.Builder()
        navBuilder.setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)

        navController?.navigate(destinationFragmentId, bundle, navBuilder.build())
    }
}