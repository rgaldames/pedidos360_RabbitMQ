package com.pedidos._0.dto;

public class AdminResponse {
    private boolean success;
    private String message;
    private Object data;

    public AdminResponse() {}

    public AdminResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public AdminResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static AdminResponse ok(String message) {
        return new AdminResponse(true, message);
    }

    public static AdminResponse ok(String message, Object data) {
        return new AdminResponse(true, message, data);
    }

    public static AdminResponse error(String message) {
        return new AdminResponse(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

