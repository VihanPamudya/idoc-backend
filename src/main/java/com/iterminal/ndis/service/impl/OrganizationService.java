/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.service.impl;

import com.iterminal.exception.CustomException;
import com.iterminal.exception.DoesNotExistException;
import com.iterminal.exception.UnknownException;
import com.iterminal.ndis.model.Organization;
import com.iterminal.ndis.repository.IOrganizationRepository;
import com.iterminal.ndis.service.IOrganizationService;
import com.iterminal.ndis.util.MessagesAndContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;




@Slf4j
@Service
public class OrganizationService implements IOrganizationService {

    private IOrganizationRepository organizationRepository;

    @Autowired
    public OrganizationService(IOrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public Organization findById(Long id) throws CustomException {
        try {

            Optional<Organization> optionalOrganization = organizationRepository.findById(id);

            if (optionalOrganization.isPresent()) {
                return optionalOrganization.get();
            } else {
                throw new DoesNotExistException(MessagesAndContent.ORGANIZATION_M01);
            }

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public List<Organization> getAll() throws CustomException {
        try {
            List<Organization> foundOrganizationList = organizationRepository.findAll();
            return foundOrganizationList;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

}
