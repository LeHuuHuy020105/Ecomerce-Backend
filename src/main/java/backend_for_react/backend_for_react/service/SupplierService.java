package backend_for_react.backend_for_react.service;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.controller.request.Supplier.SupplierCreationRequest;
import backend_for_react.backend_for_react.controller.request.Supplier.SupplierUpdateRequest;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.RoleResponse;
import backend_for_react.backend_for_react.controller.response.SupplierResponse;
import backend_for_react.backend_for_react.controller.response.UserResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.model.Supplier;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j(topic = "SUPPLIER-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SupplierService {
    SupplierRepository supplierRepository;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_ALL_SUPPLIER')")
    public PageResponse<SupplierResponse> findAll(String keyword, String sort, Status status, int page, int size) {
        Sort order = Sort.by(Sort.Direction.ASC, "id");
        if (sort != null && !sort.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)"); //tencot:asc||desc
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
        Page<Supplier> suppliers = null;
        if (keyword == null || keyword.isEmpty()) {
            if(status != null){
                suppliers = supplierRepository.findAllByStatus(status,pageable);
            }else {
                suppliers = supplierRepository.findAll(pageable);
            }

        } else {
            keyword = "%" + keyword.toLowerCase() + "%";
            if(status != null){ suppliers = supplierRepository.searchByKeyword(keyword,status,pageable);}
            else{
                suppliers = supplierRepository.searchByKeyword(keyword,pageable);
            }
        }
        PageResponse response = getUserPageResponse(pageNo, size, suppliers);
        return response;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_SUPPLIER')")
    public void save(SupplierCreationRequest request){
        Supplier supplier= Supplier.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .status(Status.ACTIVE)
                .build();
        supplierRepository.save(supplier);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADD_SUPPLIER')")
    public void restoreSupplier(Long id){
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"Supplier not found"));
        if(supplier.getStatus().equals(Status.ACTIVE)){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Supplier is already active");
        }
        supplier.setStatus(Status.ACTIVE);
        supplierRepository.save(supplier);
    }


    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_SUPPLIER')")
    public void update (SupplierUpdateRequest request){
        Supplier supplier = supplierRepository.findByIdAndStatus(request.getId(),Status.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"Supplier not found"));
        if(request.getName() != null){
            supplier.setName(request.getName());
        }
        if(request.getAddress() != null){
            supplier.setAddress(request.getAddress());
        }
        if(request.getPhone() != null){
            supplier.setPhone(request.getPhone());
        }
        supplierRepository.save(supplier);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_SUPPLIER')")
    public void delete(Long id){
        Supplier supplier = supplierRepository.findByIdAndStatus(id,Status.ACTIVE).orElseThrow(()-> new EntityNotFoundException("Supplier not found"));
        supplier.setStatus(Status.INACTIVE);
        supplierRepository.save(supplier);
    }
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_DETAIL_SUPPLIER')")
    public SupplierResponse getSupplierById(Long id){
        Supplier supplier = supplierRepository.findByIdAndStatus(id,Status.ACTIVE).orElseThrow(()-> new EntityNotFoundException("Supplier not found"));
        return getSupplierResponse(supplier);
    }
    public SupplierResponse getSupplierResponse(Supplier supplier) {
        return SupplierResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .phone(supplier.getPhone())
                .address(supplier.getAddress())
                .status(supplier.getStatus())
                .build();
    }

    private PageResponse<SupplierResponse> getUserPageResponse(int page, int size, Page<Supplier> suppliers) {
        List<SupplierResponse> supplierList = suppliers.stream()
                .map(this::getSupplierResponse)
                .toList();

        PageResponse<SupplierResponse> response = new PageResponse<>();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(suppliers.getTotalElements());
        response.setTotalPages(suppliers.getTotalPages());
        response.setData(supplierList);
        return response;
    }

}
