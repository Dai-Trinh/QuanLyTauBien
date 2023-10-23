package com.facenet.mdm.repository.custom;

import com.facenet.mdm.service.dto.MqqLeadTimeDTO;

public interface MqqPriceCustomRepository {

    MqqLeadTimeDTO findMqqPriceAndLeadTime(String vendorCode, String itemCode);

//    List<ListOfUnitPricesDTO> getMqqPriceWithMinValue(List<ListOfUnitPricesDTO> dtoList);

}
