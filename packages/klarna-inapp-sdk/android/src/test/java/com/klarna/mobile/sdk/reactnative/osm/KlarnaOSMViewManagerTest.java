package com.klarna.mobile.sdk.reactnative.osm;

import static com.klarna.mobile.sdk.reactnative.osm.KlarnaOSMViewManager.COMMAND_RENDER_OSM;
import static com.klarna.mobile.sdk.reactnative.osm.KlarnaOSMViewManager.KLARNA_OSM_VIEW_REACT_CLASS;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyArray;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableArray;
import com.klarna.mobile.sdk.api.KlarnaEnvironment;
import com.klarna.mobile.sdk.api.KlarnaRegion;
import com.klarna.mobile.sdk.api.KlarnaTheme;
import com.klarna.mobile.sdk.api.osm.KlarnaOSMView;
import com.klarna.mobile.sdk.reactnative.common.ui.WrapperView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class KlarnaOSMViewManagerTest {

    private MockedStatic<Arguments> mockedStaticArguments;
    private KlarnaOSMViewManager manager;
    private WrapperView<KlarnaOSMView> wrapper;
    private KlarnaOSMView osmView;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        manager = new KlarnaOSMViewManager();

        osmView = Mockito.mock(KlarnaOSMView.class);
        wrapper = Mockito.mock(WrapperView.class);
        Mockito.when(wrapper.getView()).thenReturn(osmView);

        mockedStaticArguments = Mockito.mockStatic(Arguments.class);
        Mockito.when(Arguments.createArray())
                .thenAnswer(invocation -> new JavaOnlyArray());
        Mockito.when(Arguments.createMap())
                .thenAnswer(invocation -> new JavaOnlyMap());
    }

    @After
    public void teardown() {
        Mockito.reset(wrapper, osmView);
        mockedStaticArguments.close();
    }

    // --- getName ---

    @Test
    public void testReactClassName() {
        Assert.assertEquals("RNKlarnaOSMView", manager.getName());
        Assert.assertEquals("RNKlarnaOSMView", KLARNA_OSM_VIEW_REACT_CLASS);
    }

    // --- Prop setters: string props ---

    @Test
    public void testSetClientId() {
        manager.setClientId(wrapper, "test-client-id");
        Mockito.verify(osmView).setClientId("test-client-id");
    }

    @Test
    public void testSetClientIdNull() {
        manager.setClientId(wrapper, null);
        Mockito.verify(osmView, Mockito.never()).setClientId(Mockito.anyString());
    }

    @Test
    public void testSetClientIdEmpty() {
        manager.setClientId(wrapper, "");
        Mockito.verify(osmView, Mockito.never()).setClientId(Mockito.anyString());
    }

    @Test
    public void testSetPlacementKey() {
        manager.setPlacementKey(wrapper, "test-placement-key");
        Mockito.verify(osmView).setPlacementKey("test-placement-key");
    }

    @Test
    public void testSetPlacementKeyNull() {
        manager.setPlacementKey(wrapper, null);
        Mockito.verify(osmView, Mockito.never()).setPlacementKey(Mockito.anyString());
    }

    @Test
    public void testSetPlacementKeyEmpty() {
        manager.setPlacementKey(wrapper, "");
        Mockito.verify(osmView, Mockito.never()).setPlacementKey(Mockito.anyString());
    }

    @Test
    public void testSetLocale() {
        manager.setLocale(wrapper, "en-US");
        Mockito.verify(osmView).setLocale("en-US");
    }

    @Test
    public void testSetLocaleNull() {
        manager.setLocale(wrapper, null);
        Mockito.verify(osmView, Mockito.never()).setLocale(Mockito.anyString());
    }

    @Test
    public void testSetLocaleEmpty() {
        manager.setLocale(wrapper, "");
        Mockito.verify(osmView, Mockito.never()).setLocale(Mockito.anyString());
    }

    // --- Prop setter: purchaseAmount ---

    @Test
    public void testSetPurchaseAmountValid() {
        manager.setPurchaseAmount(wrapper, "9999");
        Mockito.verify(osmView).setPurchaseAmount(9999L);
    }

    @Test
    public void testSetPurchaseAmountNull() {
        manager.setPurchaseAmount(wrapper, null);
        Mockito.verify(osmView, Mockito.never()).setPurchaseAmount(Mockito.anyLong());
    }

    @Test
    public void testSetPurchaseAmountEmpty() {
        manager.setPurchaseAmount(wrapper, "");
        Mockito.verify(osmView, Mockito.never()).setPurchaseAmount(Mockito.anyLong());
    }

    @Test
    public void testSetPurchaseAmountInvalid() {
        // Should not throw, but should not set the value either
        manager.setPurchaseAmount(wrapper, "not-a-number");
        Mockito.verify(osmView, Mockito.never()).setPurchaseAmount(Mockito.anyLong());
    }

    @Test
    public void testSetPurchaseAmountLargeNumber() {
        manager.setPurchaseAmount(wrapper, "999999999999");
        Mockito.verify(osmView).setPurchaseAmount(999999999999L);
    }

    // --- Prop setter: environment ---

    @Test
    public void testSetEnvironmentPlayground() {
        manager.setEnvironment(wrapper, "playground");
        Mockito.verify(osmView).setEnvironment(KlarnaEnvironment.PLAYGROUND);
    }

    @Test
    public void testSetEnvironmentProduction() {
        manager.setEnvironment(wrapper, "production");
        Mockito.verify(osmView).setEnvironment(KlarnaEnvironment.PRODUCTION);
    }

    @Test
    public void testSetEnvironmentStaging() {
        manager.setEnvironment(wrapper, "staging");
        Mockito.verify(osmView).setEnvironment(KlarnaEnvironment.STAGING);
    }

    @Test
    public void testSetEnvironmentUnknown() {
        manager.setEnvironment(wrapper, "unknown");
        Mockito.verify(osmView, Mockito.never()).setEnvironment(Mockito.any());
    }

    @Test
    public void testSetEnvironmentNull() {
        manager.setEnvironment(wrapper, null);
        Mockito.verify(osmView, Mockito.never()).setEnvironment(Mockito.any());
    }

    @Test
    public void testSetEnvironmentEmpty() {
        manager.setEnvironment(wrapper, "");
        Mockito.verify(osmView, Mockito.never()).setEnvironment(Mockito.any());
    }

    // --- Prop setter: region ---

    @Test
    public void testSetRegionEu() {
        manager.setRegion(wrapper, "eu");
        Mockito.verify(osmView).setRegion(KlarnaRegion.EU);
    }

    @Test
    public void testSetRegionNa() {
        manager.setRegion(wrapper, "na");
        Mockito.verify(osmView).setRegion(KlarnaRegion.NA);
    }

    @Test
    public void testSetRegionOc() {
        manager.setRegion(wrapper, "oc");
        Mockito.verify(osmView).setRegion(KlarnaRegion.OC);
    }

    @Test
    public void testSetRegionUnknown() {
        manager.setRegion(wrapper, "unknown");
        Mockito.verify(osmView, Mockito.never()).setRegion(Mockito.any());
    }

    @Test
    public void testSetRegionNull() {
        manager.setRegion(wrapper, null);
        Mockito.verify(osmView, Mockito.never()).setRegion(Mockito.any());
    }

    // --- Prop setter: theme ---

    @Test
    public void testSetThemeLight() {
        manager.setTheme(wrapper, "light");
        Mockito.verify(osmView).setTheme(KlarnaTheme.LIGHT);
    }

    @Test
    public void testSetThemeDark() {
        manager.setTheme(wrapper, "dark");
        Mockito.verify(osmView).setTheme(KlarnaTheme.DARK);
    }

    @Test
    public void testSetThemeAutomatic() {
        manager.setTheme(wrapper, "automatic");
        Mockito.verify(osmView).setTheme(KlarnaTheme.AUTOMATIC);
    }

    @Test
    public void testSetThemeUnknown() {
        manager.setTheme(wrapper, "unknown");
        Mockito.verify(osmView, Mockito.never()).setTheme(Mockito.any());
    }

    @Test
    public void testSetThemeNull() {
        manager.setTheme(wrapper, null);
        Mockito.verify(osmView, Mockito.never()).setTheme(Mockito.any());
    }

    // --- Prop setter: returnUrl (no-op) ---

    @Test
    public void testSetReturnUrlIsNoOp() {
        // returnUrl is intentionally ignored for OSM
        manager.setReturnUrl(wrapper, "https://return.example.com");
        // Verify no interaction with osmView for returnUrl
        Mockito.verifyNoInteractions(osmView);
    }

    // --- Prop setter: backgroundColor / textColor ---

    @Test
    public void testSetBackgroundColor() {
        manager.setBackgroundColor(wrapper, "#FF0000");
        Mockito.verify(osmView).setStyleConfiguration(Mockito.any());
    }

    @Test
    public void testSetTextColor() {
        manager.setTextColor(wrapper, "#00FF00");
        Mockito.verify(osmView).setStyleConfiguration(Mockito.any());
    }

    @Test
    public void testSetBackgroundColorNull() {
        manager.setBackgroundColor(wrapper, null);
        // null color means null style configuration
        Mockito.verify(osmView).setStyleConfiguration(null);
    }

    @Test
    public void testSetTextColorNull() {
        manager.setTextColor(wrapper, null);
        Mockito.verify(osmView).setStyleConfiguration(null);
    }

    @Test
    public void testSetBackgroundColorInvalid() {
        manager.setBackgroundColor(wrapper, "not-a-color");
        // Invalid color should result in null style configuration
        Mockito.verify(osmView).setStyleConfiguration(null);
    }

    @Test
    public void testSetColorWithoutHash() {
        // parseColor should auto-prefix "#"
        manager.setBackgroundColor(wrapper, "FF0000");
        Mockito.verify(osmView).setStyleConfiguration(Mockito.any());
    }

    // --- receiveCommand ---

    @Test
    public void testReceiveCommandRender() {
        // receiveCommand with "render" requires context for render(),
        // but we can verify the command routing by mocking the context
        ReadableArray args = new JavaOnlyArray();
        // render() calls view.getView() and then needs ReactContext, so this will
        // exercise the command dispatch path; the actual render is tested separately
        try {
            manager.receiveCommand(wrapper, COMMAND_RENDER_OSM, args);
        } catch (Exception e) {
            // Expected: render() tries to cast wrapper.getContext() to ReactContext
        }
    }

    @Test
    public void testReceiveCommandUnknown() {
        // Unknown command should be silently ignored
        ReadableArray args = new JavaOnlyArray();
        manager.receiveCommand(wrapper, "unknownCommand", args);
        // No exception should be thrown
    }

    // --- getExportedCustomDirectEventTypeConstants ---

    @Test
    public void testExportedEvents() {
        java.util.Map<String, Object> events = manager.getExportedCustomDirectEventTypeConstants();
        Assert.assertNotNull(events);
        Assert.assertEquals(3, events.size());
        Assert.assertTrue(events.containsKey("onError"));
        Assert.assertTrue(events.containsKey("onResized"));
        Assert.assertTrue(events.containsKey("onOSMViewReady"));
    }

    // --- Null view edge case ---

    @Test
    public void testSetClientIdNullInnerView() {
        Mockito.when(wrapper.getView()).thenReturn(null);
        // Should not throw
        manager.setClientId(wrapper, "test-client-id");
    }

    @Test
    public void testSetPurchaseAmountNullInnerView() {
        Mockito.when(wrapper.getView()).thenReturn(null);
        manager.setPurchaseAmount(wrapper, "9999");
    }

    @Test
    public void testSetEnvironmentNullInnerView() {
        Mockito.when(wrapper.getView()).thenReturn(null);
        manager.setEnvironment(wrapper, "production");
    }

    @Test
    public void testSetRegionNullInnerView() {
        Mockito.when(wrapper.getView()).thenReturn(null);
        manager.setRegion(wrapper, "eu");
    }

    @Test
    public void testSetThemeNullInnerView() {
        Mockito.when(wrapper.getView()).thenReturn(null);
        manager.setTheme(wrapper, "light");
    }

    @Test
    public void testSetBackgroundColorNullInnerView() {
        Mockito.when(wrapper.getView()).thenReturn(null);
        manager.setBackgroundColor(wrapper, "#FF0000");
    }

    @Test
    public void testSetTextColorNullInnerView() {
        Mockito.when(wrapper.getView()).thenReturn(null);
        manager.setTextColor(wrapper, "#00FF00");
    }
}
