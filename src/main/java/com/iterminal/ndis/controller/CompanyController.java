package com.iterminal.ndis.controller;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.CompanyRequestDto;
import com.iterminal.ndis.dto.CountResultDto;

import com.iterminal.ndis.dto.response.CompanyDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.model.Company;
import com.iterminal.ndis.service.ICompanyService;
import com.iterminal.ndis.util.ErrorResponseUtil;
import com.iterminal.searchfilters.RequestListCountDto;
import com.iterminal.searchfilters.RequestListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/system-configuration/company-management/company-registration")
public class CompanyController {

    private final ICompanyService companyService;

    @Autowired
    public CompanyController(ICompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping(value = "/view/{companyId}")
    public ResponseEntity<CompanyDto> getCompany(@PathVariable("companyId") Long companyId) {

        try {
            CompanyDto companyDto = companyService.findCompanyById(companyId);
            return new ResponseEntity<>(companyDto, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<Company>> getAllCompanies() {

        try {
            List<Company> companyList = companyService.getAll();
            return new ResponseEntity<>(companyList, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @GetMapping(value = "/count")
    public ResponseEntity<CountResultDto> getCount() {

        try {
            CountResultDto countDro = new CountResultDto();
            long count = companyService.getCount(null);
            countDro.setCount(count);
            return new ResponseEntity<>(countDro, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/list")
    public ResponseEntity<PaginationDto<Company>> getCompanies(@RequestBody RequestListDto requestList) {
        try {
            PaginationDto<Company> companyList = companyService.getPaginatedList(requestList);
            return new ResponseEntity<>(companyList, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/count")
    public ResponseEntity<CountResultDto> getCount(@RequestBody RequestListCountDto requestListCount) {

        try {
            CountResultDto countDro = new CountResultDto();
            long count = companyService.getCount(requestListCount);
            countDro.setCount(count);
            return new ResponseEntity<>(countDro, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/create")
    public ResponseEntity<CompanyDto> createCompany(@RequestBody CompanyRequestDto company) {
        try {
            CompanyDto savedCompany = companyService.create(company);
            return new ResponseEntity<>(savedCompany, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PutMapping(value = "/modify/{companyId}")
    public ResponseEntity<CompanyDto> updateCompany(@PathVariable("companyId") Long companyId, @RequestBody CompanyRequestDto company) {

        try {
            CompanyDto updatedCompany = companyService.update(companyId, company);

            return new ResponseEntity<>(updatedCompany, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @DeleteMapping(value = "/delete/{companyID}")
    public ResponseEntity<Void> deleteUser(@PathVariable("companyId") Long companyId) {

        try {
            companyService.delete(companyId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PutMapping(value = "/{companyId}/inactive")
    public ResponseEntity<Company> inactive(@PathVariable("companyId") Long companyId) {

        try {
            Company inactivatedCompany = companyService.inactive(companyId);
            return new ResponseEntity<Company>(inactivatedCompany, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PutMapping(value = "/{companyId}/active")
    public ResponseEntity<Company> active(@PathVariable("companyId") Long companyId) {

        try {
            Company updatedCompany = companyService.active(companyId);
            return new ResponseEntity<>(updatedCompany, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }
}
