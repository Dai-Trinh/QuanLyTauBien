package com.facenet.mdm.web.rest;


import com.facenet.mdm.service.SeaportService;
import com.facenet.mdm.service.dto.SeaportDTO;
import com.facenet.mdm.service.dto.response.CommonResponse;
import com.facenet.mdm.service.dto.response.PageResponse;
import com.facenet.mdm.service.model.PageFilterInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seaport")
public class SeaportResource {

    @Autowired
    SeaportService seaportService;

    @PostMapping("")
    public PageResponse<List<SeaportDTO>> getAllSeaport(@RequestBody PageFilterInput<SeaportDTO> input){
        return (PageResponse<List<SeaportDTO>>) seaportService.getAllSeaport(input);
    }

    @PostMapping("auto-complete")
    public CommonResponse<String> getAutoComplete(@RequestBody PageFilterInput<SeaportDTO> input){
        return new CommonResponse<String>().success().data(seaportService.getForCommonSearch(input));
    }

    @PostMapping("/new")
    public CommonResponse saveSeaport(@RequestBody SeaportDTO seaportDTO){
        return seaportService.saveSeaport(seaportDTO);
    }

    @PutMapping("/update/{seaportCode}")
    public CommonResponse updateSeaport(@PathVariable("seaportCode") String seaportCode, @RequestBody SeaportDTO seaportDTO){
        return seaportService.updateSeaport(seaportCode, seaportDTO);
    }

    @DeleteMapping("/delete-seaport/{seaportCode}")
    public CommonResponse deleteSeaport(@PathVariable("seaportCode") String seaportCode){
        return seaportService.deleteSeaport(seaportCode);
    }

}
