package com.db.dataplatform.techtest.server.utility;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.api.model.DataEnvelope;
import com.db.dataplatform.techtest.server.exception.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class DigestUtility {
    /*
     * Calculate checksum of the the data body and compare with the checksum available in the header.
     */
    public  boolean validateChecksumForDataBody(DataEnvelope dataEnvelope) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(dataEnvelope.getDataBody().getDataBody().getBytes());
        byte[] digest = md.digest();
        String serverChecksum = DatatypeConverter
                .printHexBinary(digest).toUpperCase();
        if (!serverChecksum.equals(dataEnvelope.getDataHeader().getCheckSum())) throw new DataIntegrityViolationException("Data corrupt, Checksum not matching");
        return (serverChecksum.equals(dataEnvelope.getDataHeader().getCheckSum()));
    }
}
