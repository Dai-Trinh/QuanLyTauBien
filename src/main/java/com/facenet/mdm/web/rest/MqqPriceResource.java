package com.facenet.mdm.web.rest;

import com.facenet.mdm.service.MqqPriceService;
import com.facenet.mdm.service.dto.MqqLeadTimeDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendor-information/vendor-detail")
public class MqqPriceResource {

    private final Logger log = LogManager.getLogger(MqqPriceResource.class);

    private final MqqPriceService mqqPriceService;

    public MqqPriceResource(MqqPriceService mqqPriceService) {
        this.mqqPriceService = mqqPriceService;
    }


    @PostMapping("/{vendorCode}/{itemCode}/new-mqq-price")
    public ResponseEntity createNewMqqPrice(
        @PathVariable("vendorCode") String vendorCode,
        @PathVariable("itemCode") String itemCode,
        @RequestBody MqqLeadTimeDTO dto){

        if (dto== null) {
            throw new CustomException(HttpStatus.BAD_REQUEST ,"invalid.param");
        }
        mqqPriceService.createMqqPrice(vendorCode, itemCode, dto);

        return ResponseEntity.ok(
            new CommonResponse<>()
                .isOk(true)
                .errorCode("00")
                .message("Thêm mới giá MOQ thành công")
        );
    }

    @PostMapping("/{vendorCode}/{itemCode}/mqq-price-list")
    public ResponseEntity getPriceMqqAndLeadTime(
        @PathVariable("vendorCode") String vendorCode,
        @PathVariable("itemCode") String itemCode
    ){
        return ResponseEntity.ok(
            new CommonResponse<MqqLeadTimeDTO>()
                .isOk(true)
                .errorCode("00")
                .message("Success")
                .data(mqqPriceService.getAllLeadTimeAndMqqPrice(vendorCode, itemCode))
        );
    }

    @PutMapping("/delete-mqq-price/{mqqPriceId}")
    public ResponseEntity deletePriceMqq( @PathVariable("mqqPriceId") String mqqPriceId ){
        mqqPriceService.deleteMqqPrice(mqqPriceId);
        return ResponseEntity.ok(
            new CommonResponse<>()
                .isOk(true)
                .message("Success")
                .errorCode("00")
        );
    }

}
