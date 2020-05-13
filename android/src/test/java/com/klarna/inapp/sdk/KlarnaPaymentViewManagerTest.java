package com.klarna.inapp.sdk;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.ReactApplicationContext;
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;

import java.util.Map;

import static com.klarna.inapp.sdk.KlarnaPaymentViewManager.COMMAND_AUTHORIZE;
import static com.klarna.inapp.sdk.KlarnaPaymentViewManager.COMMAND_FINALIZE;
import static com.klarna.inapp.sdk.KlarnaPaymentViewManager.COMMAND_INITIALIZE;
import static com.klarna.inapp.sdk.KlarnaPaymentViewManager.COMMAND_LOAD;
import static com.klarna.inapp.sdk.KlarnaPaymentViewManager.COMMAND_LOAD_PAYMENT_REVIEW;
import static com.klarna.inapp.sdk.KlarnaPaymentViewManager.COMMAND_REAUTHORIZE;

@PrepareForTest()
@RunWith(RobolectricTestRunner.class)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "androidx.*", "android.*", "com.android.*"})
public class KlarnaPaymentViewManagerTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private Application application;
    private ReactApplicationContext reactContext;

    private KlarnaPaymentViewManager manager;
    private PaymentViewWrapper wrapper;

    @Before
    public void setup() {
        application = ApplicationProvider.getApplicationContext();
        reactContext = new ReactApplicationContext(application);

        manager = new KlarnaPaymentViewManager(reactContext, application);

        wrapper = Mockito.mock(PaymentViewWrapper.class);
        wrapper.paymentView = Mockito.mock(KlarnaPaymentView.class);
    }

    @After
    public void teardown() {
        Mockito.reset(wrapper, wrapper.paymentView);
    }

    @Test
    public void testReactClassName() {
        Assert.assertEquals("KlarnaPaymentView", manager.getName());
        Assert.assertEquals("KlarnaPaymentView", KlarnaPaymentViewManager.REACT_CLASS);
    }

    @Test
    public void testCommandsMap() {
        Map<String, Integer> commandsMap = manager.getCommandsMap();

        Assert.assertNotNull(commandsMap);
        Assert.assertEquals(6, commandsMap.size());
        Assert.assertEquals(Integer.valueOf(COMMAND_INITIALIZE), commandsMap.get("initialize"));
        Assert.assertEquals(Integer.valueOf(COMMAND_LOAD), commandsMap.get("load"));
        Assert.assertEquals(Integer.valueOf(COMMAND_LOAD_PAYMENT_REVIEW), commandsMap.get("loadPaymentReview"));
        Assert.assertEquals(Integer.valueOf(COMMAND_AUTHORIZE), commandsMap.get("authorize"));
        Assert.assertEquals(Integer.valueOf(COMMAND_REAUTHORIZE), commandsMap.get("reauthorize"));
        Assert.assertEquals(Integer.valueOf(COMMAND_FINALIZE), commandsMap.get("finalize"));
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

    @Test(expected = IndexOutOfBoundsException.class)
    public void testReceiveCommandInitializeThrows() {
        final JavaOnlyArray args = new JavaOnlyArray();
        args.pushString("testToken");
        manager.receiveCommand(wrapper, COMMAND_INITIALIZE, args);
    }

    @Test
    public void testReceiveCommandLoad() {
        manager.receiveCommand(wrapper, COMMAND_LOAD, null);
        Mockito.verify(wrapper.paymentView, Mockito.times(1)).load(null);

        final JavaOnlyArray args = new JavaOnlyArray();
        args.pushString("testSessionData");
        manager.receiveCommand(wrapper, COMMAND_LOAD, args);
        Mockito.verify(wrapper.paymentView, Mockito.times(1)).load("testSessionData");
    }

    @Test
    public void testReceiveCommandLoadPaymentReview() {
        manager.receiveCommand(wrapper, COMMAND_LOAD_PAYMENT_REVIEW, null);
        Mockito.verify(wrapper.paymentView, Mockito.times(1)).loadPaymentReview();
    }

    @Test
    public void testReceiveCommandAuthorize() {
        manager.receiveCommand(wrapper, COMMAND_AUTHORIZE, null);
        Mockito.verify(wrapper.paymentView, Mockito.times(0)).authorize(Mockito.anyBoolean(), (String) Mockito.isNull());

        final JavaOnlyArray args = new JavaOnlyArray();
        args.pushBoolean(true);
        args.pushString("testSessionData");
        manager.receiveCommand(wrapper, COMMAND_AUTHORIZE, args);
        Mockito.verify(wrapper.paymentView, Mockito.times(1)).authorize(true, "testSessionData");
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testReceiveCommandAuthorizeThrows() {
        final JavaOnlyArray args = new JavaOnlyArray();
        args.pushBoolean(true);
        manager.receiveCommand(wrapper, COMMAND_AUTHORIZE, args);
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
    public void testSetCategory() {
        manager.setCategory(wrapper, "testCategory");
        Mockito.verify(wrapper.paymentView).setCategory("testCategory");
    }
}
