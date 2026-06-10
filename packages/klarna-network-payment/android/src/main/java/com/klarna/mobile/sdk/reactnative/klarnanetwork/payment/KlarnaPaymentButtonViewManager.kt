package com.klarna.mobile.sdk.reactnative.klarnanetwork.payment

import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.Choreographer
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.klarna.mobile.sdk.api.KlarnaTheme
import com.klarna.mobile.sdk.api.button.KlarnaButtonShape
import com.klarna.mobile.sdk.api.button.KlarnaButtonStyle
import com.klarna.mobile.sdk.klarna.network.core.api.button.KlarnaButtonState
import com.klarna.mobile.sdk.klarna.network.payment.button.api.KlarnaPaymentButton
import com.klarna.mobile.sdk.klarna.network.payment.button.api.KlarnaPaymentButtonConfiguration
import com.klarna.mobile.sdk.klarna.network.payment.button.api.KlarnaPaymentButtonIntent
import com.klarna.mobile.sdk.reactnative.klarnanetwork.core.KlarnaNetworkCoreModuleImpl

class KlarnaPaymentButtonViewManager : KlarnaNetworkPaymentButtonViewSpec<FrameLayout>() {

    companion object {
        const val REACT_CLASS = "KlarnaNetworkPaymentButton"
        // Staggered re-measure delays to catch async content loads inside KlarnaPaymentButton.
        private val LAYOUT_RETRY_DELAYS_MS = longArrayOf(100, 500, 1500)
    }

    private val handler = Handler(Looper.getMainLooper())
    private val viewStates = mutableMapOf<FrameLayout, ViewState>()

    override fun getName(): String = REACT_CLASS

    override fun createViewInstance(context: ThemedReactContext): FrameLayout {
        val view = FrameLayout(context)
        viewStates[view] = ViewState()
        return view
    }

    override fun setInstanceId(view: FrameLayout?, value: String?) {
        view ?: return
        if (viewStates[view]?.instanceId == value) return
        viewStates[view]?.instanceId = value
        scheduleButtonCreation(view)
    }

    override fun setState(view: FrameLayout?, value: String?) {
        view ?: return
        viewStates[view]?.state = value
        val button = view.getChildAt(0) as? KlarnaPaymentButton
        if (button != null) {
            // State can be updated in-place without recreating the button.
            button.state = parseState(value)
        } else {
            scheduleButtonCreation(view)
        }
    }

    override fun setIntent(view: FrameLayout?, value: String?) {
        view ?: return
        viewStates[view]?.intent = value
        scheduleButtonCreation(view)
    }

    override fun setShape(view: FrameLayout?, value: String?) {
        view ?: return
        viewStates[view]?.shape = value
        scheduleButtonCreation(view)
    }

    override fun setButtonStyle(view: FrameLayout?, value: String?) {
        view ?: return
        viewStates[view]?.buttonStyle = value
        scheduleButtonCreation(view)
    }

    override fun setTheme(view: FrameLayout?, value: String?) {
        view ?: return
        viewStates[view]?.theme = value
        scheduleButtonCreation(view)
    }

    override fun onDropViewInstance(view: FrameLayout) {
        tearDownButton(view)
        viewStates.remove(view)
        super.onDropViewInstance(view)
    }

    private fun tearDownButton(view: FrameLayout) {
        removePreDrawListener(view)
        handler.removeCallbacksAndMessages(view)
        view.removeAllViews()
    }

    private fun removePreDrawListener(view: FrameLayout) {
        val state = viewStates[view] ?: return
        val (button, listener) = state.preDrawListener ?: return
        state.preDrawListener = null
        val observer = button.viewTreeObserver
        if (observer.isAlive) observer.removeOnPreDrawListener(listener)
    }

    private fun scheduleButtonCreation(view: FrameLayout) {
        val state = viewStates[view] ?: return
        if (state.pendingCreate) return
        state.pendingCreate = true
        Choreographer.getInstance().postFrameCallback {
            state.pendingCreate = false
            tearDownButton(view)
            createButton(view)
        }
    }

    private fun nextGeneration(view: FrameLayout): Int {
        val state = viewStates[view] ?: return 0
        return ++state.generation
    }

    private fun isCurrentGeneration(view: FrameLayout, generation: Int): Boolean =
        viewStates[view]?.generation == generation

    private fun createButton(view: FrameLayout) {
        val state = viewStates[view] ?: return
        val instanceId = state.instanceId ?: return
        val sdk = KlarnaNetworkCoreModuleImpl.getInstance(instanceId) ?: return

        val configuration = KlarnaPaymentButtonConfiguration(
            intent = parseIntent(state.intent),
            state = parseState(state.state),
            shape = parseShape(state.shape),
            style = parseStyle(state.buttonStyle),
            theme = parseTheme(state.theme),
        )

        try {
            val button = KlarnaPaymentButton(view.context, sdk, configuration)
            button.setOnClickListener {
                val reactContext = view.context as? ReactContext ?: return@setOnClickListener
                UIManagerHelper.getEventDispatcherForReactTag(reactContext, view.id)
                    ?.dispatchEvent(KlarnaPaymentButtonPressEvent(UIManagerHelper.getSurfaceId(view), view.id))
            }
            view.addView(button, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            ))
            val generation = nextGeneration(view)
            setupManualLayout(view, button, generation)
        } catch (e: Exception) {
            android.util.Log.e(REACT_CLASS, "Failed to create KlarnaPaymentButton", e)
        }
    }

    private fun setupManualLayout(view: FrameLayout, button: KlarnaPaymentButton, generation: Int) {
        // Phase 1: frame-by-frame polling until the button height stabilises.
        // Capped at maxFrames to prevent an indefinite loop if the button never reports
        // a positive height (e.g. SDK error or missing data).
        val maxFrames = 90
        var frameCount = 0
        val lastHeightPx = intArrayOf(-1)
        val stableFrameCount = intArrayOf(0)
        val frameCallback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                if (!isCurrentGeneration(view, generation) || !button.isAttachedToWindow) return
                frameCount++
                measureAndLayout(view, button)
                val currentHeightPx = button.height
                when {
                    currentHeightPx <= 0 -> stableFrameCount[0] = 0
                    currentHeightPx == lastHeightPx[0] -> stableFrameCount[0]++
                    else -> stableFrameCount[0] = 0
                }
                lastHeightPx[0] = currentHeightPx
                if (frameCount < maxFrames && (currentHeightPx <= 0 || stableFrameCount[0] < 3)) {
                    Choreographer.getInstance().postFrameCallback(this)
                }
            }
        }
        Choreographer.getInstance().postFrameCallback(frameCallback)

        // Phase 2: OnPreDrawListener catches any subsequent layout changes once the
        // button's internal content finishes loading and triggers a redraw.
        val preDrawListener = ViewTreeObserver.OnPreDrawListener {
            if (isCurrentGeneration(view, generation) && button.isAttachedToWindow) {
                measureAndLayout(view, button)
            }
            true
        }
        button.viewTreeObserver.addOnPreDrawListener(preDrawListener)
        viewStates[view]?.preDrawListener = button to preDrawListener

        // Phase 3: staggered re-measures to catch async content loads that may not
        // trigger a layout pass on their own (same pattern as KlarnaMessagingPlacementViewManager).
        for (delay in LAYOUT_RETRY_DELAYS_MS) {
            handler.postAtTime({
                if (isCurrentGeneration(view, generation) && button.isAttachedToWindow) {
                    measureAndLayout(view, button)
                }
            }, view, SystemClock.uptimeMillis() + delay)
        }
    }

    private fun measureAndLayout(view: FrameLayout, button: KlarnaPaymentButton) {
        val containerWidth = view.measuredWidth
        val containerHeight = view.measuredHeight
        if (containerWidth <= 0) return
        // First pass: unspecified height so the button reports its natural minimum.
        button.measure(
            View.MeasureSpec.makeMeasureSpec(containerWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        )
        // naturalHeight must be read AFTER measure() so it reflects the button's actual content.
        val naturalHeight = button.measuredHeight
        // Button content not yet loaded — bail out and let Phase 1/3 retry.
        if (naturalHeight <= 0) return
        // Grow freely with the container, but never shrink below the button's natural minimum.
        val height = maxOf(containerHeight, naturalHeight)
        // Second pass: commit the resolved height so the button lays itself out correctly.
        button.measure(
            View.MeasureSpec.makeMeasureSpec(containerWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY),
        )
        button.layout(0, 0, button.measuredWidth, button.measuredHeight)
        button.requestLayout()
    }

    // region Prop parsing

    internal fun parseState(value: String?): KlarnaButtonState = when (value) {
        "disabled" -> KlarnaButtonState.DISABLED
        "loading" -> KlarnaButtonState.LOADING
        else -> KlarnaButtonState.DEFAULT
    }

    internal fun parseIntent(value: String?): KlarnaPaymentButtonIntent = when (value) {
        "subscribe" -> KlarnaPaymentButtonIntent.SUBSCRIBE
        "addToWallet" -> KlarnaPaymentButtonIntent.ADD_TO_WALLET
        else -> KlarnaPaymentButtonIntent.PAY
    }

    internal fun parseShape(value: String?): KlarnaButtonShape = when (value) {
        "roundedRect" -> KlarnaButtonShape.ROUNDED_RECT
        "pill" -> KlarnaButtonShape.PILL
        "rectangle" -> KlarnaButtonShape.RECTANGLE
        else -> KlarnaButtonShape.ROUNDED_RECT
    }

    internal fun parseStyle(value: String?): KlarnaButtonStyle = when (value) {
        "filled" -> KlarnaButtonStyle.FILLED
        "outlined" -> KlarnaButtonStyle.OUTLINED
        else -> KlarnaButtonStyle.FILLED
    }

    internal fun parseTheme(value: String?): KlarnaTheme = when (value) {
        "light" -> KlarnaTheme.LIGHT
        "dark" -> KlarnaTheme.DARK
        "automatic" -> KlarnaTheme.AUTOMATIC
        else -> KlarnaTheme.DARK
    }

    // endregion

    private data class ViewState(
        var instanceId: String? = null,
        var state: String? = null,
        var intent: String? = null,
        var shape: String? = null,
        var buttonStyle: String? = null,
        var theme: String? = null,
        var pendingCreate: Boolean = false,
        var generation: Int = 0,
        var preDrawListener: Pair<KlarnaPaymentButton, ViewTreeObserver.OnPreDrawListener>? = null,
    )
}
