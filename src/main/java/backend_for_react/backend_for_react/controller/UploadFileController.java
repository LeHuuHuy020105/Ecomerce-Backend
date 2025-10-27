package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.common.utils.CloudinaryHelper;
import backend_for_react.backend_for_react.controller.request.DeleteFileRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadFileController {
    private final CloudinaryHelper cloudinaryHelper;

    @PostMapping()
    public ApiResponse<List<String>> upload(@RequestParam("files") List<MultipartFile> files){
        List<String> urls = new ArrayList<>();
        try {
            for(MultipartFile file : files){
                urls.add(cloudinaryHelper.upload(file));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        var response = ApiResponse.<List<String>>builder()
                .status(HttpStatus.OK.value())
                .message("Url")
                .data(urls)
                .build();
        return response;
    }
    @PostMapping("/delete")
    public void deleteAllFile(@RequestBody DeleteFileRequest request) throws IOException {
        cloudinaryHelper.deleteByUrl(request.getFiles());
    }
}
