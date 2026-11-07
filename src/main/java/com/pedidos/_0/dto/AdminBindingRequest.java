package com.pedidos._0.dto;

import java.util.Map;

public class AdminBindingRequest {
    private String queue;
    private String exchange;
    private String routingKey = "";
    private Map<String, Object> arguments;

    public AdminBindingRequest() {}

    public AdminBindingRequest(String queue, String exchange, String routingKey) {
        this.queue = queue;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }
}

