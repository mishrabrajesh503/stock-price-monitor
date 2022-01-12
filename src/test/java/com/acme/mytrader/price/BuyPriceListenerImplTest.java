package com.acme.mytrader.price;

import com.acme.mytrader.execution.ExecutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BuyPriceListenerImplTest {

    private static final String TRADE_SECURITY_ICICI = "ICICI";
    private static final String TRADE_SECURITY_IBM = "IBM";

    private BuyPriceListenerImpl buyPriceListener;
    private ExecutionService executionService;

    @BeforeEach
    void setUp() {
        executionService = mock(ExecutionService.class);
    }

    @Test
    @DisplayName(" When Threshold not met For BuyPriceListener")
    void testShouldNotBuy_whenThresholdIsNotMet() {
        final ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Double> doubleArgumentCaptor = ArgumentCaptor.forClass(Double.class);
        final ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        buyPriceListener = new BuyPriceListenerImpl(TRADE_SECURITY_IBM, 50.00, 100, executionService,
                false);
        buyPriceListener.priceUpdate(TRADE_SECURITY_IBM, 55.00);

        verify(executionService, times(0))
                .buy(stringArgumentCaptor.capture(), doubleArgumentCaptor.capture(), integerArgumentCaptor.capture());
        assertThat(buyPriceListener.isBuyTrade())
                .as("Should be the trade is not successfully executed").isFalse();
    }

    @Test
    @DisplayName("Given Several PriceUpdates when TradeIs AlreadyExecucted should buy OnlyOnce")
    void testGivenSeveralPriceUpdates_whenTradeIsAlreadyExecucted_shouldBuyOnlyOnce() {
        final ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Double> doubleArgumentCaptor = ArgumentCaptor.forClass(Double.class);
        final ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        buyPriceListener = new BuyPriceListenerImpl(TRADE_SECURITY_IBM, 50.00, 100, executionService,
                false);
        buyPriceListener.priceUpdate(TRADE_SECURITY_IBM, 25.00);
        buyPriceListener.priceUpdate(TRADE_SECURITY_IBM, 10.00);
        buyPriceListener.priceUpdate(TRADE_SECURITY_IBM, 35.00);

        verify(executionService, times(1))
                .buy(stringArgumentCaptor.capture(), doubleArgumentCaptor.capture(), integerArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).as("Should be IBM ")
                .isEqualTo(TRADE_SECURITY_IBM);
        assertThat(doubleArgumentCaptor.getValue()).as("Should be the value less than 50.00, that is 25.00")
                .isEqualTo(25.00);
        assertThat(integerArgumentCaptor.getValue()).as("Should be the volume purchased").isEqualTo(100);
        assertThat(buyPriceListener.isBuyTrade())
                .as("Should be the trade is successfully executed").isTrue();
    }

    @Test
    @DisplayName(" When trade secuirty is different For BuyPriceListener")
    void testShouldNotBuy_whenSecurityIsDifferent() {
        final ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Double> doubleArgumentCaptor = ArgumentCaptor.forClass(Double.class);
        final ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        buyPriceListener = new BuyPriceListenerImpl(TRADE_SECURITY_ICICI, 50.00, 100, executionService,
                false);
        buyPriceListener.priceUpdate(TRADE_SECURITY_IBM, 55.00);

        verify(executionService, times(0))
                .buy(stringArgumentCaptor.capture(), doubleArgumentCaptor.capture(), integerArgumentCaptor.capture());
        assertThat(buyPriceListener.isBuyTrade())
                .as("Should be the trade is not successfully executed").isFalse();
    }

    @Test
    @DisplayName(" When Threshold met For BuyPriceListener")
    void testBuy_whenThresholdIsMet() {
        final ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Double> doubleArgumentCaptor = ArgumentCaptor.forClass(Double.class);
        final ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        buyPriceListener = new BuyPriceListenerImpl(TRADE_SECURITY_IBM, 50.00, 100, executionService,
                false);
        buyPriceListener.priceUpdate(TRADE_SECURITY_IBM, 25.00);

        verify(executionService, times(1))
                .buy(stringArgumentCaptor.capture(), doubleArgumentCaptor.capture(), integerArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).as("Should be IBM ")
                .isEqualTo(TRADE_SECURITY_IBM);
        assertThat(doubleArgumentCaptor.getValue()).as("Should be the value less than 50.00, that is 25.00")
                .isEqualTo(25.00);
        assertThat(integerArgumentCaptor.getValue()).as("Should be the volume purchased").isEqualTo(100);
        assertThat(buyPriceListener.isBuyTrade())
                .as("Should be the trade is successfully executed").isTrue();
    }

    @Test
    @DisplayName(" Initialize State For BuyPriceListener")
    void testInitializeStateForBuyPriceListener() {
        buyPriceListener = new BuyPriceListenerImpl(TRADE_SECURITY_IBM, 50.00, 100, executionService,
                false);

        assertThat(buyPriceListener.getTradeSecurity()).isEqualTo(TRADE_SECURITY_IBM);
        assertThat(buyPriceListener.getBuyTrigger()).isEqualTo(50.00);
        assertThat(buyPriceListener.getBuyQuantity()).isEqualTo(100);
        assertThat(buyPriceListener.isBuyTrade()).isFalse();
    }

}