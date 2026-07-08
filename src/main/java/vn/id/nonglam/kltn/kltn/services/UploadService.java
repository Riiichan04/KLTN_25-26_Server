package vn.id.nonglam.kltn.kltn.services;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.id.nonglam.kltn.kltn.config.CloudinaryConfig.CloudinaryProperties;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UploadService {
    private final Cloudinary cloudinary;
    private final CloudinaryProperties props;

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

    public Map<String, Object> getSignature(String folderName) {
        long timestamp = System.currentTimeMillis() / 1000L;

        Map<String, Object> paramsToSign = new HashMap<>();
        paramsToSign.put("timestamp", timestamp);
        paramsToSign.put("folder", folderName);

        String signature = cloudinary.apiSignRequest(paramsToSign, props.getSecret(), 2);

        Map<String, Object> response = new HashMap<>();
        response.put("signature", signature);
        response.put("timestamp", timestamp);
        response.put("cloud_name", props.getName());
        response.put("api_key", props.getApiKey());
        response.put("folder", folderName);

        return response;
    }
}
