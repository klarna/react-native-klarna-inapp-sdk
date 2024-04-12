package com.klarna.mobile.sdk.reactnative.common.event;

import android.view.View;

import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaProduct;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.reactnative.common.ArgumentsUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ArgumentsUtil.class})
public class KlarnaEventHandlerEventsUtilTest {

    private ComponentEventSender componentEventSender;
    private View view;

    @Before
    public void setUp() {
        componentEventSender = Mockito.mock(ComponentEventSender.class);
        view = Mockito.mock(View.class);

        PowerMockito.mockStatic(ArgumentsUtil.class);
        Mockito.when(ArgumentsUtil.createMap(Mockito.anyMap())).thenAnswer(invocation -> null);
    }

    @Test
    public void sendKlarnaProductEvent() {
        // Given
        KlarnaProductEvent klarnaProductEvent = new KlarnaProductEvent("action", new HashSet<>(List.of(KlarnaProduct.KLARNA_CHECKOUT)), new HashMap<>(), "testSessionId");

        // When
        KlarnaEventHandlerEventsUtil.sendKlarnaProductEvent(componentEventSender, view, klarnaProductEvent);

        // Then
        Mockito.verify(componentEventSender).postEventForView(view, KlarnaEventHandlerEventsUtil.EVENT_NAME_ON_EVENT, null);
    }

    @Test
    public void sendKlarnaMobileSDKError() {
        // Given
        KlarnaMobileSDKError klarnaMobileSDKError = new com.klarna.mobile.sdk.api.hybrid.KlarnaMobileSDKError("name", "message", true, "sessionId");

        // When
        KlarnaEventHandlerEventsUtil.sendKlarnaMobileSDKError(componentEventSender, view, klarnaMobileSDKError);

        // Then
        Mockito.verify(componentEventSender).postEventForView(view, KlarnaEventHandlerEventsUtil.EVENT_NAME_ON_EVENT, null);
    }
}
