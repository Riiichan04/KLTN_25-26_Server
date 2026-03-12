package vn.id.nonglam.kltn.kltn.services;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.id.nonglam.kltn.kltn.config.CloudinaryConfig.CloudinaryProperties;

import java.util.HashMap;
import java.util.Map;

@Service
public class UploadService {
    private final Cloudinary cloudinary;
    private final CloudinaryProperties props;

    @Autowired
    public UploadService(Cloudinary cloudinary, CloudinaryProperties props) {
        this.cloudinary = cloudinary;
        this.props = props;
    }

    public Map<String, Object> generateSignature(String folderName, String fileName, String contentType) {
        try {
            if (folderName == null) return null;

            Map<String, Object> listUrlParams = new HashMap<>(Map.of(
                    "timestamp", System.currentTimeMillis() / 1000L,
                    "folder", folderName,
                    "public_id", fileName)
            );

            /**
             * Signature Version param value:
             *  1 = SHA-1
             *  2 = SHA-256
             */
            String signature = cloudinary.apiSignRequest(listUrlParams, props.getSecret(), 2);
            listUrlParams.put("signature", signature);
            listUrlParams.put("api_key", props.getApiKey());

            return listUrlParams;
        }
        catch (Exception e) {
            return null;
        }
    }
}
