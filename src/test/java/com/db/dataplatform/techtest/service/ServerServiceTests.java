package com.db.dataplatform.techtest.service;

import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.component.impl.ServerImpl;
import com.db.dataplatform.techtest.server.mapper.ServerMapperConfiguration;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.persistence.model.DataBodyEntity;
import com.db.dataplatform.techtest.server.persistence.model.DataHeaderEntity;
import com.db.dataplatform.techtest.server.service.DataBodyService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.db.dataplatform.techtest.TestDataHelper.createTestDataBodyEntityList;
import static com.db.dataplatform.techtest.TestDataHelper.createTestDataEnvelopeApiObject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ServerServiceTests {

    public static final String HEADER_NAME = "TSLA-USDGBP-10Y";
    private DataBodyEntity expectedDataBodyEntity;
    private DataEnvelope testDataEnvelope;

    @Spy
    private DataBodyService dataBodyServiceImplMock;

    @Mock
    private ModelMapper modelMapper;

    @Spy
    RetryTemplate retryTemplate;

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    private Server server  = new ServerImpl(dataBodyServiceImplMock, modelMapper);;


    @Before
    public void setup() {

        ServerMapperConfiguration serverMapperConfiguration = new ServerMapperConfiguration();
        modelMapper = serverMapperConfiguration.createModelMapperBean();

        testDataEnvelope = createTestDataEnvelopeApiObject();
        expectedDataBodyEntity = modelMapper.map(testDataEnvelope.getDataBody(), DataBodyEntity.class);
        expectedDataBodyEntity.setDataHeaderEntity(modelMapper.map(testDataEnvelope.getDataHeader(), DataHeaderEntity.class));

        server = new ServerImpl(dataBodyServiceImplMock, modelMapper);
        MockitoAnnotations.initMocks(this);
    }

    /*
     *  Given a request for service to save the model data
     *  Ensure that model is converted to entity and Persisted in database
     */
    @Test
    public void shouldSaveDataEnvelopeAsExpected() throws NoSuchAlgorithmException, IOException {
        dataBodyServiceImplMock.saveDataBody(expectedDataBodyEntity);
        boolean success = server.saveDataEnvelope(testDataEnvelope);
        assertThat(success).isTrue();
        verify(dataBodyServiceImplMock, times(1)).saveDataBody(eq(expectedDataBodyEntity));
    }

    /*
     *  Given a request for fetching all the records for a given BlockType
     *  Ensure that data is fetched from database and returned succesfully.
     */
    @Test
    public void shouldGetDataEnvelopeAsExpected() {
        List<DataBodyEntity> dataBodyEntities = createTestDataBodyEntityList();
        when(dataBodyServiceImplMock.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA)).thenReturn(dataBodyEntities);
        List<DataEnvelope> dataEnvelopeList = server.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA);
        verify(dataBodyServiceImplMock, times(1)).getDataByBlockType(eq(BlockTypeEnum.BLOCKTYPEA));
        assertThat(dataEnvelopeList.size() == 2);
        assertThat(dataEnvelopeList.get(0).getDataHeader().getBlockType().equals(BlockTypeEnum.BLOCKTYPEA));
    }

    /*
     *  Given a request for updating records for a given name,
     *  update it with given block type
     *  and return the boolean value as successfull or not.
     */
    @Test
    public void shouldPatchDataAsExpected(){
        when(dataBodyServiceImplMock.patchData(HEADER_NAME, BlockTypeEnum.BLOCKTYPEA.name())).thenReturn(true);
        boolean response = server.patchData(HEADER_NAME, BlockTypeEnum.BLOCKTYPEA.name());
        verify(dataBodyServiceImplMock, times(1)).patchData(HEADER_NAME, BlockTypeEnum.BLOCKTYPEA.name());
        assertThat(response).isTrue();
    }
}
