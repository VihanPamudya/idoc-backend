package com.iterminal.ndis.service.impl;

import com.iterminal.exception.CustomException;
import com.iterminal.exception.DoesNotExistException;
import com.iterminal.exception.RecordNotFoundException;
import com.iterminal.exception.UnknownException;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.model.Package;
import com.iterminal.ndis.repository.IPackageRepository;
import com.iterminal.ndis.service.IPackageService;
import com.iterminal.searchfilters.RequestListCountDto;
import com.iterminal.searchfilters.RequestListDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class PackageService implements IPackageService {

    private final IPackageRepository packageRepository;

    @Autowired
    public PackageService(IPackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    @Override
    public Package save(Package t) throws CustomException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Package findById(Long id) throws CustomException {
        try {

            Optional<Package> foundPackage = packageRepository.findById(id);

            if (foundPackage.isPresent()) {

                return foundPackage.get();
            } else {
                throw new DoesNotExistException();
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
    public List<Package> getAll() throws CustomException {
        try {
            List<Package> foundPackageList = new ArrayList<>();
            foundPackageList = packageRepository.findAll();
            if (!foundPackageList.isEmpty()) {
                return foundPackageList;
            } else {
                throw new RecordNotFoundException("No Records Available");
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public long getCount(RequestListCountDto requestListCount) throws CustomException {
        try {
            return packageRepository.count();

        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public PaginationDto<Package> getPaginatedList(RequestListDto requestList) throws CustomException {
        try {
            List<Package> foundPackageList = new ArrayList<>();
            foundPackageList = packageRepository.findAll();
            if (!foundPackageList.isEmpty()) {
                PaginationDto<Package> paginationDto = new PaginationDto<>();

                paginationDto.setData(foundPackageList);
                paginationDto.setTotalSize(packageRepository.count());

                return paginationDto;
            } else {
                throw new RecordNotFoundException("No Records Available");
            }

        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public void delete(Long id) throws CustomException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Package update(Long id, Package t) throws CustomException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
