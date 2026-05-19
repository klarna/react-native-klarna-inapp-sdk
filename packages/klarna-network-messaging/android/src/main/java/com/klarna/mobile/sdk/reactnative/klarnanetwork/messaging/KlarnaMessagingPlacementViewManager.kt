package com.klarna.mobile.sdk.reactnative.klarnanetwork.messaging

import android.os.Handler
import android.os.Looper
import android.view.Choreographer
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.facebook.react.bridge.ReactContext
import com.facebook.react.uimanager.PixelUtil
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import com.facebook.react.uimanager.annotations.ReactProp
import com.klarna.mobile.sdk.klarna.network.messaging.api.KlarnaMessagingPlacementView
import com.klarna.mobile.sdk.reactnative.klarnanetwork.core.KlarnaNetworkCoreModuleImpl

class KlarnaMessagingPlacementViewManager :
    RNKlarnaMessagingPlacementViewSpec<FrameLayout>() {

    companion object {
        const val REACT_CLASS = "RNKlarnaMessagingPlacementView"

        /**
         * Staggered post times (ms) for [reportFromActualLayout] retries. Messaging content
         * can load after the first layout pass, so we schedule extra measurements alongside
         * the pre-draw listener.
         */
        private val HEIGHT_CHECK_DELAYS_MS = longArrayOf(100, 500, 1500, 3000, 6000)
    }

    private val handler = Handler(Looper.getMainLooper())

    private val viewToInstanceId = mutableMapOf<FrameLayout, String>()
    private val viewToPlacementType = mutableMapOf<FrameLayout, String>()
    private val viewToTheme = mutableMapOf<FrameLayout, String>()
    private val viewToAmount = mutableMapOf<FrameLayout, String>()
    private val viewToCurrency = mutableMapOf<FrameLayout, String>()
    private val pendingRecreate = mutableMapOf<FrameLayout, Boolean>()
    // Pair the listener with the View it was registered on. removeLayoutListener
    // must remove from the same ViewTreeObserver — looking up wrapper.getChildAt(0)
    // is unreliable: recreatePlacementView calls removeAllViews() before
    // removeLayoutListener, leaving childCount == 0 and orphaning the listener.
    private val preDrawListeners = mutableMapOf<FrameLayout, Pair<View, ViewTreeObserver.OnPreDrawListener>>()
    private val lastReportedHeight = mutableMapOf<FrameLayout, Int>()

    override fun getName(): String = REACT_CLASS

    override fun createViewInstance(themedReactContext: ThemedReactContext): FrameLayout {
        return FrameLayout(themedReactContext)
    }

    /** Cleans up listeners, pending handler callbacks, and prop maps when React drops the view. */
    override fun onDropViewInstance(view: FrameLayout) {
        removeLayoutListener(view)
        handler.removeCallbacksAndMessages(view)
        pendingRecreate.remove(view)
        viewToInstanceId.remove(view)
        viewToPlacementType.remove(view)
        viewToTheme.remove(view)
        viewToAmount.remove(view)
        viewToCurrency.remove(view)
        lastReportedHeight.remove(view)
        super.onDropViewInstance(view)
    }

    @ReactProp(name = "instanceId")
    override fun setInstanceId(view: FrameLayout, value: String?) {
        if (value != null) viewToInstanceId[view] = value else viewToInstanceId.remove(view)
        scheduleRecreate(view)
    }

    @ReactProp(name = "placementType")
    override fun setPlacementType(view: FrameLayout, value: String?) {
        if (value != null) viewToPlacementType[view] = value else viewToPlacementType.remove(view)
        scheduleRecreate(view)
    }

    @ReactProp(name = "theme")
    override fun setTheme(view: FrameLayout, value: String?) {
        if (value != null) viewToTheme[view] = value else viewToTheme.remove(view)
        scheduleRecreate(view)
    }

    @ReactProp(name = "amount")
    override fun setAmount(view: FrameLayout, value: String?) {
        if (value != null) viewToAmount[view] = value else viewToAmount.remove(view)
        scheduleRecreate(view)
    }

    @ReactProp(name = "currency")
    override fun setCurrency(view: FrameLayout, value: String?) {
        if (value != null) viewToCurrency[view] = value else viewToCurrency.remove(view)
        scheduleRecreate(view)
    }

    override fun getExportedCustomDirectEventTypeConstants(): Map<String, Any> =
        mapOf(
            "onError" to mapOf("registrationName" to "onError"),
            "onResized" to mapOf("registrationName" to "onResized")
        )

    /**
     * Schedules placement view recreation for the next frame so React prop batches
     * can settle first. Subsequent prop changes within the same frame coalesce into
     * a single recreate pass.
     */
    private fun scheduleRecreate(wrapper: FrameLayout) {
        if (pendingRecreate[wrapper] == true) return
        pendingRecreate[wrapper] = true
        Choreographer.getInstance().postFrameCallback {
            pendingRecreate.remove(wrapper)
            recreatePlacementView(wrapper)
        }
    }

    /**
     * Tears down any existing placement view inside [wrapper] and builds a fresh one
     * from the latest React props. Returns silently when amount, currency, or
     * instanceId are missing; dispatches a `NotInitialized` error event when the
     * Klarna instance can't be resolved. After adding the new SDK view it attaches
     * a measurement observer so height changes are reported back to React.
     */
    private fun recreatePlacementView(wrapper: FrameLayout) {
        val amount = KlarnaMessagingViewManagerHelpers.parseAmount(viewToAmount[wrapper]) ?: return
        val currency = viewToCurrency[wrapper] ?: return
        if (currency.isEmpty()) return

        removeLayoutListener(wrapper)
        wrapper.removeAllViews()
        handler.removeCallbacksAndMessages(wrapper)
        lastReportedHeight.remove(wrapper)

        val instanceId = viewToInstanceId[wrapper]
        if (instanceId.isNullOrEmpty()) return
        val klarnaInstance = KlarnaNetworkCoreModuleImpl.getInstance(instanceId)
        if (klarnaInstance == null) {
            dispatchError(
                wrapper,
                "NotInitialized",
                "No Klarna instance found for instanceId. Call Klarna.initialize() first."
            )
            return
        }

        val theme = KlarnaMessagingViewManagerHelpers.parseTheme(viewToTheme[wrapper])
        val configuration = KlarnaMessagingViewManagerHelpers.buildConfiguration(
            viewToPlacementType[wrapper],
            theme,
            amount,
            currency
        )

        val placementView = KlarnaMessagingPlacementView(wrapper.context, klarnaInstance, configuration)
        wrapper.addView(placementView, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ))
        attachMeasurementObserver(wrapper, placementView)
    }

    /**
     * Registers two complementary height-tracking strategies for [placementView]:
     * 1. An [ViewTreeObserver.OnPreDrawListener] that fires on every layout pass.
     * 2. A series of staggered [Handler] callbacks ([HEIGHT_CHECK_DELAYS_MS]) to catch
     *    asynchronous content loads that may not trigger a layout pass immediately.
     */
    private fun attachMeasurementObserver(wrapper: FrameLayout, placementView: View) {
        val listener = ViewTreeObserver.OnPreDrawListener {
            reportFromActualLayout(wrapper, placementView)
            true
        }
        placementView.viewTreeObserver.addOnPreDrawListener(listener)
        preDrawListeners[wrapper] = placementView to listener

        for ((index, delay) in HEIGHT_CHECK_DELAYS_MS.withIndex()) {
            val isLastCheck = index == HEIGHT_CHECK_DELAYS_MS.size - 1
            val runnable = Runnable {
                reportFromActualLayout(wrapper, placementView)
                if (isLastCheck && lastReportedHeight[wrapper] == null) {
                    dispatchResized(wrapper, 0)
                }
            }
            handler.postAtTime(runnable, wrapper, android.os.SystemClock.uptimeMillis() + delay)
        }
    }

    /**
     * Manually measures [placementView] against [wrapper]'s width with unconstrained height,
     * then dispatches an `onResized` event if the content height (in dp) changed since the
     * last report. A manual measure is needed because React Native sizes the wrapper via
     * `setFrame()`, so the SDK view never gets a standard measure pass with the real width.
     */
    private fun reportFromActualLayout(wrapper: FrameLayout, placementView: View) {
        if (!placementView.isAttachedToWindow) return

        val width = wrapper.width
        if (width <= 0) return

        val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        placementView.measure(widthSpec, heightSpec)
        placementView.layout(0, 0, placementView.measuredWidth, placementView.measuredHeight)

        val contentHeight = placementView.measuredHeight
        if (contentHeight <= 0) return

        val heightDp = Math.round(PixelUtil.toDIPFromPixel(contentHeight.toFloat()))
        if (!KlarnaMessagingViewManagerHelpers.shouldReportHeight(lastReportedHeight[wrapper], heightDp)) {
            return
        }
        lastReportedHeight[wrapper] = heightDp
        dispatchResized(wrapper, heightDp)
    }

    private fun dispatchResized(wrapper: FrameLayout, heightDp: Int) {
        val dispatcher = resolveDispatcher(wrapper) ?: return
        val surfaceId = UIManagerHelper.getSurfaceId(wrapper)
        dispatcher.dispatchEvent(
            KlarnaMessagingPlacementResizedEvent(surfaceId, wrapper.id, heightDp.toString())
        )
    }

    private fun dispatchError(wrapper: FrameLayout, name: String, message: String) {
        val dispatcher = resolveDispatcher(wrapper) ?: return
        val surfaceId = UIManagerHelper.getSurfaceId(wrapper)
        dispatcher.dispatchEvent(
            KlarnaMessagingPlacementErrorEvent(surfaceId, wrapper.id, name, message)
        )
    }

    private fun resolveDispatcher(wrapper: FrameLayout) =
        UIManagerHelper.getEventDispatcherForReactTag(
            wrapper.context as ReactContext,
            wrapper.id
        )

    /**
     * Removes the pre-draw listener previously registered for [wrapper]. The listener is
     * removed from the [ViewTreeObserver] of the original view it was attached to (stored
     * in [preDrawListeners]), not the current child, because [recreatePlacementView] may
     * have already called `removeAllViews()`.
     */
    private fun removeLayoutListener(wrapper: FrameLayout) {
        val (target, listener) = preDrawListeners.remove(wrapper) ?: return
        val observer = target.viewTreeObserver
        if (observer.isAlive) {
            observer.removeOnPreDrawListener(listener)
        }
    }
}
