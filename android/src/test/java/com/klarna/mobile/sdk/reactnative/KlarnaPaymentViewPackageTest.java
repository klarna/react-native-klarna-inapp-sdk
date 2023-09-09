package com.klarna.mobile.sdk.reactnative;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class KlarnaPaymentViewPackageTest {

    private Application application;
    private ReactApplicationContext reactContext;

    @Before
    public void setup() {
        application = ApplicationProvider.getApplicationContext();
        reactContext = new ReactApplicationContext(application);
    }

    @Test
    public void testCreateNativeModules() {
        KlarnaPaymentViewPackage paymentViewPackage = new KlarnaPaymentViewPackage();
        List<NativeModule> nativeModules = paymentViewPackage.createNativeModules(reactContext);
        Assert.assertNotNull(nativeModules);
        Assert.assertTrue(nativeModules.isEmpty());
    }

    @Test
    public void testCreateViewManagers() {
        KlarnaPaymentViewPackage paymentViewPackage = new KlarnaPaymentViewPackage();
        List<ViewManager> nativeModules = paymentViewPackage.createViewManagers(reactContext);
        Assert.assertNotNull(nativeModules);
        Assert.assertFalse(nativeModules.isEmpty());
        Assert.assertTrue(nativeModules.get(0) instanceof KlarnaPaymentViewManager);
    }
}
