package com.iterminal.ndis.service;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.CompanyRequestDto;
import com.iterminal.ndis.dto.response.CompanyDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.model.Company;
import com.iterminal.searchfilters.RequestListCountDto;
import com.iterminal.searchfilters.RequestListDto;

import java.util.List;

public interface ICompanyService {

    enum Status {
        Active,
        Inactive,
    }

    public CompanyDto create(CompanyRequestDto company) throws CustomException;

    public long getCount(RequestListCountDto requestListCount) throws CustomException;

    public PaginationDto<Company> getPaginatedList(RequestListDto requestList) throws CustomException;

    public List<Company> getAll() throws CustomException;

    public void delete(Long companyId) throws CustomException;

    public CompanyDto update(Long companyId, CompanyRequestDto company) throws CustomException;

    public Company inactive(Long companyId) throws CustomException;

    public Company active(Long companyId) throws CustomException;

    public CompanyDto findCompanyById(Long companyId) throws CustomException;

    public CompanyDto convertCompanyToCompanyResponseDto(Company company) throws CustomException;

    public List<Company> getCompanyListByStatus(String companyId) throws CustomException;
}
