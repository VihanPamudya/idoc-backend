/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author suranga Created date: Aug 28, 2021
 */
@Repository
public interface IUserRoleRepository extends JpaRepository<UserRole, Long> {

    public List<UserRole> findByEpfNumber(String epfNumber);

}
