package com.hwz.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {

    private boolean success;
    private T data;
    private String message;

    private Result() {}

    private Result(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    /** 成功：{ success: true, data: payload } */
    public static <T> Result<T> ok(T data) {
        return new Result<>(true, data, null);
    }

    /** 成功但无返回数据：{ success: true } */
    public static <T> Result<T> ok() {
        return new Result<>(true, null, null);
    }

    /** 失败：{ success: false, message: "..." } */
    public static <T> Result<T> fail(String message) {
        return new Result<>(false, null, message);
    }

    /** 失败：从异常读取 message（避免 NPE） */
    public static <T> Result<T> fail(Throwable e) {
        String msg = (e == null) ? "未知错误" : (e.getMessage() == null ? "未知错误" : e.getMessage());
        return new Result<>(false, null, msg);
    }

    // --- getter/setter ---

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
