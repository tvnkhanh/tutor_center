package ptit.tvnkhanh.tutor_center_management.services.cloudinary;

import android.os.Handler;
import android.os.Looper;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class UploadImageTask {
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void uploadImage(File file, UploadCallback callback) {
        executorService.submit(() -> {
            Cloudinary cloudinary = CloudinaryConfig.getCloudinary();
            try {
                Map<String, Object> uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                String url = (String) uploadResult.get("url");

                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onSuccess(url);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                });
            }
        });
    }

    public interface UploadCallback {
        void onSuccess(String url);
        void onFailure(Exception e);
    }
}
