package com.acme.mytrader.strategy;

import com.acme.mytrader.execution.ExecutionService;
import com.acme.mytrader.price.PriceListener;
import com.acme.mytrader.price.PriceSourceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.exceptions.base.MockitoException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TradingStrategyTest {

    private ExecutionService executionService;
    private TradingStrategy tradingStrategy;
    private static final String TRADE_SECURITY_ICICI = "ICICI";
    private static final String TRADE_SECURITY_IBM = "IBM";

    @BeforeEach
    void setUp() {
        PriceSourceImpl priceSource = new MockPriceSource(TRADE_SECURITY_IBM, 90.0);
        executionService = mock(ExecutionService.class);
        tradingStrategy = new TradingStrategy(executionService, priceSource);
    }

    @Test
    @DisplayName("Test case for buy trade for security")
    void buy() throws InterruptedException {
        final ArgumentCaptor<String> securityCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        final ArgumentCaptor<Integer> volumeCaptor = ArgumentCaptor.forClass(Integer.class);

        tradingStrategy.buy(TRADE_SECURITY_IBM, 100, 10);
        verify(executionService, times(1))
                .buy(securityCaptor.capture(), priceCaptor.capture(), volumeCaptor.capture());
        assertThat(securityCaptor.getValue()).isEqualTo(TRADE_SECURITY_IBM);
        assertThat(priceCaptor.getValue()).isEqualTo(90.00);
        assertThat(volumeCaptor.getValue()).isEqualTo(10);
    }

    @Test
    @DisplayName("Test case for buy trade for security")
    void buyForInvalidTradeSecurity() throws InterruptedException {
        final ArgumentCaptor<String> securityCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        final ArgumentCaptor<Integer> volumeCaptor = ArgumentCaptor.forClass(Integer.class);

        tradingStrategy.buy(TRADE_SECURITY_ICICI, 100, 10);
        verify(executionService, times(0))
                .buy(securityCaptor.capture(), priceCaptor.capture(), volumeCaptor.capture());
        assertThrows(MockitoException.class, () -> securityCaptor.getValue());
    }

    @Test
    @DisplayName("Test case for sell trade for security")
    void sell() throws InterruptedException {
        final ArgumentCaptor<String> securityCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        final ArgumentCaptor<Integer> volumeCaptor = ArgumentCaptor.forClass(Integer.class);

        tradingStrategy.sell(TRADE_SECURITY_IBM, 100.0, 10);
        verify(executionService, times(1))
                .sell(securityCaptor.capture(), priceCaptor.capture(), volumeCaptor.capture());
        assertThat(securityCaptor.getValue()).isEqualTo(TRADE_SECURITY_IBM);
        assertThat(priceCaptor.getValue()).isEqualTo(90.0d);
        assertThat(volumeCaptor.getValue()).isEqualTo(10);
    }

    private class MockPriceSource extends PriceSourceImpl {

        private String security;
        private double price;

        MockPriceSource(final String security, final double price) {
            this.security = security;
            this.price = price;
        }

        private final List<PriceListener> priceListeners = new CopyOnWriteArrayList<>();

        @Override
        public void addPriceListener(final PriceListener listener) {
            priceListeners.add(listener);
        }

        @Override
        public void removePriceListener(final PriceListener listener) {
            priceListeners.remove(listener);
        }

        @Override
        public void run() {
            priceListeners.forEach(priceListener -> priceListener.priceUpdate(security, price));
        }
    }
}
