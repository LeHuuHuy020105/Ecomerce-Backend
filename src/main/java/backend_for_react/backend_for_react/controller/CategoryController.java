package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.request.Category.CategoryCreationRequest;
import backend_for_react.backend_for_react.controller.request.Category.CategoryUpdateRequest;
import backend_for_react.backend_for_react.controller.request.Category.MoveCategoryRequest;
import backend_for_react.backend_for_react.service.CategoryService;
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
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size){
        Map<String,Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message","category list");
        result.put("data",categoryService.findAll(keyword,sort,page,size));
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


    @PostMapping("/add")
    public ResponseEntity<Object> createCategory(@RequestBody List<CategoryCreationRequest> req){
        categoryService.save(req);
        return new ResponseEntity<>("",HttpStatus.OK);
    }
    @PostMapping("/move")
    public ResponseEntity<Object> moveCategory(@RequestBody MoveCategoryRequest request){
        categoryService.moveCategory(request);
        return new ResponseEntity<>("",HttpStatus.OK);
    }
    @PutMapping("/update")
    public ResponseEntity<String> updateCategory (@RequestBody CategoryUpdateRequest req){
        categoryService.update(req);
        return new ResponseEntity<>("",HttpStatus.OK);
    }
    @DeleteMapping("/{categoryId}/delete")
    public ResponseEntity<String> deleteCategory (@PathVariable Long categoryId){
        categoryService.delete(categoryId);
        return new ResponseEntity<>("",HttpStatus.OK);
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
