package com.iterminal.ndis.controller;

import com.iterminal.ndis.dto.CountResultDto;
import com.iterminal.ndis.dto.PermissionDto;
import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.model.Package;
import com.iterminal.ndis.service.IPackageService;
import com.iterminal.ndis.util.ErrorResponseUtil;
import com.iterminal.searchfilters.RequestListCountDto;
import com.iterminal.searchfilters.RequestListDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/system-configuration")
public class PackageController {

    private final IPackageService packageService;

    @Autowired
    public PackageController(IPackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping(value = "/permissions")
    public ResponseEntity<PermissionDto> getPermission() {

        try {
            PermissionDto PermissionDto = new PermissionDto();
            List<Package> list = packageService.getAll();
            PermissionDto.setPackageList(list);

            return new ResponseEntity<>(PermissionDto, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @GetMapping(value = "/package/view/{id}")
    public ResponseEntity<Package> getPackage(@PathVariable("id") Long id) {

        try {
            Package systemPackage = packageService.findById(id);
            return new ResponseEntity<>(systemPackage, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/package/list")
    public ResponseEntity<List<Package>> getAll() {

        try {
            List<Package> list = packageService.getAll();
            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @GetMapping(value = "/count")
    public ResponseEntity<CountResultDto> getCount() {

        try {
            CountResultDto countDro = new CountResultDto();
            long count = packageService.getCount(null);
            countDro.setCount(count);
            return new ResponseEntity<>(countDro, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/package/list")
    public ResponseEntity<PaginationDto<Package>> getList(@RequestBody RequestListDto requestList) {
        try {
            PaginationDto<Package> list = packageService.getPaginatedList(requestList);

            return new ResponseEntity<>(list, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/count")
    public ResponseEntity<CountResultDto> getCount(@RequestBody RequestListCountDto requestListCount) {

        try {
            CountResultDto countDro = new CountResultDto();
            long count = packageService.getCount(requestListCount);
            countDro.setCount(count);
            return new ResponseEntity<>(countDro, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

}
