package com.klarna.mobile.sdk.reactnative.payments;

import static com.klarna.mobile.sdk.reactnative.payments.KlarnaPaymentViewManager.COMMAND_AUTHORIZE;
import static com.klarna.mobile.sdk.reactnative.payments.KlarnaPaymentViewManager.COMMAND_FINALIZE;
import static com.klarna.mobile.sdk.reactnative.payments.KlarnaPaymentViewManager.COMMAND_INITIALIZE;
import static com.klarna.mobile.sdk.reactnative.payments.KlarnaPaymentViewManager.COMMAND_LOAD;
import static com.klarna.mobile.sdk.reactnative.payments.KlarnaPaymentViewManager.COMMAND_LOAD_PAYMENT_REVIEW;
import static com.klarna.mobile.sdk.reactnative.payments.KlarnaPaymentViewManager.COMMAND_REAUTHORIZE;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;

import java.util.Collections;

@PrepareForTest({Arguments.class})
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "androidx.*", "android.*", "com.android.*"})
public class KlarnaPaymentViewManagerTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private KlarnaPaymentViewManager manager;
    private PaymentViewWrapper wrapper;

    @Before
    public void setup() {
        Application application = ApplicationProvider.getApplicationContext();
        ReactApplicationContext reactContext = new ReactApplicationContext(application);

        manager = new KlarnaPaymentViewManager(reactContext);

        wrapper = Mockito.mock(PaymentViewWrapper.class);
        wrapper.paymentView = Mockito.mock(KlarnaPaymentView.class);

        PowerMockito.mockStatic(Arguments.class);
        PowerMockito.when(Arguments.createArray())
                .thenAnswer(invocation -> new JavaOnlyArray());
        PowerMockito.when(Arguments.createMap())
                .thenAnswer(invocation -> new JavaOnlyMap());
    }

    @After
    public void teardown() {
        Mockito.reset(wrapper, wrapper.paymentView);
    }

    @Test
    public void testReactClassName() {
        Assert.assertEquals("RNKlarnaPaymentView", manager.getName());
        Assert.assertEquals("RNKlarnaPaymentView", KlarnaPaymentViewManager.REACT_CLASS);
    }

    @Test
    public void testSetCategory() {
        manager.setCategory(wrapper, "testCategory");
        Mockito.verify(wrapper.paymentView).setCategory("testCategory");
    }

    @Test
    public void testSetReturnUrl() {
        manager.setReturnUrl(wrapper, "testReturnUrl://");
        Mockito.verify(wrapper.paymentView).setReturnURL("testReturnUrl://");
    }

    @Test
    public void testReceiveCommandInitialize() {
        manager.receiveCommand(wrapper, COMMAND_INITIALIZE, null);
        Mockito.verify(wrapper.paymentView, Mockito.times(0)).initialize(Mockito.anyString(), Mockito.anyString());
        Mockito.verify(wrapper.paymentView, Mockito.times(0)).initialize(null, null);

        final JavaOnlyArray args = new JavaOnlyArray();
        args.pushString("testToken");
        args.pushString("testReturnUrl");
        manager.receiveCommand(wrapper, COMMAND_INITIALIZE, args);
        Mockito.verify(wrapper.paymentView, Mockito.times(1)).initialize("testToken", "testReturnUrl");
    }

    public void testReceiveCommandInitializeThrows() {
        final JavaOnlyArray args = new JavaOnlyArray();
        manager.receiveCommand(wrapper, COMMAND_INITIALIZE, args);
        Mockito.verify(wrapper.paymentView, Mockito.times(0)).initialize(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testReceiveCommandLoad() {
        manager.receiveCommand(wrapper, COMMAND_LOAD, null);
        Mockito.verify(wrapper, Mockito.times(1)).load(null);

        final JavaOnlyArray args = new JavaOnlyArray();
        args.pushString("testSessionData");
        manager.receiveCommand(wrapper, COMMAND_LOAD, args);
        Mockito.verify(wrapper, Mockito.times(1)).load("testSessionData");
    }

    @Test
    public void testReceiveCommandLoadPaymentReview() {
        manager.receiveCommand(wrapper, COMMAND_LOAD_PAYMENT_REVIEW, null);
        Mockito.verify(wrapper.paymentView, Mockito.times(1)).loadPaymentReview();
    }

    @Test
    public void testReceiveCommandAuthorize() {
        manager.receiveCommand(wrapper, COMMAND_AUTHORIZE, null);
        Mockito.verify(wrapper.paymentView, Mockito.times(1)).authorize(Mockito.anyBoolean(), (String) Mockito.isNull());

        final JavaOnlyArray args = new JavaOnlyArray();
        args.pushBoolean(true);
        args.pushString("testSessionData");
        manager.receiveCommand(wrapper, COMMAND_AUTHORIZE, args);
        Mockito.verify(wrapper.paymentView, Mockito.times(1)).authorize(true, "testSessionData");
    }

    public void testReceiveCommandAuthorizeThrows() {
        final JavaOnlyArray args = new JavaOnlyArray();
        args.pushBoolean(true);
        manager.receiveCommand(wrapper, COMMAND_AUTHORIZE, args);
        Mockito.verify(wrapper.paymentView, Mockito.times(1)).authorize(true, null);
    }

    @Test
    public void testReceiveCommandReauthorize() {
        manager.receiveCommand(wrapper, COMMAND_REAUTHORIZE, null);
        Mockito.verify(wrapper.paymentView, Mockito.times(1)).reauthorize(null);

        final JavaOnlyArray args = new JavaOnlyArray();
        args.pushString("testSessionData");
        manager.receiveCommand(wrapper, COMMAND_REAUTHORIZE, args);
        Mockito.verify(wrapper.paymentView, Mockito.times(1)).reauthorize("testSessionData");
    }

    @Test
    public void testReceiveCommandFinalize() {
        manager.receiveCommand(wrapper, COMMAND_FINALIZE, null);
        Mockito.verify(wrapper.paymentView, Mockito.times(1)).finalize(null);

        final JavaOnlyArray args = new JavaOnlyArray();
        args.pushString("testSessionData");
        manager.receiveCommand(wrapper, COMMAND_FINALIZE, args);
        Mockito.verify(wrapper.paymentView, Mockito.times(1)).finalize("testSessionData");
    }

    @Test
    public void testErrorMapFieldsAndValues() {
        KlarnaPaymentsSDKError error = new KlarnaPaymentsSDKError(
                "testName",
                "testMessage",
                false,
                "testAction",
                Collections.singletonList("testInvalidField"),
                "testSessionId"
        );

        ReadableMap map = manager.buildErrorMap(error);

        Assert.assertEquals("testName", map.getString("name"));
        Assert.assertEquals("testMessage", map.getString("message"));
        Assert.assertFalse(map.getBoolean("isFatal"));
        Assert.assertEquals("testAction", map.getString("action"));

        ReadableArray invalidFields = map.getArray("invalidFields");
        Assert.assertNotNull(invalidFields);
        Assert.assertEquals(1, invalidFields.size());
        Assert.assertEquals("testInvalidField", invalidFields.getString(0));

        Assert.assertEquals("testSessionId", map.getString("sessionId"));
    }
}
