package com.co.brasso.feature.shared.listener

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

abstract class AppBarStateChangeListener : AppBarLayout.OnOffsetChangedListener {
    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    var mCurrentState = State.IDLE

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, i: Int) {
        when {
            i == 0 -> {
                if (mCurrentState != State.EXPANDED) {
                    onStateChanged(appBarLayout,
                        State.EXPANDED
                    )
                }
                mCurrentState =
                    State.EXPANDED
            }
            abs(i) >= appBarLayout?.totalScrollRange!! -> {
                if (mCurrentState != State.COLLAPSED) {
                    onStateChanged(appBarLayout,
                        State.COLLAPSED
                    )
                }
                mCurrentState =
                    State.COLLAPSED
            }
            else -> {
                if (mCurrentState != State.IDLE) {
                    onStateChanged(appBarLayout,
                        State.IDLE
                    )
                }
                mCurrentState =
                    State.IDLE
            }
        }
    }

    abstract fun onStateChanged(appBarLayout: AppBarLayout?, state: State?)

}