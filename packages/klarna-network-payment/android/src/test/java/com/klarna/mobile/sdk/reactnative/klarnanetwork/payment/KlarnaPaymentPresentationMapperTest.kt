package com.klarna.mobile.sdk.reactnative.klarnanetwork.payment

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationContent
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationIcon
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationImageAlignment
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationInstruction
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationPaymentButton
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationPaymentOption
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationText
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationTextPart
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationTextPartLinkContext
import com.klarna.mobile.sdk.klarna.network.payment.api.presentation.models.KlarnaPaymentPresentationTextPartStyle
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class KlarnaPaymentPresentationMapperTest {

    private lateinit var writableMap: WritableMap
    private lateinit var writableArray: WritableArray

    @Before
    fun setUp() {
        mockkStatic(Arguments::class)
        writableMap = mockk(relaxed = true)
        writableArray = mockk(relaxed = true)
        every { Arguments.createMap() } returns writableMap
        every { Arguments.createArray() } returns writableArray
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // region Helpers

    /**
     * Stubs Arguments.createMap() to return [maps] in order: first call returns maps[0],
     * second returns maps[1], etc. After all maps are consumed the last one is reused.
     * Use this when the SUT calls createMap() multiple times in a single toWritableMap() call.
     */
    private fun givenCreateMapReturnsInOrder(vararg maps: WritableMap) {
        every { Arguments.createMap() } returnsMany maps.toList()
    }

    // endregion

    // region KlarnaPaymentPresentationText.toWritableMap tests

    @Test
    fun `KlarnaPaymentPresentationText PlainText toWritableMap - sets type to plainText and text`() {
        // Given
        val plainText = mockk<KlarnaPaymentPresentationText.PlainText> {
            every { text } returns "Hello World"
        }

        // When
        plainText.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putString("type", "plainText") }
        verify(exactly = 1) { writableMap.putString("text", "Hello World") }
    }

    @Test
    fun `KlarnaPaymentPresentationText AttributedText toWritableMap - sets type to attributedText and parts array`() {
        // Given
        val outerMap = mockk<WritableMap>(relaxed = true)
        val partMap = mockk<WritableMap>(relaxed = true)
        givenCreateMapReturnsInOrder(outerMap, partMap)

        val part = mockk<KlarnaPaymentPresentationTextPart.PlainText> {
            every { text } returns "part text"
            every { styles } returns null
        }
        val attributedText = mockk<KlarnaPaymentPresentationText.AttributedText> {
            every { parts } returns listOf(part)
        }

        // When
        attributedText.toWritableMap()

        // Then
        verify(exactly = 1) { outerMap.putString("type", "attributedText") }
        verify(exactly = 1) { writableArray.pushMap(partMap) }
        verify(exactly = 1) { outerMap.putArray("parts", writableArray) }
    }

    @Test
    fun `KlarnaPaymentPresentationText AttributedText toWritableMap - puts null parts when absent`() {
        // Given
        val attributedText = mockk<KlarnaPaymentPresentationText.AttributedText> {
            every { parts } returns null
        }

        // When
        attributedText.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putString("type", "attributedText") }
        verify(exactly = 1) { writableMap.putNull("parts") }
        verify(exactly = 0) { writableMap.putArray(eq("parts"), any()) }
    }

    // endregion

    // region KlarnaPaymentPresentationTextPart.toWritableMap tests

    @Test
    fun `KlarnaPaymentPresentationTextPart PlainText toWritableMap - sets type to plain and text`() {
        // Given
        val part = mockk<KlarnaPaymentPresentationTextPart.PlainText> {
            every { text } returns "plain content"
            every { styles } returns null
        }

        // When
        part.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putString("type", "plain") }
        verify(exactly = 1) { writableMap.putString("text", "plain content") }
        verify(exactly = 1) { writableMap.putNull("styles") }
    }

    @Test
    fun `KlarnaPaymentPresentationTextPart Link toWritableMap - sets type to link with url and context`() {
        // Given
        val part = mockk<KlarnaPaymentPresentationTextPart.Link> {
            every { text } returns "Click here"
            every { url } returns "https://klarna.com"
            every { context } returns KlarnaPaymentPresentationTextPartLinkContext.INFO
            every { styles } returns null
        }

        // When
        part.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putString("type", "link") }
        verify(exactly = 1) { writableMap.putString("text", "Click here") }
        verify(exactly = 1) { writableMap.putString("url", "https://klarna.com") }
        verify(exactly = 1) {
            writableMap.putString("context", KlarnaPaymentPresentationTextPartLinkContext.INFO.toString())
        }
    }

    @Test
    fun `KlarnaPaymentPresentationTextPart PlainText toWritableMap - puts styles array when present`() {
        // Given
        val boldStyle = KlarnaPaymentPresentationTextPartStyle.BOLD
        val part = mockk<KlarnaPaymentPresentationTextPart.PlainText> {
            every { text } returns "bold text"
            every { styles } returns listOf(boldStyle)
        }

        // When
        part.toWritableMap()

        // Then
        verify(exactly = 1) { writableArray.pushString(boldStyle.toString()) }
        verify(exactly = 1) { writableMap.putArray("styles", writableArray) }
    }

    // endregion

    // region KlarnaPaymentPresentationContent.toWritableMap tests

    @Test
    fun `KlarnaPaymentPresentationContent toWritableMap - puts paymentOption as map when present`() {
        // Given
        val contentMap = mockk<WritableMap>(relaxed = true)
        val optionMap = mockk<WritableMap>(relaxed = true)
        givenCreateMapReturnsInOrder(contentMap, optionMap)

        val paymentOption = mockk<KlarnaPaymentPresentationPaymentOption>(relaxed = true) {
            every { paymentOptionId } returns "opt_1"
        }
        val content = mockk<KlarnaPaymentPresentationContent> {
            every { instruction } returns KlarnaPaymentPresentationInstruction.SHOW_KLARNA
            every { paymentStatus } returns null
            every { this@mockk.paymentOption } returns paymentOption
            every { savedPaymentOption } returns null
        }

        // When
        content.toWritableMap()

        // Then
        verify(exactly = 1) { contentMap.putMap("paymentOption", optionMap) }
        verify(exactly = 0) { contentMap.putNull("paymentOption") }
    }

    @Test
    fun `KlarnaPaymentPresentationContent toWritableMap - puts null paymentOption when absent`() {
        // Given
        val content = mockk<KlarnaPaymentPresentationContent> {
            every { instruction } returns KlarnaPaymentPresentationInstruction.PRESELECT_KLARNA
            every { paymentStatus } returns null
            every { paymentOption } returns null
            every { savedPaymentOption } returns null
        }

        // When
        content.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putNull("paymentOption") }
        verify(exactly = 0) { writableMap.putMap(eq("paymentOption"), any()) }
    }

    @Test
    fun `KlarnaPaymentPresentationContent toWritableMap - puts instruction string`() {
        // Given
        val content = mockk<KlarnaPaymentPresentationContent> {
            every { instruction } returns KlarnaPaymentPresentationInstruction.SHOW_ONLY_KLARNA
            every { paymentStatus } returns null
            every { paymentOption } returns null
            every { savedPaymentOption } returns null
        }

        // When
        content.toWritableMap()

        // Then
        verify(exactly = 1) {
            writableMap.putString("instruction", KlarnaPaymentPresentationInstruction.SHOW_ONLY_KLARNA.toString())
        }
    }

    // endregion

    // region KlarnaPaymentPresentationPaymentButton.toWritableMap tests

    @Test
    fun `KlarnaPaymentPresentationPaymentButton toWritableMap - puts all fields when present`() {
        // Given
        val button = mockk<KlarnaPaymentPresentationPaymentButton> {
            every { text } returns "Pay now"
            every { imageUrl } returns "https://example.com/icon.png"
            every { imageAlignment } returns KlarnaPaymentPresentationImageAlignment.RIGHT
        }

        // When
        button.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putString("text", "Pay now") }
        verify(exactly = 1) { writableMap.putString("imageUrl", "https://example.com/icon.png") }
        verify(exactly = 1) {
            writableMap.putString("imageAlignment", KlarnaPaymentPresentationImageAlignment.RIGHT.toString())
        }
    }

    @Test
    fun `KlarnaPaymentPresentationPaymentButton toWritableMap - puts null imageUrl when absent`() {
        // Given
        val button = mockk<KlarnaPaymentPresentationPaymentButton> {
            every { text } returns "Pay"
            every { imageUrl } returns null
            every { imageAlignment } returns null
        }

        // When
        button.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putString("imageUrl", null) }
        verify(exactly = 1) { writableMap.putString("imageAlignment", null) }
    }

    // endregion

    // region KlarnaPaymentPresentationIcon.toWritableMap tests

    @Test
    fun `KlarnaPaymentPresentationIcon toWritableMap - puts all fields when present`() {
        // Given
        val icon = mockk<KlarnaPaymentPresentationIcon> {
            every { alt } returns "Klarna logo"
            every { badgeImageUrl } returns "https://example.com/badge.png"
            every { rectangleImageUrl } returns "https://example.com/rect.png"
            every { squareImageUrl } returns "https://example.com/square.png"
        }

        // When
        icon.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putString("alt", "Klarna logo") }
        verify(exactly = 1) { writableMap.putString("badgeImageUrl", "https://example.com/badge.png") }
        verify(exactly = 1) { writableMap.putString("rectangleImageUrl", "https://example.com/rect.png") }
        verify(exactly = 1) { writableMap.putString("squareImageUrl", "https://example.com/square.png") }
    }

    @Test
    fun `KlarnaPaymentPresentationIcon toWritableMap - puts null for all absent fields`() {
        // Given
        val icon = mockk<KlarnaPaymentPresentationIcon> {
            every { alt } returns null
            every { badgeImageUrl } returns null
            every { rectangleImageUrl } returns null
            every { squareImageUrl } returns null
        }

        // When
        icon.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putString("alt", null) }
        verify(exactly = 1) { writableMap.putString("badgeImageUrl", null) }
        verify(exactly = 1) { writableMap.putString("rectangleImageUrl", null) }
        verify(exactly = 1) { writableMap.putString("squareImageUrl", null) }
    }

    // endregion

    // region KlarnaPaymentPresentationPaymentOption.toWritableMap tests

    @Test
    fun `KlarnaPaymentPresentationPaymentOption toWritableMap - puts paymentOptionId`() {
        // Given
        val option = mockk<KlarnaPaymentPresentationPaymentOption> {
            every { paymentOptionId } returns "opt_klarna"
            every { header } returns null
            every { badge } returns null
            every { subheader } returns null
            every { message } returns null
            every { terms } returns null
            every { paymentButton } returns null
            every { icon } returns null
        }

        // When
        option.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putString("paymentOptionId", "opt_klarna") }
    }

    @Test
    fun `KlarnaPaymentPresentationPaymentOption toWritableMap - puts null for all absent optional fields`() {
        // Given
        val option = mockk<KlarnaPaymentPresentationPaymentOption> {
            every { paymentOptionId } returns "opt_klarna"
            every { header } returns null
            every { badge } returns null
            every { subheader } returns null
            every { message } returns null
            every { terms } returns null
            every { paymentButton } returns null
            every { icon } returns null
        }

        // When
        option.toWritableMap()

        // Then
        verify(exactly = 1) { writableMap.putNull("header") }
        verify(exactly = 1) { writableMap.putNull("badge") }
        verify(exactly = 1) { writableMap.putNull("subheader") }
        verify(exactly = 1) { writableMap.putNull("message") }
        verify(exactly = 1) { writableMap.putNull("terms") }
        verify(exactly = 1) { writableMap.putNull("paymentButton") }
        verify(exactly = 1) { writableMap.putNull("icon") }
    }

    @Test
    fun `KlarnaPaymentPresentationPaymentOption toWritableMap - puts header as map when present`() {
        // Given
        val optionMap = mockk<WritableMap>(relaxed = true)
        val headerMap = mockk<WritableMap>(relaxed = true)
        givenCreateMapReturnsInOrder(optionMap, headerMap)

        val header = mockk<KlarnaPaymentPresentationText.PlainText> {
            every { text } returns "Pay with Klarna"
        }
        val option = mockk<KlarnaPaymentPresentationPaymentOption> {
            every { paymentOptionId } returns "opt_klarna"
            every { this@mockk.header } returns header
            every { badge } returns null
            every { subheader } returns null
            every { message } returns null
            every { terms } returns null
            every { paymentButton } returns null
            every { icon } returns null
        }

        // When
        option.toWritableMap()

        // Then
        verify(exactly = 1) { optionMap.putMap("header", headerMap) }
        verify(exactly = 0) { optionMap.putNull("header") }
    }

    @Test
    fun `KlarnaPaymentPresentationPaymentOption toWritableMap - puts paymentButton and icon as maps when present`() {
        // Given
        val optionMap = mockk<WritableMap>(relaxed = true)
        val buttonMap = mockk<WritableMap>(relaxed = true)
        val iconMap = mockk<WritableMap>(relaxed = true)
        givenCreateMapReturnsInOrder(optionMap, buttonMap, iconMap)

        val button = mockk<KlarnaPaymentPresentationPaymentButton> {
            every { text } returns "Pay now"
            every { imageUrl } returns null
            every { imageAlignment } returns null
        }
        val icon = mockk<KlarnaPaymentPresentationIcon> {
            every { alt } returns null
            every { badgeImageUrl } returns null
            every { rectangleImageUrl } returns null
            every { squareImageUrl } returns null
        }
        val option = mockk<KlarnaPaymentPresentationPaymentOption> {
            every { paymentOptionId } returns "opt_klarna"
            every { header } returns null
            every { badge } returns null
            every { subheader } returns null
            every { message } returns null
            every { terms } returns null
            every { paymentButton } returns button
            every { this@mockk.icon } returns icon
        }

        // When
        option.toWritableMap()

        // Then
        verify(exactly = 1) { optionMap.putMap("paymentButton", buttonMap) }
        verify(exactly = 0) { optionMap.putNull("paymentButton") }
        verify(exactly = 1) { optionMap.putMap("icon", iconMap) }
        verify(exactly = 0) { optionMap.putNull("icon") }
    }

    // endregion
}
