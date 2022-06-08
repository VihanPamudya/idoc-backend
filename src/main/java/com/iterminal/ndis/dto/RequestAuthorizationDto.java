/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iterminal.ndis.dto;

/**
 *
 * @author suranga Created date: Feb 20, 2022
 */
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
