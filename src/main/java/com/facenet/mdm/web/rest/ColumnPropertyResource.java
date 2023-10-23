package com.facenet.mdm.web.rest;

import com.facenet.mdm.security.AuthoritiesConstants;
import com.facenet.mdm.service.ColumnPropertyService;
import com.facenet.mdm.service.dto.KeyDictionaryDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/column-properties")
public class ColumnPropertyResource {

    @Autowired
    ColumnPropertyService columnPropertyService;

    @PostMapping("")
    public CommonResponse createProperties(@RequestBody KeyDictionaryDTO input) {
        columnPropertyService.createProperty(input);
        return new CommonResponse<>().success();
    }

    @PutMapping("/{keyName}")
    @PreAuthorize(AuthoritiesConstants.ADMIN_AUTH)
    public CommonResponse updateProperties(@PathVariable String keyName, @RequestBody KeyDictionaryDTO input) {
        columnPropertyService.updateProperty(keyName, input, input.getEntityType());
        return new CommonResponse<>().success();
    }

    @DeleteMapping("/{keyName}/{entityType}")
    @PreAuthorize(AuthoritiesConstants.ADMIN_AUTH)
    public CommonResponse deleteProperties(@PathVariable String keyName, @PathVariable Integer entityType) {
        columnPropertyService.deleteProperty(keyName, entityType);
        return new CommonResponse<>().success();
    }

    @GetMapping("/{entityType}")
    //    @PreAuthorize(AuthoritiesConstants.ADMIN_AUTH)
    public CommonResponse getColumnForEntity(@PathVariable Integer entityType) {
        return columnPropertyService.getColumnList(entityType);
    }

    @PostMapping("/import-column/{entityType}")
    @PreAuthorize(AuthoritiesConstants.ADMIN_AUTH)
    public CommonResponse importColumn(@RequestParam("file") MultipartFile file, @PathVariable Integer entityType)
        throws IOException, ParseException {
        return columnPropertyService.importColumn(file, entityType);
    }

    @PostMapping("/search")
    @PreAuthorize(AuthoritiesConstants.ADMIN_AUTH)
    public CommonResponse search(@RequestBody PageFilterInput<KeyDictionaryDTO> input) {
        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }
        return new CommonResponse().success().data(columnPropertyService.search(input.getFilter(), input.getCommon(), pageable));
    }

    @PostMapping("/common-autocomplete")
    public CommonResponse<Set<String>> autocompleteCommonConfig(@RequestBody PageFilterInput<KeyDictionaryDTO> input) {
        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }
        return new CommonResponse()
            .success()
            .data(columnPropertyService.autocompleteCommonConfig(input.getFilter(), input.getCommon(), pageable));
    }
}
