package com.klarna.mobile.sdk.reactnative.expresscheckout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.widget.FrameLayout;

import com.klarna.mobile.sdk.api.KlarnaEnvironment;
import com.klarna.mobile.sdk.api.KlarnaRegion;
import com.klarna.mobile.sdk.api.button.KlarnaButtonShape;
import com.klarna.mobile.sdk.api.button.KlarnaButtonStyle;
import com.klarna.mobile.sdk.api.button.KlarnaButtonTheme;
import com.klarna.mobile.sdk.api.expresscheckout.KlarnaExpressCheckoutButton;
import com.klarna.mobile.sdk.api.expresscheckout.KlarnaExpressCheckoutButtonAuthorizationResponse;
import com.klarna.mobile.sdk.api.expresscheckout.KlarnaExpressCheckoutError;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class KlarnaExpressCheckoutViewManagerTest {

    private KlarnaExpressCheckoutViewManager manager;

    @Before
    public void setup() {
        manager = new KlarnaExpressCheckoutViewManager();
    }

    // region React class name

    @Test
    public void testReactClassName() {
        assertEquals("RNKlarnaExpressCheckoutView", manager.getName());
        assertEquals("RNKlarnaExpressCheckoutView", KlarnaExpressCheckoutViewManager.REACT_CLASS);
    }

    // endregion

    // region parseTheme

    @Test
    public void testParseTheme_light() {
        assertEquals(KlarnaButtonTheme.LIGHT, KlarnaExpressCheckoutViewManager.parseTheme("light"));
    }

    @Test
    public void testParseTheme_dark() {
        assertEquals(KlarnaButtonTheme.DARK, KlarnaExpressCheckoutViewManager.parseTheme("dark"));
    }

    @Test
    public void testParseTheme_auto() {
        assertEquals(KlarnaButtonTheme.AUTO, KlarnaExpressCheckoutViewManager.parseTheme("auto"));
    }

    @Test
    public void testParseTheme_null() {
        assertNull(KlarnaExpressCheckoutViewManager.parseTheme(null));
    }

    @Test
    public void testParseTheme_unknown() {
        assertNull(KlarnaExpressCheckoutViewManager.parseTheme("neon"));
    }

    // endregion

    // region parseShape

    @Test
    public void testParseShape_roundedRect() {
        assertEquals(KlarnaButtonShape.ROUNDED_RECT, KlarnaExpressCheckoutViewManager.parseShape("roundedRect"));
    }

    @Test
    public void testParseShape_pill() {
        assertEquals(KlarnaButtonShape.PILL, KlarnaExpressCheckoutViewManager.parseShape("pill"));
    }

    @Test
    public void testParseShape_rectangle() {
        assertEquals(KlarnaButtonShape.RECTANGLE, KlarnaExpressCheckoutViewManager.parseShape("rectangle"));
    }

    @Test
    public void testParseShape_null() {
        assertNull(KlarnaExpressCheckoutViewManager.parseShape(null));
    }

    @Test
    public void testParseShape_unknown() {
        assertNull(KlarnaExpressCheckoutViewManager.parseShape("circle"));
    }

    // endregion

    // region parseButtonStyle

    @Test
    public void testParseButtonStyle_filled() {
        assertEquals(KlarnaButtonStyle.FILLED, KlarnaExpressCheckoutViewManager.parseButtonStyle("filled"));
    }

    @Test
    public void testParseButtonStyle_outlined() {
        assertEquals(KlarnaButtonStyle.OUTLINED, KlarnaExpressCheckoutViewManager.parseButtonStyle("outlined"));
    }

    @Test
    public void testParseButtonStyle_null() {
        assertNull(KlarnaExpressCheckoutViewManager.parseButtonStyle(null));
    }

    @Test
    public void testParseButtonStyle_unknown() {
        assertNull(KlarnaExpressCheckoutViewManager.parseButtonStyle("gradient"));
    }

    // endregion

    // region parseEnvironment

    @Test
    public void testParseEnvironment_playground() {
        assertEquals(KlarnaEnvironment.PLAYGROUND, KlarnaExpressCheckoutViewManager.parseEnvironment("playground"));
    }

    @Test
    public void testParseEnvironment_production() {
        assertEquals(KlarnaEnvironment.PRODUCTION, KlarnaExpressCheckoutViewManager.parseEnvironment("production"));
    }

    @Test
    public void testParseEnvironment_null() {
        assertNull(KlarnaExpressCheckoutViewManager.parseEnvironment(null));
    }

    @Test
    public void testParseEnvironment_unknown() {
        assertNull(KlarnaExpressCheckoutViewManager.parseEnvironment("staging"));
    }

    // endregion

    // region parseRegion

    @Test
    public void testParseRegion_eu() {
        assertEquals(KlarnaRegion.EU, KlarnaExpressCheckoutViewManager.parseRegion("eu"));
    }

    @Test
    public void testParseRegion_na() {
        assertEquals(KlarnaRegion.NA, KlarnaExpressCheckoutViewManager.parseRegion("na"));
    }

    @Test
    public void testParseRegion_oc() {
        assertEquals(KlarnaRegion.OC, KlarnaExpressCheckoutViewManager.parseRegion("oc"));
    }

    @Test
    public void testParseRegion_null() {
        assertNull(KlarnaExpressCheckoutViewManager.parseRegion(null));
    }

    @Test
    public void testParseRegion_unknown() {
        assertNull(KlarnaExpressCheckoutViewManager.parseRegion("ap"));
    }

    // endregion

    // region ExpressCheckoutProps defaults

    @Test
    public void testPropsDefaults() {
        KlarnaExpressCheckoutViewManager.ExpressCheckoutProps props =
                new KlarnaExpressCheckoutViewManager.ExpressCheckoutProps();

        assertNull(props.sessionType);
        assertNull(props.clientId);
        assertNull(props.clientToken);
        assertNull(props.locale);
        assertNull(props.environment);
        assertNull(props.region);
        assertNull(props.returnUrl);
        assertNull(props.theme);
        assertNull(props.shape);
        assertNull(props.buttonStyle);
        assertTrue(props.autoFinalize);
        assertFalse(props.collectShippingAddress);
        assertNull(props.sessionData);
        assertEquals(-1, props.lastSentHeightDp);
    }

    // endregion

    // region Callback forwarding

    @Test
    public void testCallbackForwardsOnAuthorized() {
        FrameLayout container = Mockito.mock(FrameLayout.class);
        KlarnaExpressCheckoutViewEventSender sender =
                Mockito.mock(KlarnaExpressCheckoutViewEventSender.class);

        KlarnaExpressCheckoutViewCallback callback =
                new KlarnaExpressCheckoutViewCallback(container, sender);

        KlarnaExpressCheckoutButton button = Mockito.mock(KlarnaExpressCheckoutButton.class);
        KlarnaExpressCheckoutButtonAuthorizationResponse response =
                Mockito.mock(KlarnaExpressCheckoutButtonAuthorizationResponse.class);

        callback.onAuthorized(button, response);

        Mockito.verify(sender, Mockito.times(1))
                .sendOnAuthorizedEvent(container, response);
    }

    @Test
    public void testCallbackForwardsOnError() {
        FrameLayout container = Mockito.mock(FrameLayout.class);
        KlarnaExpressCheckoutViewEventSender sender =
                Mockito.mock(KlarnaExpressCheckoutViewEventSender.class);

        KlarnaExpressCheckoutViewCallback callback =
                new KlarnaExpressCheckoutViewCallback(container, sender);

        KlarnaExpressCheckoutButton button = Mockito.mock(KlarnaExpressCheckoutButton.class);
        KlarnaExpressCheckoutError error = Mockito.mock(KlarnaExpressCheckoutError.class);

        callback.onError(button, error);

        Mockito.verify(sender, Mockito.times(1))
                .sendKlarnaExpressCheckoutError(container, error);
    }

    // endregion

    // region Event sender configuration

    @Test
    public void testExportedEventConstants() {
        assertNotNull(manager.getExportedCustomDirectEventTypeConstants());
        assertTrue(manager.getExportedCustomDirectEventTypeConstants().containsKey("onAuthorized"));
        assertTrue(manager.getExportedCustomDirectEventTypeConstants().containsKey("onError"));
        assertTrue(manager.getExportedCustomDirectEventTypeConstants().containsKey("onResized"));
    }

    // endregion
}
