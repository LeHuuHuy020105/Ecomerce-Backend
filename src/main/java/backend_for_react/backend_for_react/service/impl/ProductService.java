package backend_for_react.backend_for_react.service.impl;

import backend_for_react.backend_for_react.common.enums.ProductStatus;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.common.utils.CloudinaryHelper;
import backend_for_react.backend_for_react.common.utils.SecurityUtils;
import backend_for_react.backend_for_react.common.utils.TextUtils;
import backend_for_react.backend_for_react.controller.request.Attribute.AttributeCreationRequest;
import backend_for_react.backend_for_react.controller.request.AttributeValue.AttributeValueCreationRequest;
import backend_for_react.backend_for_react.controller.request.Product.ProductCreationRequest;
import backend_for_react.backend_for_react.controller.request.Product.ProductUpdateRequest;
import backend_for_react.backend_for_react.controller.request.ProductVariant.ProductVariantCreationRequest;
import backend_for_react.backend_for_react.controller.request.VariantAttribute.VariantAttributeRequest;
import backend_for_react.backend_for_react.controller.request.VariantQuantityUpdateRequest;
import backend_for_react.backend_for_react.controller.response.*;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.mapper.ProductMapper;
import backend_for_react.backend_for_react.mapper.ProductVariantMapper;
import backend_for_react.backend_for_react.model.*;
import backend_for_react.backend_for_react.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "PRODUCT - SERVCIE")
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageProductRepository imageProductRepository;
    private final AttributeRepository attributeRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final VariantAttributeValueRepository variantAttributeValueRepository;
    private final SecurityUtils securityUtils;
    private final CloudinaryHelper cloudinaryHelper;
    private final UserBehaviorRepository userBehaviorRepository;



    public PageResponse<ProductBaseResponse> findAll(String keyword, String sort, int page, int size) {
        log.info("KEYWORD : ", keyword);
        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = Sort.by(Sort.Direction.ASC, columnName);
                } else {
                    order = Sort.by(Sort.Direction.DESC, columnName);
                }
            }
        }
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo, size, order);
        Page<Product> products = null;
        if (keyword == null || keyword.isEmpty()) {
            products = productRepository.findAllByProductStatus(ProductStatus.ACTIVE, pageable);
        } else {
            log.info("Keyword");
            keyword = "%" + keyword.toLowerCase() + "%";
            products = productRepository.searchByKeyword(keyword,ProductStatus.ACTIVE, pageable);
        }
        PageResponse response = getProductPageResponse(pageNo, size, products);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_ALL_PRODUCT')")
    public PageResponse<ProductBaseResponse> findAllByAdmin(String keyword, String sort, ProductStatus productStatus, int page, int size) {
        log.info("KEYWORD : ", keyword);
        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String columnName = matcher.group(1);
                if (matcher.group(3).equalsIgnoreCase("asc")) {
                    order = Sort.by(Sort.Direction.ASC, columnName);
                } else {
                    order = Sort.by(Sort.Direction.DESC, columnName);
                }
            }
        }
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo, size, order);
        Page<Product> products = null;
        if (keyword == null || keyword.isEmpty()) {
            if(productStatus != null){
                products = productRepository.findAllByProductStatus(productStatus,pageable);
            }else {
                products = productRepository.findAll(pageable);
            }

        } else {
            log.info("Keyword");
            keyword = "%" + keyword.toLowerCase() + "%";
            if(productStatus != null){products = productRepository.searchByKeyword(keyword,productStatus,pageable);}
            else {
                products =productRepository.searchByKeyword(keyword,pageable);
            }
        }
        PageResponse response = getProductPageResponse(pageNo, size, products);
        return response;
    }


    public List<ProductResponse> getDetailAllProduct() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(product -> getProductDetailResponse(product)).toList();
    }

    /**
     * Gợi ý sản phẩm có phân trang (Pageable - không phân trang thủ công)
     */
    public PageResponse<ProductBaseResponse> getRecommendedProducts(Long userId, String sort, int page, int size) {
        log.info("Get recommended products for userId={}", userId);

        // ✅ Xử lý sort string (vd: "avgRating:desc" hoặc "soldQuantity:asc")
        Sort order = Sort.by(Sort.Direction.DESC, "id"); // mặc định
        if (sort != null && !sort.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(sort);
            if (matcher.find()) {
                String column = matcher.group(1);
                String direction = matcher.group(3);
                order = Sort.by(direction.equalsIgnoreCase("asc") ?
                        Sort.Direction.ASC : Sort.Direction.DESC, column);
            }
        }

        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }
        Pageable pageable = PageRequest.of(pageNo, size, order);

        Page<Product> resultPage;

        // 🔹 Nếu user chưa đăng nhập → hiển thị sản phẩm bán chạy và yếu (guest)
        if (userId == null) {
            resultPage = productRepository.findRecommendedForGuest(pageable);
        } else {
            List<UserBehavior> behaviors = userBehaviorRepository.findByUserId(userId);
            if (behaviors.isEmpty()) {
                resultPage = productRepository.findRecommendedForGuest(pageable);
            } else {
                // Lấy danh sách category mà user tương tác
                Set<Long> productIds = new HashSet<>();
                behaviors.forEach(b -> productIds.add(b.getProduct().getId()));

                List<String> categories = productRepository.findCategoryNamesByProductIds(productIds);

                resultPage = productRepository.findRecommendedForUser(categories, pageable);
            }
        }

        PageResponse response = getProductPageResponse(pageNo, size, resultPage);
        return response;
    }

    /**
     * Gợi ý cho khách vãng lai (chưa đăng nhập)
     */
    private List<Product> recommendForGuest() {
        List<Product> top = productRepository.findTop20ByOrderBySoldQuantityDescAvgRatingDesc();
        List<Product> low = productRepository.findTop10ByOrderBySoldQuantityAscAvgRatingAsc();

        List<Product> mixed = new ArrayList<>(top);
        mixed.addAll(low);
        Collections.shuffle(mixed);
        return mixed;
    }

    /**
     * Gợi ý cho người dùng đã đăng nhập
     */
    private List<Product> recommendForUser(Long userId) {
        List<UserBehavior> behaviors = userBehaviorRepository.findByUserId(userId);

        if (behaviors.isEmpty()) {
            return recommendForGuest();
        }

        // Lấy danh sách sản phẩm mà user từng tương tác
        Set<Long> productIds = new HashSet<>();
        behaviors.forEach(b -> productIds.add(b.getProduct().getId()));

        // Lấy danh sách category liên quan
        List<String> categories = productRepository.findCategoryNamesByProductIds(productIds);

        // Lấy sản phẩm cùng loại và chất lượng cao
        List<Product> related = productRepository.findTop50ByCategoryNamesAndSimilarPrice(categories);

        // Thêm sản phẩm yếu để tăng cơ hội hiển thị
        List<Product> low = productRepository.findTop10ByOrderBySoldQuantityAscAvgRatingAsc();

        related.addAll(low);
        Collections.shuffle(related);
        return related;
    }

    public PageResponse<ProductBaseResponse> findAllByCategory(
            Long categoryId, String keyword, String sort, int page, int size) {

        // B1. Lấy danh sách categoryId bao gồm cả cha và con
        List<Long> categoryIds = new ArrayList<>();
        collectCategoryIds(categoryId, categoryIds);

        // B2. Xử lý sắp xếp
        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Matcher matcher = Pattern.compile("(\\w+?)(:)(.*)").matcher(sort);
            if (matcher.find()) {
                order = Sort.by(
                        matcher.group(3).equalsIgnoreCase("asc")
                                ? Sort.Direction.ASC
                                : Sort.Direction.DESC,
                        matcher.group(1)
                );
            }
        }

        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, order);

        // B3. Gọi repository
        Page<Product> products;
        if (keyword == null || keyword.isEmpty()) {
            products = productRepository.findByCategoryIds(categoryIds, ProductStatus.ACTIVE, pageable);
        } else {
            keyword = "%" + keyword.toLowerCase() + "%";
            products = productRepository.searchByKeywordAndCategory(keyword, ProductStatus.ACTIVE, categoryIds, pageable);
        }

        // B4. Map ra response
        return getProductPageResponse(page, size, products);
    }

    /** Hàm đệ quy lấy tất cả category con **/
    private void collectCategoryIds(Long parentId, List<Long> ids) {
        ids.add(parentId);
        List<Category> children = categoryRepository.findByParent_Id(parentId);
        for (Category child : children) {
            collectCategoryIds(child.getId(), ids);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_PRODUCT')")
    public void save(ProductCreationRequest req) throws IOException {
        log.info("REQUEST : ", req);
        List<String> uploadedUrls = new ArrayList<>();

        // 1. Tạo sản phẩm chính
        Product product = createBaseProduct(req);

        // 2. Xử lý attributes (nếu có)
        List<Attribute> attributes = new ArrayList<>();
        if (req.getAttributes() != null && !req.getAttributes().isEmpty()) {
            attributes = processAttributes(product, req.getAttributes());
        }

        // 3. Xử lý variants (nếu có). Nếu không có, tạo biến thể mặc định.
        if (req.getProductVariant() != null && !req.getProductVariant().isEmpty()) {
            processVariants(product, attributes, req.getProductVariant());
        } else {
            // === QUAN TRỌNG: Tạo biến thể mặc định nếu sản phẩm không có variant ===
            createDefaultVariantForProduct(product);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_PRODUCT')")
    public void update(ProductUpdateRequest req) throws IOException {
        Product product = productRepository.findByIdAndProductStatus(req.getId(), ProductStatus.ACTIVE).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        if (req.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndStatus(req.getCategoryId(), Status.ACTIVE).orElseThrow(() -> new EntityNotFoundException("Category not found"));
            product.setCategory(category);
        }
        if (req.getDescription() != null) product.setDescription(req.getDescription());
        if(req.getName() != null) product.setName(req.getName());
        if(req.getSalePrice() != null) product.setSalePrice(req.getSalePrice());
        if(req.getListPrice() !=null) product.setListPrice(req.getListPrice());

        if (req.isRemoveVideo()) {
            cloudinaryHelper.deleteByUrl(List.of(req.getVideo()));
            product.setUrlvideo(null);
        } else if (req.getVideo() != null) {
            product.setUrlvideo(req.getVideo());
        }


        if (req.isRemoveCoverImage()) {
            product.setUrlCoverImage(null);
        } else if (req.getCoverImage() != null) {
            product.setUrlCoverImage(req.getCoverImage());
        }

        productRepository.save(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('RESTORE_PRODUCT')")
    public void restoreProduct(Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST,MessageError.PRODUCT_NOT_FOUND));
        if(!product.getProductStatus().equals(ProductStatus.INACTIVE)){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Product is active");
        }
        product.setProductStatus(ProductStatus.ACTIVE);
        productRepository.save(product);
    }

    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_PRODUCT')")
    public void delete(Long id) {
        Product product = productRepository.findByIdAndProductStatus(id,ProductStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.BAD_REQUEST,MessageError.PRODUCT_NOT_FOUND));
        product.setProductStatus(ProductStatus.INACTIVE);
        productRepository.save(product);
    }


    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndProductStatus(id,ProductStatus.ACTIVE).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return getProductDetailResponse(product);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_DETAIL_PRODUCT')")
    public ProductResponse getProductByIdForAdmin(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return getProductDetailResponse(product);
    }

    /**
     * Tao san pham chung
     */
    private Product createBaseProduct(ProductCreationRequest req) {
        log.info("REQ: ", req);
        // Tìm danh mục
        Category category = categoryRepository.findByIdAndStatus(req.getCategoryId(),Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        // Tạo sản phẩm chính
        Product newProduct = Product.builder()
                .category(category)
                .soldQuantity(0)
                .avgRating(0.0)
                .description(req.getDescription())
                .name(req.getName())
                .listPrice(req.getListPrice())
                .salePrice(req.getSalePrice())
                .productStatus(ProductStatus.ACTIVE)
                .urlvideo(req.getVideo()) // Lấy từ URL đã upload trước
                .urlCoverImage(req.getCoverImage()) // Lấy từ URL đã upload trước
                .build();
        // Xử lý ảnh sản phẩm (nếu có)
        if (req.getImageProduct() != null) {
            req.getImageProduct().forEach(url -> {
                ImageProduct imageProduct = new ImageProduct();
                imageProduct.setProduct(newProduct);
                imageProduct.setUrl(url);
                imageProduct.setStatus(Status.ACTIVE);
                imageProductRepository.save(imageProduct);
            });
        }
        productRepository.save(newProduct);
        return newProduct;
    }

    /**
     * Xử lý tạo các phân loại (attributes) và giá trị của chúng
     */
    private Attribute processSingleAttribute(Product product, AttributeCreationRequest attributeRequest) {
           Attribute attribute = Attribute.builder()
                    .product(product)
                    .status(Status.ACTIVE)
                    .name(attributeRequest.getName())
                    .build();
            attributeRepository.save(attribute); // Gán attribute từ newAttribute
        return attribute;
    }

    private List<Attribute> processAttributes(Product product, List<AttributeCreationRequest> req) {

        return req.stream().map(attributeRequest -> {
            Attribute attribute = processSingleAttribute(product, attributeRequest);
            List<AttributeValue> attributeValues = attributeRequest.getAttributeValue().stream()
                    .map(attributeValueRequest -> {
                        AttributeValue attributeValue = AttributeValue.builder()
                                .attribute(attribute)
                                .value(attributeValueRequest.getValue())
                                .status(Status.ACTIVE)
                                .urlImage(attributeValueRequest.getImage())
                                .build();
                        return attributeValueRepository.save(attributeValue);
                    }).toList();
            attribute.setValues(attributeValues);
            return attribute;
        }).toList();
    }

    /**
     * Tạo các biến thể sản phẩm từ các tổ hợp thuộc tính
     */
    private ProductVariant makeBaseProductVariant(ProductVariantCreationRequest variantRequest , Product product) {
        ProductVariant productVariant = ProductVariant.builder()
                .product(product)
                .height(variantRequest.getHeight())
                .width(variantRequest.getWidth())
                .length(variantRequest.getLength())
                .weight(variantRequest.getWeight())
                .price(variantRequest.getPrice())
                .quantity(0)
                .status(Status.ACTIVE)
                .sku(generateSku(product, variantRequest))
                .build();
        return productVariant;
    }
    private List<ProductVariant> processVariants(Product product, List<Attribute> attributes, List<ProductVariantCreationRequest> req) {
        log.info("ProductVariantCreationRequest: ", req);
        return req.stream().map(variantRequest -> {
            ProductVariant productVariant = makeBaseProductVariant(variantRequest, product);
            productVariantRepository.save(productVariant);
            variantRequest.getVariantAttributes().forEach(combo -> {
                AttributeValue attributeValue = findAttributeValue(attributes, combo).orElseThrow(() -> new BusinessException(
                        ErrorCode.EXISTED,
                        "Attribute value not found: " + combo.getAttribute() + " - " + combo.getValue()));
                VariantAttributeValue vav = VariantAttributeValue.builder()
                        .productVariant(productVariant)
                        .attributeValue(attributeValue)
                        .status(Status.ACTIVE)
                        .build();
                variantAttributeValueRepository.save(vav);
            });
            return productVariant;
        }).toList();
    }

    /**
     * Tạo một biến thể mặc định cho sản phẩm (dành cho sản phẩm đơn giản không có phân loại)
     */
    private ProductVariant createDefaultVariantForProduct(Product product) {
        // Tạo SKU mặc định dựa trên tên sản phẩm
        String defaultSku = generateDefaultSku(product);

        ProductVariant defaultVariant = ProductVariant.builder()
                .product(product)
                .price(product.getListPrice()) // Dùng luôn giá listPrice từ sản phẩm chính
                .quantity(0) // Khởi tạo số lượng = 0
                .sku(defaultSku)
                .status(Status.ACTIVE)
                .build();

        return productVariantRepository.save(defaultVariant);
    }

    /**
     * Hàm tạo SKU mặc định cho sản phẩm không biến thể
     */
    private String generateDefaultSku(Product product) {
        String productCode = product.getName().substring(0, Math.min(3, product.getName().length())).toUpperCase();
        String randomDigits = RandomStringUtils.randomNumeric(4);
        return productCode + "-DEF-" + randomDigits; // Ví dụ: "ABC-DEF-1234"
    }

    /**
     * Tìm giá trị thuộc tính theo tên và giá trị
     */
    private Optional<AttributeValue> findAttributeValue(List<Attribute> attributes, VariantAttributeRequest combo) {
        return attributes.stream()
                .filter(attr -> attr.getName().equalsIgnoreCase(combo.getAttribute()))
                .flatMap(attr -> attr.getValues().stream())
                .filter(value -> value.getValue().equalsIgnoreCase(combo.getValue()))
                .findFirst();
    }

    /**
     * Tạo SKU tự động
     */
    public static String generateSku(Product product, ProductVariantCreationRequest request) {
        log.info("ProductVariantCreationRequest : ", request);
        String name = TextUtils.removeVietnameseAccent(product.getName());
        String productCode = name.substring(0, 3).toUpperCase();
        String variantCode = request.getVariantAttributes().stream()
                .sorted(Comparator.comparing(VariantAttributeRequest::getAttribute))
                .map(c -> c.getValue().substring(0, 1))
                .collect(Collectors.joining());
        return productCode + "-" + variantCode + "-" + RandomStringUtils.randomNumeric(4);
    }


    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_PRODUCT_VARIANT')")
    public void addVariant(Long productId, ProductVariantCreationRequest req) {
        Product product = productRepository.findByIdAndProductStatus(productId,ProductStatus.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,MessageError.PRODUCT_NOT_FOUND));
        ProductVariant productVariant = makeBaseProductVariant(req, product);
        List<VariantAttributeValue> variantAttributeValues = req.getVariantAttributes().stream()
                .map(variantAttributeRequest -> {
                    Attribute attribute = attributeRepository.findByNameAndStatusAndProduct(variantAttributeRequest.getAttribute(),Status.ACTIVE,product)
                            .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, "Attribute not found"));
                    AttributeValue attributeValue = attributeValueRepository.findByAttributeAndValueAndStatus(attribute,variantAttributeRequest.getValue(),Status.ACTIVE)
                            .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST, "Attribute value not found"));
                    VariantAttributeValue variantAttributeValue = new VariantAttributeValue();
                    variantAttributeValue.setAttributeValue(attributeValue);
                    variantAttributeValue.setStatus(Status.ACTIVE);
                    variantAttributeValue.setProductVariant(productVariant);
                    return variantAttributeValue;
                })
                .toList();
        productVariant.setAttributeValues(variantAttributeValues);
        productVariantRepository.save(productVariant);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_PRODUCT_VARIANT')")
    public void updateVariant(Long productId, Long variantId, ProductVariantCreationRequest req) {
        ProductVariant variant = productVariantRepository.findByIdAndStatus(variantId,Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Variant not found"));
        if (!variant.getProduct().getId().equals(productId))
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Variant not part of product");

        if(req.getPrice() != null) variant.setPrice(req.getPrice());
        if(req.getWeight() != null) variant.setWeight(req.getWeight());
        if(req.getHeight() != null) variant.setHeight(req.getHeight());
        if(req.getWidth() != null) variant.setWidth(req.getWidth());
        if(req.getLength() != null) variant.setLength(req.getLength());
        productVariantRepository.save(variant);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_PRODUCT_VARIANT')")
    public void deleteVariant(Long productId, Long variantId) {
        ProductVariant variant = productVariantRepository.findByIdAndStatus(variantId,Status.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("Variant not found"));
        if (!variant.getProduct().getId().equals(productId))
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Variant not part of product");

        variant.setStatus(Status.INACTIVE);
        productVariantRepository.save(variant);
    }


    /**
     * Xây dựng response
     */

    private ProductResponse getProductDetailResponse(Product product) {
        List<ProductVariantResponse> productVariantResponses = product.getVariants().stream()
                .filter(productVariant -> productVariant.getStatus() == Status.ACTIVE)
                .map(productVariant -> ProductVariantMapper.getProductVariantResponse(productVariant))
                .toList();
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .salePrice(product.getSalePrice())
                .description(product.getDescription())
                .listPrice(product.getListPrice())
                .categoryId(product.getCategory().getId())
                .coverImage(product.getUrlCoverImage())
                .video(product.getUrlvideo())
                .soldQuantity(product.getSoldQuantity())
                .avgRating(product.getAvgRating())
                .imageProduct(getImageProduct(product))
                .attributes(getAttributeResponse(product))
                .productVariant(productVariantResponses)
                .productStatus(product.getProductStatus())
                .createAt(product.getCreatedAt())
                .updateAt(product.getUpdatedAt())
                .build();
    }

    private ProductBaseResponse getProductBaseResponse(Product product) {
        return ProductBaseResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .listPrice(product.getListPrice())
                .urlvideo(product.getUrlvideo())
                .urlCoverImage(product.getUrlCoverImage())
                .soldQuantity(product.getSoldQuantity())
                .description(product.getDescription())
                .avgRating(product.getAvgRating())
                .salePrice(product.getSalePrice())
                .status(product.getProductStatus())
                .createdAt(product.getCreatedAt())
                .updateAt(product.getUpdatedAt())
                .build();
    }


    private List<AttributeResponse> getAttributeResponse(Product product) {
        List<AttributeResponse> attributeResponses = attributeRepository.findAllByProduct(product).stream()
                .filter(attribute -> attribute.getStatus()==Status.ACTIVE).map(
                attribute -> AttributeResponse.builder()
                        .id(attribute.getId())
                        .name(attribute.getName())
                        .attributeValue(getAttributeValueResponse(attribute))
                        .build()
        ).toList();
        return attributeResponses;
    }

    private List<AttributeValueResponse> getAttributeValueResponse(Attribute attribute) {
        List<AttributeValueResponse> attributeValueResponses = null;
        attributeValueResponses = attributeValueRepository.findAllByAttribute(attribute).stream().map(
                attributeValue -> AttributeValueResponse.builder()
                        .id(attributeValue.getId())
                        .image(attributeValue.getUrlImage())
                        .value(attributeValue.getValue())
                        .build()
        ).toList();

        return attributeValueResponses;
    }


    private List<String> getImageProduct(Product product) {
        List<String> imageProduct = imageProductRepository.findAllByStatusAndProduct(Status.ACTIVE, product).stream().map(
                image -> image.getUrl()
        ).toList();
        return imageProduct;
    }

    private PageResponse<ProductBaseResponse> getProductPageResponse(int page, int size, Page<Product> products) {
        log.info("products : {}" ,products);
        List<ProductBaseResponse> productList = products.stream()
                .map(product -> getProductBaseResponse(product))
                .toList();
        PageResponse<ProductBaseResponse> response = new PageResponse<>();
        response.setPageNumber(page + 1);
        response.setPageSize(size);
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());
        response.setData(productList);
        return response;
    }

}
