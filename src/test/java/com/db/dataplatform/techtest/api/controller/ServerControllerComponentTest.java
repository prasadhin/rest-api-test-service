package com.db.dataplatform.techtest.api.controller;

import com.db.dataplatform.techtest.TestDataHelper;
import com.db.dataplatform.techtest.server.api.controller.ServerController;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.exception.HadoopClientException;
import com.db.dataplatform.techtest.server.component.Server;
import com.db.dataplatform.techtest.server.persistence.BlockTypeEnum;
import com.db.dataplatform.techtest.server.utility.DigestUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.util.UriTemplate;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.modelmapper.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class ServerControllerComponentTest {

	public static final String URI_PUSHDATA = "http://localhost:8090/dataserver/pushdata";
	public static final UriTemplate URI_GETDATA = new UriTemplate("http://localhost:8090/dataserver/data/{blockType}");
	public static final UriTemplate URI_PATCHDATA = new UriTemplate("http://localhost:8090/dataserver/update/{name}/{newBlockType}");
	public static final String HEADER_NAME = "TSLA-USDGBP-10Y";

	@Mock
	private Server serverMock;
	@Mock
	private DigestUtility digestUtility;

	private DataEnvelope testDataEnvelope;
	private ObjectMapper objectMapper;
	private MockMvc mockMvc;
	private ServerController serverController;

	@Before
	public void setUp() throws HadoopClientException, NoSuchAlgorithmException, IOException {
		serverController = new ServerController(serverMock,digestUtility);
		mockMvc = standaloneSetup(serverController).build();
		objectMapper = Jackson2ObjectMapperBuilder
				.json()
				.build();

		testDataEnvelope = TestDataHelper.createTestDataEnvelopeApiObject();

		when(serverMock.saveDataEnvelope(any(DataEnvelope.class))).thenReturn(true);
	}

	// API controller Post data assertion
	@Test
	public void testPushDataPostCallWorksAsExpected() throws Exception {

		String testDataEnvelopeJson = objectMapper.writeValueAsString(testDataEnvelope);

		MvcResult mvcResult = mockMvc.perform(post(URI_PUSHDATA)
				.content(testDataEnvelopeJson)
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();

		boolean checksumPass = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());
		assertThat(checksumPass).isTrue();
	}

	// API controller Get data assertion
	@Test
	public void testGetDataPostCallWorksAsExpected() throws Exception {

		when(serverMock.getDataByBlockType(BlockTypeEnum.BLOCKTYPEA)).thenReturn(TestDataHelper.createTestDataEnvelopeList());

		MvcResult mvcResult = mockMvc.perform(get(URI_GETDATA.expand(BlockTypeEnum.BLOCKTYPEA.name()))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].dataHeader.checkSum").
						value(TestDataHelper.createTestDataEnvelopeList().get(0).getDataHeader().getCheckSum()))
				.andExpect(MockMvcResultMatchers.jsonPath("$[0].dataHeader.blockType").
						value(TestDataHelper.createTestDataEnvelopeList().get(0).getDataHeader().getBlockType().name()))
				.andReturn();
		String response = mvcResult.getResponse().getContentAsString();
		System.out.println(response);
	}

    // API controller update assertion
	@Test
	public void testPatchDataPostCallWorksAsExpected() throws Exception {
		when(serverMock.patchData(HEADER_NAME,BlockTypeEnum.BLOCKTYPEA.name())).thenReturn(true);
		MvcResult mvcResult = mockMvc.perform(patch(URI_PATCHDATA.expand(HEADER_NAME,BlockTypeEnum.BLOCKTYPEA.name()))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();
		boolean checksumPass = Boolean.parseBoolean(mvcResult.getResponse().getContentAsString());
		assertThat(checksumPass).isTrue();
	}

	//assert for invalid block type, with status 404, not valid request
	@Test
	public void testPatchForExceptionForInvalidBlock() throws Exception {
		MvcResult mvcResult = mockMvc.perform(patch(URI_PATCHDATA.expand(HEADER_NAME,null))
				.contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isNotFound())
				.andReturn();	}
}
