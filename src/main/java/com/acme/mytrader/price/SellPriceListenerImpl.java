package com.acme.mytrader.price;

import com.acme.mytrader.execution.ExecutionService;

/**
 For the sell security trades
 */
public class SellPriceListenerImpl implements PriceListener {

    private final String tradeSecurity;
    private final double sellTrigger;
    private final int sellQuantity;
    private final ExecutionService executionService;
    private boolean sellTrade;

    public SellPriceListenerImpl(final String tradeSecurity, final double sellTrigger, final int sellQuantity,
                                 final ExecutionService executionService, final boolean sellTrade) {
        this.tradeSecurity = tradeSecurity;
        this.sellTrigger = sellTrigger;
        this.sellQuantity = sellQuantity;
        this.executionService = executionService;
        this.sellTrade = sellTrade;
    }

    public String getTradeSecurity() {
        return tradeSecurity;
    }

    public double getSellTrigger() {
        return sellTrigger;
    }

    public int getSellQuantity() {
        return sellQuantity;
    }

    public ExecutionService getExecutionService() {
        return executionService;
    }

    public boolean isSellTrade() {
        return sellTrade;
    }

    @Override
    public void priceUpdate(final String security, final double price) {
        if (validateSellRequest(security, price)) {
            executionService.sell(security, price, sellQuantity);
            sellTrade = true;
        }
    }

    /**
     Based on price and registered trade security, verify the sell request
     */
    private boolean validateSellRequest(final String security, final double price) {
        return (!sellTrade) && this.tradeSecurity.equals(security) && (price < this.sellTrigger);
    }
}
