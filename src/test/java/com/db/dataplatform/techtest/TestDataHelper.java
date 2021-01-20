package com.db.dataplatform.techtest;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.api.model.DataHeader;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TestDataHelper {

    public static final String TEST_NAME = "Test";
    public static final String TEST_NAME_EMPTY = "";
    public static final String DUMMY_DATA = "AKCp5fU4WNWKBVvhXsbNhqk33tawri9iJUkA5o4A6YqpwvAoYjajVw8xdEw6r9796h1wEp29D";
    public static final String CHECKSUM = "CECFD3953783DF706878AAEC2C22AA70";

    public static DataHeaderEntity createTestDataHeaderEntity(Instant expectedTimestamp) {
        DataHeaderEntity dataHeaderEntity = new DataHeaderEntity();
        dataHeaderEntity.setName(TEST_NAME);
        dataHeaderEntity.setBlocktype(BlockTypeEnum.BLOCKTYPEA);
        dataHeaderEntity.setCreatedTimestamp(expectedTimestamp);
        return dataHeaderEntity;
    }

    public static DataBodyEntity createTestDataBodyEntity(DataHeaderEntity dataHeaderEntity) {
        DataBodyEntity dataBodyEntity = new DataBodyEntity();
        dataBodyEntity.setDataHeaderEntity(dataHeaderEntity);
        dataBodyEntity.setDataBody(DUMMY_DATA);
        return dataBodyEntity;
    }

    public static DataEnvelope createTestDataEnvelopeApiObject() {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA,CHECKSUM);

        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);
        return dataEnvelope;
    }

    public static DataEnvelope createTestDataEnvelopeApiObjectWithEmptyName() {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(TEST_NAME_EMPTY, BlockTypeEnum.BLOCKTYPEA,CHECKSUM);

        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);
        return dataEnvelope;
    }

    public static DataBodyEntity createTestDataBodyEntity() {
        DataHeaderEntity dataHeaderEntity1 = new DataHeaderEntity();
        dataHeaderEntity1.setName(TEST_NAME);
        dataHeaderEntity1.setBlocktype(BlockTypeEnum.BLOCKTYPEA);
        dataHeaderEntity1.setCreatedTimestamp(Instant.now());
        DataBodyEntity dataBodyEntity = createTestDataBodyEntity(dataHeaderEntity1);
        return dataBodyEntity;
    }

    public static List<DataBodyEntity> createTestDataBodyEntityList() {
        List<DataBodyEntity> dataBodyEntities = new ArrayList<>();
        DataHeaderEntity dataHeaderEntity1 = new DataHeaderEntity();
        dataHeaderEntity1.setName(TEST_NAME);
        dataHeaderEntity1.setBlocktype(BlockTypeEnum.BLOCKTYPEA);
        dataHeaderEntity1.setCreatedTimestamp(Instant.now());
        DataBodyEntity dataBodyEntity1 = createTestDataBodyEntity(dataHeaderEntity1);

        DataHeaderEntity dataHeaderEntity2 = new DataHeaderEntity();
        dataHeaderEntity2.setName(TEST_NAME);
        dataHeaderEntity2.setBlocktype(BlockTypeEnum.BLOCKTYPEA);
        dataHeaderEntity2.setCreatedTimestamp(Instant.now().plusSeconds(100L));
        DataBodyEntity dataBodyEntity2 = createTestDataBodyEntity(dataHeaderEntity2);

        dataBodyEntities.add(dataBodyEntity1);
        dataBodyEntities.add(dataBodyEntity2);
        return dataBodyEntities;
    }

    public static List<DataEnvelope> createTestDataEnvelopeList(){
        List<DataEnvelope> dataEnvelopes = new ArrayList<>();
        dataEnvelopes.add(createTestDataEnvelopeApiObject());
        dataEnvelopes.add(createTestDataEnvelopeApiObject());
        return dataEnvelopes;
    }

    public static DataEnvelope createTestDataEnvelope() {
        DataBody dataBody = new DataBody(DUMMY_DATA);
        DataHeader dataHeader = new DataHeader(TEST_NAME, BlockTypeEnum.BLOCKTYPEA,CHECKSUM);

        DataEnvelope dataEnvelope = new DataEnvelope(dataHeader, dataBody);
        return dataEnvelope;
    }
}
