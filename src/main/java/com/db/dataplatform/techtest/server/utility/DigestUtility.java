package com.db.dataplatform.techtest.server.utility;

import com.db.dataplatform.techtest.server.api.model.DataBody;
import com.db.dataplatform.techtest.server.exception.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class DigestUtility {
    public static boolean validateChecksumForDataBody(DataBody dataBody, String clientCheckSum) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(dataBody.getDataBody().getBytes());
        byte[] digest = md.digest();
        String serverChecksum = DatatypeConverter
                .printHexBinary(digest).toUpperCase();
        if (!serverChecksum.equals(clientCheckSum)) throw new DataIntegrityViolationException("Data corrupt, Checksum not matching");
        return (serverChecksum.equals(clientCheckSum));
    }
}
