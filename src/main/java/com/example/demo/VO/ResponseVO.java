package com.example.demo.VO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ResponseVO {

    /**
     * 调用是否成功
     */
    private Boolean success;

    /**
     * 返回的提示信息
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    private String message;

    /**
     * 内容
     */
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    private Object content;

    public static ResponseVO buildSuccess() {
        ResponseVO response = new ResponseVO();
        response.setSuccess(true);
        return response;
    }

    public static ResponseVO buildSuccess(Object content) {
        ResponseVO response = new ResponseVO();
        response.setContent(content);
        response.setSuccess(true);
        return response;
    }

    public static ResponseVO buildFailure(String message) {
        ResponseVO response = new ResponseVO();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    public boolean getSuccess() {
        return success;
    }

    private void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public Object getContent() {
        return content;
    }

    private void setContent(Object content) {
        this.content = content;
    }
}
