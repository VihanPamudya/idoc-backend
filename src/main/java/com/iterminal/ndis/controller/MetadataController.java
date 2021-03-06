package com.iterminal.ndis.controller;

import com.iterminal.ndis.dto.RequestChanagePasswordDto;
import com.iterminal.exception.CustomException;
import com.iterminal.ndis.service.IMetadataService;
import com.iterminal.ndis.util.DataUtil;
import com.iterminal.ndis.util.ErrorResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/meta-data")
public class MetadataController {

    private final IMetadataService metadataService;

    public MetadataController(
            IMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    //Change Password on first time login
    @PostMapping(value = "/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody RequestChanagePasswordDto request) {
        try {

            String userName = DataUtil.getUserName();

            if (!userName.equals(request.getUsername())) {
                throw new CustomException(CustomException.INVALID_JWT_TOKEN, "Invalid JWT token.");
            }

            metadataService.changePassword(request);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

}
