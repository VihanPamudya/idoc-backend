package com.iterminal.ndis.dto;


public class RequestAuthorizationDto {

    protected String username;
    protected String featureAction;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFeatureAction() {
        return featureAction;
    }

    public void setFeatureAction(String featureAction) {
        this.featureAction = featureAction;
    }

}
