package ptit.tvnkhanh.tutor_center_management.services.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryConfig {
    private static Cloudinary cloudinary;

    public static Cloudinary getCloudinary() {
        if (cloudinary == null) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "dyrszx7wy",
                    "api_key", "588857879945995",
                    "api_secret", "wbpbIBJGH-QeGPxDxLEaTOialj4"
            ));
        }
        return cloudinary;
    }
}
