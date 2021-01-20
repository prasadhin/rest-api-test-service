package com.db.dataplatform.techtest.server.utility;

import com.db.dataplatform.techtest.TestDataHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class DigestUtilityTest {

    @Mock
    private DigestUtility digestUtility;

    @BeforeEach
    void setUp() {
        digestUtility = new DigestUtility();
    }

    @Test
    void validateDataIsIntactBasedOnChecksum() throws NoSuchAlgorithmException {
        boolean checksum = digestUtility.validateChecksumForDataBody(TestDataHelper.createTestDataEnvelope());
        assertThat(checksum).isTrue();
    }
}