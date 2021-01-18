package com.db.dataplatform.techtest.server.component.impl;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import com.db.dataplatform.techtest.server.component.Server;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerImpl implements Server {

    private static final String URI_PUSHDATA = "http://localhost:8090/hadoopserver/pushbigdata";
    private final DataBodyService dataBodyServiceImpl;
    private final ModelMapper modelMapper;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    RetryTemplate retryTemplate;

    /**
     * @param envelope
     * @return true if there is a match with the client provided checksum.
     */
    @Override
    public boolean saveDataEnvelope(DataEnvelope envelope) {

        // Save to persistence.
        persist(envelope);
        // Push to big data lake
        pushToBigData(envelope);
        log.info("Data persisted successfully, data name: {}", envelope.getDataHeader().getName());
        return true;
    }

    private void persist(DataEnvelope envelope) {
        log.info("Persisting data with attribute name: {}", envelope.getDataHeader().getName());
        DataHeaderEntity dataHeaderEntity = modelMapper.map(envelope.getDataHeader(), DataHeaderEntity.class);

        DataBodyEntity dataBodyEntity = modelMapper.map(envelope.getDataBody(), DataBodyEntity.class);
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);

        saveData(dataBodyEntity);
    }

    private void saveData(DataBodyEntity dataBodyEntity) {
        dataBodyServiceImpl.saveDataBody(dataBodyEntity);
    }

    private void pushToBigData(DataEnvelope dataEnvelope) throws HttpServerErrorException {
        log.info("Pushing data {} to {}", dataEnvelope.getDataHeader().getName(), URI_PUSHDATA);
        HttpEntity<DataEnvelope> request = new HttpEntity<>(dataEnvelope);
        // retry to post to data lake based on retry config bean retry attempts.
        retryTemplate.execute(context -> {
            ResponseEntity<Boolean> response = restTemplate.postForEntity(URI_PUSHDATA, request, Boolean.class);
            return null;
        });
    }

    @Override
    public List<DataEnvelope> getDataByBlockType(BlockTypeEnum blockTypeEnum) {
        List<DataBodyEntity> dataBodyEntities = dataBodyServiceImpl.getDataByBlockType(blockTypeEnum);
        List<DataEnvelope> dataEnvelopes = dataBodyEntities.stream()
                .map( dataBodyEntity -> {
                    return new DataEnvelope(
                            modelMapper.map(dataBodyEntity.getDataHeaderEntity(), DataHeader.class),
                            modelMapper.map(dataBodyEntity, DataBody.class)
                    );
                }).collect(Collectors.toList());
        return dataEnvelopes;
    }

    @Override
    public boolean patchData(String name, String newBlockType){
        return dataBodyServiceImpl.patchData(name,newBlockType);
    }

}
