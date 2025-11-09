package backend_for_react.backend_for_react.controller;

import backend_for_react.backend_for_react.controller.response.ApiResponse;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.ProductBaseResponse;
import backend_for_react.backend_for_react.service.FavoriteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteController {
    FavoriteService favoriteService;

    @GetMapping("/listForMe")
    public ApiResponse<PageResponse<ProductBaseResponse>> getFavoritesForMe(@RequestParam(required = false) String keyword,
                                                                            @RequestParam(required = false) String sort,
                                                                            @RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProductBaseResponse> favorites = favoriteService.findAll(keyword, sort, page, size);
        return ApiResponse.<PageResponse<ProductBaseResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("List favorites")
                .data(favorites)
                .build();
    }

    @PostMapping("/add")
    public ApiResponse<Void> addFavorite(@RequestParam Long productId) {
        favoriteService.addFavorite(productId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Add favorite product")
                .build();
    }

    @DeleteMapping("/{productId}/delete")
    public ApiResponse<Void> deleteFavorite(@PathVariable Long productId) {
        favoriteService.removeFavorite(productId);
        return ApiResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .message("Remove favorite product")
                .build();
    }
}
