package com.klarna.mobile.sdk.reactnative.klarnanetwork.core

import android.util.Log
import androidx.annotation.RestrictTo
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.klarna.mobile.sdk.klarna.network.core.api.common.KlarnaResult
import com.klarna.mobile.sdk.klarna.network.core.api.common.KlarnaSDKError
import com.klarna.mobile.sdk.klarna.network.core.api.klarna.Klarna
import com.klarna.mobile.sdk.klarna.network.core.api.klarna.KlarnaConfiguration
import com.klarna.mobile.sdk.klarna.network.core.api.metadata.KlarnaIntegrationMetadata
import com.klarna.mobile.sdk.klarna.network.core.api.metadata.KlarnaIntegratorMetadata
import com.klarna.mobile.sdk.klarna.network.core.api.metadata.KlarnaOriginatorMetadata
import java.util.concurrent.ConcurrentHashMap

class KlarnaNetworkCoreModuleImpl(
    private val reactContext: ReactApplicationContext,
    private val klarnaFactory: (ReactApplicationContext, KlarnaConfiguration) -> KlarnaResult<Klarna, KlarnaSDKError> = { ctx, cfg ->
        Klarna.initialize(ctx, cfg)
    }
) {

    companion object {
        const val NAME = "KlarnaNetworkCore"
        private const val KEY_CLIENT_ID = "clientId"
        private const val KEY_ACCOUNT_ID = "accountId"
        private const val KEY_LOCALE = "locale"
        private const val KEY_KLARNA_NETWORK_SESSION_TOKEN = "klarnaNetworkSessionToken"
        private const val KEY_INTEGRATOR = "integrator"
        private const val KEY_ORIGINATORS = "originators"
        private const val KEY_NAME = "name"
        private const val KEY_SESSION_REFERENCE = "sessionReference"
        private const val KEY_MODULE_NAME = "moduleName"
        private const val KEY_MODULE_VERSION = "moduleVersion"
        private const val ERROR_INSTANCE_NOT_FOUND = "No instance found for the given instanceId. Call initialize first."

        // Sibling network packages (messaging, payment, …) look up the
        // Klarna instance by instanceId received over the JS bridge.
        // Companion-static so the lookup is reachable without an instance of
        // the module class. The visibility is broader than ideal — it should
        // be migrated to a @RequiresOptIn-gated API alongside the iOS @_spi
        // tag once that work lands on core.
        @JvmStatic
        val instances = ConcurrentHashMap<String, Klarna>()

        @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
        fun getInstance(instanceId: String): Klarna? = instances[instanceId]
    }

    fun initialize(instanceId: String, configuration: ReadableMap?, promise: Promise?) {
        val clientId = configuration?.getString(KEY_CLIENT_ID) ?: run {
            promise?.reject(NAME, "clientId is required")
            return
        }
        // appReturnUrl from KlarnaConfiguration type is ignored on android side since is not needed.
        val config = KlarnaConfiguration(
            clientId = clientId,
            accountId = configuration.getString(KEY_ACCOUNT_ID),
            locale = configuration.getString(KEY_LOCALE),
            klarnaNetworkSessionToken = configuration.getString(KEY_KLARNA_NETWORK_SESSION_TOKEN)
        )
        when (val result = klarnaFactory(reactContext, config)) {
            is KlarnaResult.Success -> {
                instances[instanceId] = result.value
                promise?.resolve(null)
            }
            is KlarnaResult.Failure -> {
                promise?.reject(result.error.name, result.error.message)
            }
        }
    }

    fun getSessionToken(instanceId: String, promise: Promise?) {
        val klarna = instances[instanceId] ?: run {
            promise?.reject(NAME, ERROR_INSTANCE_NOT_FOUND)
            return
        }
        klarna.network.session.token { result ->
            when (result) {
                is KlarnaResult.Success -> promise?.resolve(result.value)
                is KlarnaResult.Failure -> promise?.reject(result.error.name, result.error.message)
            }
        }
    }

    fun clearSession(instanceId: String, promise: Promise?) {
        val klarna = instances[instanceId] ?: run {
            promise?.reject(NAME, ERROR_INSTANCE_NOT_FOUND)
            return
        }
        klarna.network.session.clear { result ->
            when (result) {
                is KlarnaResult.Success -> promise?.resolve(null)
                is KlarnaResult.Failure -> promise?.reject(result.error.name, result.error.message)
            }
        }
    }

    fun handleReturnUrl(url: String?, promise: Promise?) {
        Log.d(NAME, "handleReturnUrl is not supported on Android.")
        promise?.resolve(false)
    }

    fun setIntegrationMetadata(instanceId: String, metadata: ReadableMap?) {
        val klarna = instances[instanceId] ?: run {
            Log.d(NAME, "setIntegrationMetadata: no instance found for instanceId: $instanceId")
            return
        }
        val integratorMap = metadata?.getMap(KEY_INTEGRATOR) ?: run {
            Log.d(NAME, "setIntegrationMetadata: integrator map is missing")
            return
        }
        val integrator = KlarnaIntegratorMetadata(
            name = integratorMap.getString(KEY_NAME) ?: run {
                Log.d(NAME, "setIntegrationMetadata: integrator name is missing")
                return
            },
            sessionReference = integratorMap.getString(KEY_SESSION_REFERENCE) ?: run {
                Log.d(NAME, "setIntegrationMetadata: integrator sessionReference is missing")
                return
            },
            moduleName = integratorMap.getString(KEY_MODULE_NAME),
            moduleVersion = integratorMap.getString(KEY_MODULE_VERSION)
        )
        val originatorsArray: ReadableArray? = metadata.getArray(KEY_ORIGINATORS)
        val originators = originatorsArray?.let { arr ->
            (0 until arr.size()).mapNotNull { i ->
                arr.getMap(i)?.let { origMap ->
                    KlarnaOriginatorMetadata(
                        name = origMap.getString(KEY_NAME) ?: return@mapNotNull null,
                        sessionReference = origMap.getString(KEY_SESSION_REFERENCE) ?: return@mapNotNull null,
                        moduleName = origMap.getString(KEY_MODULE_NAME),
                        moduleVersion = origMap.getString(KEY_MODULE_VERSION)
                    )
                }
            }
        }
        klarna.integrationMetadata = KlarnaIntegrationMetadata(integrator, originators)
    }

    fun dispose(instanceId: String, promise: Promise?) {
        instances.remove(instanceId)
        promise?.resolve(null)
    }
}
