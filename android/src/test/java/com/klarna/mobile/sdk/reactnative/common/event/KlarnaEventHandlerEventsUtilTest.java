package com.klarna.mobile.sdk.reactnative.common.event;

import android.view.View;

import com.klarna.mobile.sdk.KlarnaMobileSDKError;
import com.klarna.mobile.sdk.api.KlarnaProduct;
import com.klarna.mobile.sdk.api.KlarnaProductEvent;
import com.klarna.mobile.sdk.reactnative.common.util.ArgumentsUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class KlarnaEventHandlerEventsUtilTest {

    private MockedStatic<ArgumentsUtil> mockedStaticArgumentsUtil;
    private ComponentEventSender componentEventSender;
    private View view;

    @Before
    public void setUp() {
        componentEventSender = Mockito.mock(ComponentEventSender.class);
        view = Mockito.mock(View.class);

        mockedStaticArgumentsUtil = Mockito.mockStatic(ArgumentsUtil.class);
        Mockito.when(ArgumentsUtil.createMap(Mockito.anyMap())).thenAnswer(invocation -> null);
    }

    @After
    public void teardown() {
        mockedStaticArgumentsUtil.close();
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
