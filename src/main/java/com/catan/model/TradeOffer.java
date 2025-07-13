package com.catan.model;

public class TradeOffer {

    private final Player fromPlayer;
    private final Player toPlayer;

    private final ResourceType giveResource;
    private final int giveAmount;

    private final ResourceType receiveResource;
    private final int receiveAmount;

    public TradeOffer(Player fromPlayer, Player toPlayer,
                      ResourceType giveResource, int giveAmount,
                      ResourceType receiveResource, int receiveAmount) {
        this.fromPlayer = fromPlayer;
        this.toPlayer = toPlayer;
        this.giveResource = giveResource;
        this.giveAmount = giveAmount;
        this.receiveResource = receiveResource;
        this.receiveAmount = receiveAmount;
    }

    public Player getFromPlayer() {
        return fromPlayer;
    }

    public Player getToPlayer() {
        return toPlayer;
    }

    public ResourceType getGiveResource() {
        return giveResource;
    }

    public int getGiveAmount() {
        return giveAmount;
    }

    public ResourceType getReceiveResource() {
        return receiveResource;
    }

    public int getReceiveAmount() {
        return receiveAmount;
    }
}
