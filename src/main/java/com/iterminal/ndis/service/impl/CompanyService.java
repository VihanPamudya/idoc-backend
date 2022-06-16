package com.iterminal.ndis.service.impl;


import com.iterminal.exception.*;
import com.iterminal.ndis.dto.CompanyRequestDto;
import com.iterminal.ndis.dto.response.CompanyDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.model.Company;
import com.iterminal.ndis.model.UserGroup;
import com.iterminal.ndis.repository.CustomQueryRepository;
import com.iterminal.ndis.repository.ICompanyRepository;
import com.iterminal.ndis.repository.IUserRepository;
import com.iterminal.ndis.service.ICompanyService;
import com.iterminal.ndis.service.email.EmailService;
import com.iterminal.ndis.util.DataUtil;
import com.iterminal.ndis.util.InputValidatorUtil;
import com.iterminal.ndis.util.MessagesAndContent;
import com.iterminal.searchfilters.FilterUtil;
import com.iterminal.searchfilters.RequestListCountDto;
import com.iterminal.searchfilters.RequestListDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.iterminal.ndis.util.MessagesAndContent.COMMON_M29;

@Service
public class CompanyService implements ICompanyService {

    private final CustomQueryRepository<Company> customQueryRepository;
    private final IUserRepository userRepository;
    private final ICompanyRepository companyRepository;

    private EmailService emailService;

    final String TABLE_USER = "company";
    final String USER_STATUS_ACTIVE = "Active";
    final String USER_STATUS_INACTIVE = "Inactive";

    @Value("${ndis.url}")
    private String ndisUrl;

    @Autowired
    public CompanyService(IUserRepository userRepository,
                          ICompanyRepository companyRepository,
                          EmailService emailService,
                          CustomQueryRepository customQueryRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.emailService = emailService;
        this.customQueryRepository = customQueryRepository;

    }

    @Override
    public CompanyDto create(CompanyRequestDto company) throws CustomException {
        try {

            Company companyRequest = new Company();

            if (company == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            long currentTime = Instant.now().getEpochSecond();

            String companyName = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M012, company.getCompanyName(), "Company Name", 100);
            Optional<Company> foundCompany = companyRepository.findByName(companyName);
            if(foundCompany.isPresent()) {
                throw new AlreadyExistException("A company with name " + companyName + " already exists.");
            }

            companyRequest.setCompanyName(companyName);

            String companyEmail = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M02, company.getCompanyEmail(), "Company Email", 100);
            companyRequest.setCompanyEmail(companyEmail);

            String companyAddress = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M03, company.getCompanyAddress(), "Company Address", 100);
            companyRequest.setCompanyAddress(companyAddress);

            String storage = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M04, company.getTotalStorage(), "Company Storage", 50);
            companyRequest.setTotalStorage(storage);

//            String documentURL = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M05, company.getDocumentUploadURL(), "Document Upload URL", 100);
            companyRequest.setDocumentUploadURL(company.getDocumentUploadURL());

            String contactName = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M06, company.getContactName(), "Contact Name", 100);
            companyRequest.setContactName(contactName);

            String contactEmail = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M07, company.getContactEmail(), "Contact Email", 100);
            companyRequest.setContactEmail(contactEmail);

            String mobileNo = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M08, company.getContactNumber(), "Contact Number", 100);
            companyRequest.setContactNumber(mobileNo);

            if (companyRepository.countByCompanyEmail(companyEmail) > 0) {
                throw new AlreadyExistException(MessagesAndContent.USER_M034);
            }

            if (companyRepository.countByContactEmail(contactEmail) > 0) {
                throw new AlreadyExistException(MessagesAndContent.USER_M034);
            }


            if (!InputValidatorUtil.isValidEmail(companyEmail)) {
                throw new InvalidInputException(MessagesAndContent.USER_M08);
            }

            if (!InputValidatorUtil.isValidEmail(contactEmail)) {
                throw new InvalidInputException(MessagesAndContent.USER_M08);
            }

            companyRequest.setStatus(USER_STATUS_ACTIVE);
            companyRequest.setCreatedBy(DataUtil.getUserName());
            companyRequest.setCreatedDateTime(currentTime);


            Company savedCompany = companyRepository.save(companyRequest);

            try {
                String messageBody = "Hey " + savedCompany.getCompanyName() + ",\n"
                        + "\n"
                        + "Welcome to Office for National Development Information System. Company account has been created.\n"
                        + "\n"
                        + ndisUrl + " - Office for National Development Information System Administration";
                emailService.send(savedCompany.getContactEmail(), "Create a new company account", messageBody, null);
            } catch (Exception ex) {
                throw ex;
            }

            CompanyDto companyDto = convertCompanyToCompanyResponseDto(savedCompany);
            return companyDto;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public CompanyDto update(Long companyId, CompanyRequestDto company) throws CustomException {
        try {

            if (company == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }


            Optional<Company> foundCompany = companyRepository.findById(companyId);

            if (!foundCompany.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.COMPANY_M09);
            }

            Company currentCompany = foundCompany.get();

            String companyName = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M01, company.getCompanyName(), "Company Name", 100);
            int companyFound=companyRepository.countCompaniesByCompanyNameEquals(companyName);
            if(companyFound>0 && !foundCompany.get().getCompanyName().equals(company.getCompanyName())){
                throw new AlreadyExistException("A company with name " + companyName + " already exists.");
            }
            company.setCompanyName(companyName);

            String companyEmail = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M02, company.getCompanyEmail(), "Company Email", 100);
            company.setCompanyEmail(companyEmail);

            String companyAddress = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M03, company.getCompanyAddress(), "Company Address", 100);
            company.setCompanyAddress(companyAddress);

            String storage = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M04, company.getTotalStorage(), "Company Storage", 50);
            company.setTotalStorage(storage);

//            String documentURL = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M05, company.getDocumentUploadURL(), "Document Upload URL", 100);
            company.setDocumentUploadURL(company.getDocumentUploadURL());

            String contactName = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M06, company.getContactName(), "Contact Name", 100);
            company.setContactName(contactName);

            String contactEmail = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M07, company.getContactEmail(), "Contact Email", 100);
            company.setCompanyEmail(contactEmail);

            String mobileNo = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M08, company.getContactNumber(), "Contact Number", 100);
            company.setContactNumber(mobileNo);

            if (!currentCompany.getCompanyEmail().equals(companyEmail)) {
                if (companyRepository.countByCompanyEmail(companyEmail) > 0) {
                    throw new AlreadyExistException(MessagesAndContent.USER_M034);
                }
            }

            if (!currentCompany.getContactEmail().equals(contactEmail)) {
                if (companyRepository.countByContactEmail(contactEmail) > 0) {
                    throw new AlreadyExistException(MessagesAndContent.USER_M034);
                }
            }


            if (!InputValidatorUtil.isValidEmail(companyEmail)) {
                throw new InvalidInputException(MessagesAndContent.USER_M08);
            }

            if (!InputValidatorUtil.isValidEmail(contactEmail)) {
                throw new InvalidInputException(MessagesAndContent.USER_M08);
            }

            currentCompany.setCompanyName(company.getCompanyName());
            currentCompany.setCompanyEmail(company.getCompanyEmail());
            currentCompany.setCompanyAddress(company.getCompanyAddress());
            currentCompany.setTotalStorage(company.getTotalStorage());
            currentCompany.setDocumentUploadURL(company.getDocumentUploadURL());
            currentCompany.setContactName(company.getContactName());
            currentCompany.setContactEmail(company.getContactEmail());
            currentCompany.setContactNumber(company.getContactNumber());
            Company updatedCompany = companyRepository.save(currentCompany);


            CompanyDto companyDto = convertCompanyToCompanyResponseDto(updatedCompany);
            return companyDto;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public long getCount(RequestListCountDto requestListCount) throws CustomException {
        try {

            long count = 0;

            if (requestListCount == null) {
                count = companyRepository.count();
            } else {
                String sql = FilterUtil.generateCountSql(TABLE_USER, requestListCount.getFilterData());
                count = customQueryRepository.getResultListCount(sql);
            }

            return count;
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            String err = "";
            if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getMessage() != null) {
                err = ex.getCause().getCause().getMessage();
            }
            throw new InvalidFilterInputException("Sql query execute failed. " + err);
        }
    }

    @Override
    public PaginationDto<Company> getPaginatedList(RequestListDto requestList) throws CustomException{
        try {

            if (requestList == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }
            PaginationDto<Company> paginationDto = new PaginationDto<>();
            List<Company> list = new ArrayList<>();
            int page = requestList.getPage();
            int limit = requestList.getLimit();
            if (page <= 0) {
                page = 1;
            }
            if (limit <= 0) {
                limit = 20;
            }
            String sql = FilterUtil.generateListSql(TABLE_USER, requestList.getFilterData(), requestList.getOrderFields(), requestList.isDescending(), page, limit);
            list = customQueryRepository.getResultList(sql, Company.class);
            paginationDto.setData(list);
            paginationDto.setTotalSize(companyRepository.count());
            return  paginationDto;
        } catch (CustomException ex) {
            //log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            String err = "";
            if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getMessage() != null) {
                err = ex.getCause().getCause().getMessage();
            }
            //log.error(ex.getMessage());
            throw new InvalidFilterInputException("Sql query execute failed. " + err);
        }
    }

    @Override
    public List<Company> getAll() throws CustomException {
        try {
            return companyRepository.findAll();

        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public void delete(Long companyId) throws CustomException {
        try {


            Optional<Company> foundCompany = companyRepository.findById(companyId);

            if (foundCompany.isPresent()) {
                companyRepository.delete(foundCompany.get());
            } else {
                throw new DoesNotExistException(MessagesAndContent.COMPANY_M09);
            }

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public Company inactive(Long companyId) throws CustomException {
        try {


            Optional<Company> foundCompany = companyRepository.findById(companyId);

            if (!foundCompany.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.COMPANY_M09);
            }
            Company currentCompany = foundCompany.get();

            if (currentCompany.getStatus().equals(USER_STATUS_INACTIVE)) {
                throw new AlreadyExistException(MessagesAndContent.COMPANY_M010);
            }

            currentCompany.setStatus(USER_STATUS_INACTIVE);

            Company updatedCompany = companyRepository.save(currentCompany);

            try {
                String messageBody = "Hey " + updatedCompany.getCompanyName() + ",\n"
                        + "\n"
                        + "Your company account of the Office for National Development Information System has been deactivated.\n"
                        + "If you want to reactivate your account, please contact the User Administrator on the administration helpline.\n"
                        + "\n"
                        + ndisUrl + "- Office for National Development Information System Administration";
                emailService.send(updatedCompany.getCompanyEmail(), "Inactivation of a company", messageBody, null);
            } catch (Exception ex) {
                throw ex;
            }

            return updatedCompany;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public Company active(Long companyId) throws CustomException {
        try {


            Optional<Company> foundCompany = companyRepository.findById(companyId);

            if (!foundCompany.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.COMPANY_M09);
            }
            Company currentCompany = foundCompany.get();

            if (currentCompany.getStatus().equals(USER_STATUS_ACTIVE)) {
                throw new AlreadyExistException(MessagesAndContent.COMPANY_M011);
            }

            currentCompany.setStatus(USER_STATUS_ACTIVE);

            Company updatedCompany = companyRepository.save(currentCompany);

            try {
                String messageBody = "Hey " + updatedCompany.getCompanyName() + ",\n"
                        + "\n"
                        + "Your company account of the Office for National Development Information System has been deactivated.\n"
                        + "If you want to reactivate your account, please contact the User Administrator on the administration helpline.\n"
                        + "\n"
                        + ndisUrl + "- Office for National Development Information System Administration";
                emailService.send(updatedCompany.getCompanyEmail(), "Inactivation of a company", messageBody, null);
            } catch (Exception ex) {
                throw ex;
            }

            return updatedCompany;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }


    @Override
    public CompanyDto findCompanyById(Long companyId) throws CustomException {
        try {


            Optional<Company> foundCompanies = companyRepository.findById(companyId);

            if (foundCompanies.isPresent()) {

                Company foundCompany = foundCompanies.get();
                CompanyDto companyDto = convertCompanyToCompanyResponseDto(foundCompany);
                return companyDto;
            } else {
                throw new DoesNotExistException(MessagesAndContent.COMPANY_M09);
            }

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public CompanyDto convertCompanyToCompanyResponseDto(Company company) throws CustomException {
        CompanyDto companyDto = new CompanyDto();
        BeanUtils.copyProperties(company, companyDto);
        return companyDto;
    }

    @Override
    public List<Company> getCompanyListByStatus(String status) throws CustomException {
        try {
            ICompanyService.Status statusValue = null;

            String validatedStatus = InputValidatorUtil.validateStringProperty(MessagesAndContent.COMPANY_M012, status);

            try {
                statusValue = ICompanyService.Status.valueOf(validatedStatus);
            } catch (Exception e) {
                throw new InvalidInputException(COMMON_M29);
            }

            List<Company> foundCompanyList = companyRepository.findAllByStatus(validatedStatus);

            return foundCompanyList;
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception e) {
            throw new UnknownException(e.getMessage());
        }
    }
}
