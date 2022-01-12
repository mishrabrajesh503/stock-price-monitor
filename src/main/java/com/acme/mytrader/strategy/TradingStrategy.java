package com.acme.mytrader.strategy;

import com.acme.mytrader.execution.ExecutionService;
import com.acme.mytrader.price.BuyPriceListenerImpl;
import com.acme.mytrader.price.PriceListener;
import com.acme.mytrader.price.PriceSourceThread;
import com.acme.mytrader.price.SellPriceListenerImpl;

/**
 * This application enables monitoring of stock prices so that when any breach appears
 * application hits a trigger level order which gets executed automatically.
 */
public class TradingStrategy {

    private Thread thread;
    private final ExecutionService tradeExecutionService;
    private final PriceSourceThread sourceListener;

    public TradingStrategy(final ExecutionService tradeExecutionService, final PriceSourceThread sourceListener) {
        this.tradeExecutionService = tradeExecutionService;
        this.sourceListener = sourceListener;

    }

    public void buy(final String security, final double price, final int volume) throws InterruptedException {
        final PriceListener priceListener = new BuyPriceListenerImpl(security, price, volume,
                tradeExecutionService, false);
        sourceListener.addPriceListener(priceListener);
        thread = new Thread(sourceListener);
        thread.start();
        thread.join();
        sourceListener.removePriceListener(priceListener);

    }

    public void sell(final String security, final double price, final int volume) throws InterruptedException {
        final PriceListener priceListener = new SellPriceListenerImpl(security, price, volume,
                tradeExecutionService, false);
        sourceListener.addPriceListener(priceListener);
        thread = new Thread(sourceListener);
        thread.start();
        thread.join();
        sourceListener.removePriceListener(priceListener);
    }
}
