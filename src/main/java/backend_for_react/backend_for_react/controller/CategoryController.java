package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.request.Category.CategoryCreationRequest;
import backend_for_react.backend_for_react.controller.request.Category.CategoryUpdateRequest;
import backend_for_react.backend_for_react.controller.request.Category.MoveCategoryRequest;
import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/list")
    public ResponseEntity<Object> findAll(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) String sort,
                                          @RequestParam(required = false) Status status,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","category list");
        result.put("data",categoryService.findAll(keyword,sort,status,page,size));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }


    @GetMapping("/all")
    public ResponseEntity<Object> findAllWithouPagination(){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","category list");
        result.put("data",categoryService.findAllWithouPagination());
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @PostMapping("/{categoryId}/restore")
    public ApiResponse<Void> restoreCategory(@PathVariable Long categoryId){
        categoryService.restore(categoryId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("category restored")
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<Void> createCategory(@RequestBody List<@Valid CategoryCreationRequest> req){
        categoryService.save(req);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.CREATED.value())
                .message("category created")
                .build();
    }
    @PostMapping("/move")
    public ApiResponse<Void> moveCategory(@RequestBody MoveCategoryRequest request){
        categoryService.moveCategory(request);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("category moved")
                .build();
    }
    @PutMapping("/update")
    public ApiResponse<Void> updateCategory (@RequestBody CategoryUpdateRequest req){
        categoryService.update(req);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("category updated")
                .build();
    }
    @DeleteMapping("/{categoryId}/delete")
    public ApiResponse<Void> deleteCategory (@PathVariable Long categoryId){
        categoryService.delete(categoryId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("category deleted")
                .build();
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Object> getDetailCategory (@PathVariable Long categoryId){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","product detail");
        result.put("data",categoryService.getCategoryById(categoryId));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/{categoryId}/parents")
    public ResponseEntity<Object> getParentCategory (@PathVariable Long categoryId){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","product detail");
        result.put("data",categoryService.getAllParentCategories(categoryId));
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
