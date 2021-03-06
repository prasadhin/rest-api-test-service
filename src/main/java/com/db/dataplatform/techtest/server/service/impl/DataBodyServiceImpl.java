package com.db.dataplatform.techtest.server.service.impl;

import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.repository.DataStoreRepository;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataBodyServiceImpl implements DataBodyService {

    private final DataStoreRepository dataStoreRepository;

    @Override
    public void saveDataBody(DataBodyEntity dataBody) {
        dataStoreRepository.save(dataBody);
    }

    @Override
    public List<DataBodyEntity> getDataByBlockType(BlockTypeEnum blockType) {
        List<DataBodyEntity> dataBodyEntities = dataStoreRepository.findByDataHeaderEntity_Blocktype(blockType);
        return dataBodyEntities;
    }

    @Override
    public Optional<DataBodyEntity> getDataByBlockName(String blockName) {
        return null;
    }

    @Override
    public boolean patchData(String name, String newBlockType){
        List<DataBodyEntity> dataBodyEntities = dataStoreRepository.findByDataHeaderEntity_Name(name);
        dataBodyEntities.forEach(
                dataBodyEntity -> {
                    dataBodyEntity.getDataHeaderEntity().setBlocktype(BlockTypeEnum.valueOf(newBlockType));
                    dataStoreRepository.save(dataBodyEntity);
                }
        );
        return true;
    };
}
