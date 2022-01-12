package com.acme.mytrader.price;

import com.acme.mytrader.execution.ExecutionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SellPriceListenerImplTest {

    private static final String TRADE_SECURITY_ICICI = "ICICI";
    private static final String TRADE_SECURITY_IBM = "IBM";

    private SellPriceListenerImpl sellPriceListener;
    private ExecutionService executionService;

    @BeforeEach
    void setUp() {
        executionService = mock(ExecutionService.class);
    }

    @Test
    @DisplayName(" When Threshold not met For SellPriceListener")
    void testShouldNotsell_whenThresholdIsNotMet() {
        final ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Double> doubleArgumentCaptor = ArgumentCaptor.forClass(Double.class);
        final ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        sellPriceListener = new SellPriceListenerImpl(TRADE_SECURITY_IBM, 50.00, 100, executionService,
                false);
        sellPriceListener.priceUpdate(TRADE_SECURITY_IBM, 55.00);

        verify(executionService, times(0))
                .sell(stringArgumentCaptor.capture(), doubleArgumentCaptor.capture(), integerArgumentCaptor.capture());
        assertThat(sellPriceListener.isSellTrade())
                .as("Should be the trade is not successfully executed").isFalse();
    }

    @Test
    @DisplayName(" When trade secuirty is different For SellPriceListener")
    void testShouldNotsell_whenSecurityIsDifferent() {
        final ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Double> doubleArgumentCaptor = ArgumentCaptor.forClass(Double.class);
        final ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        sellPriceListener = new SellPriceListenerImpl(TRADE_SECURITY_ICICI, 70.00, 30, executionService,
                false);
        sellPriceListener.priceUpdate(TRADE_SECURITY_IBM, 55.00);

        verify(executionService, times(0))
                .sell(stringArgumentCaptor.capture(), doubleArgumentCaptor.capture(), integerArgumentCaptor.capture());
        assertThat(sellPriceListener.isSellTrade())
                .as("Should be the trade is not successfully executed").isFalse();
    }

    @Test
    @DisplayName("Given Several PriceUpdates when TradeIs AlreadyExecucted should sell OnlyOnce")
    void testGivenSeveralPriceUpdates_whenTradeIsAlreadyExecucted_shouldSellOnlyOnce() {
        final ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Double> doubleArgumentCaptor = ArgumentCaptor.forClass(Double.class);
        final ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        sellPriceListener = new SellPriceListenerImpl(TRADE_SECURITY_IBM, 70.00, 30, executionService,
                false);
        sellPriceListener.priceUpdate(TRADE_SECURITY_IBM, 25.00);
        sellPriceListener.priceUpdate(TRADE_SECURITY_IBM, 10.00);
        sellPriceListener.priceUpdate(TRADE_SECURITY_IBM, 35.00);

        verify(executionService, times(1))
                .sell(stringArgumentCaptor.capture(), doubleArgumentCaptor.capture(), integerArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).as("Should be IBM ")
                .isEqualTo(TRADE_SECURITY_IBM);
        assertThat(doubleArgumentCaptor.getValue()).as("Should be the value less than 70.00, that is 25.00")
                .isEqualTo(25.00);
        assertThat(integerArgumentCaptor.getValue()).as("Should be the volume purchased").isEqualTo(30);
        assertThat(sellPriceListener.isSellTrade())
                .as("Should be the trade is successfully executed").isTrue();
    }

    @Test
    @DisplayName(" Initialize State For SellPriceListener")
    void testInitializeStateForSellPriceListener() {
        sellPriceListener = new SellPriceListenerImpl(TRADE_SECURITY_IBM, 70.00, 30, executionService,
                false);

        assertThat(sellPriceListener.getTradeSecurity()).isEqualTo(TRADE_SECURITY_IBM);
        assertThat(sellPriceListener.getSellTrigger()).isEqualTo(70.00);
        assertThat(sellPriceListener.getSellQuantity()).isEqualTo(30);
        assertThat(sellPriceListener.isSellTrade()).isFalse();
    }

    @Test
    @DisplayName(" When Threshold met For sellPriceListener")
    void testsell_whenThresholdIsMet() {
        final ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Double> doubleArgumentCaptor = ArgumentCaptor.forClass(Double.class);
        final ArgumentCaptor<Integer> integerArgumentCaptor = ArgumentCaptor.forClass(Integer.class);

        sellPriceListener = new SellPriceListenerImpl(TRADE_SECURITY_IBM, 50.00, 30, executionService,
                false);
        sellPriceListener.priceUpdate(TRADE_SECURITY_IBM, 25.00);

        verify(executionService, times(1))
                .sell(stringArgumentCaptor.capture(), doubleArgumentCaptor.capture(), integerArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).as("Should be IBM ")
                .isEqualTo(TRADE_SECURITY_IBM);
        assertThat(doubleArgumentCaptor.getValue()).as("Should be the value less than 50.00, that is 25.00")
                .isEqualTo(25.00);
        assertThat(integerArgumentCaptor.getValue()).as("Should be the volume purchased").isEqualTo(30);
        assertThat(sellPriceListener.isSellTrade())
                .as("Should be the trade is successfully executed").isTrue();
    }

}