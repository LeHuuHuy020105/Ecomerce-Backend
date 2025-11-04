package backend_for_react.backend_for_react.service.impl;

import backend_for_react.backend_for_react.common.enums.OTPType;
import backend_for_react.backend_for_react.common.enums.Rank;
import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.common.enums.UserStatus;
import backend_for_react.backend_for_react.common.utils.SecurityUtils;
import backend_for_react.backend_for_react.controller.request.User.*;
import backend_for_react.backend_for_react.controller.response.AddressResponse;
import backend_for_react.backend_for_react.controller.response.PageResponse;
import backend_for_react.backend_for_react.controller.response.UserResponse;
import backend_for_react.backend_for_react.exception.BusinessException;
import backend_for_react.backend_for_react.exception.ErrorCode;
import backend_for_react.backend_for_react.exception.MessageError;
import backend_for_react.backend_for_react.mapper.UserMapper;
import backend_for_react.backend_for_react.model.*;
import backend_for_react.backend_for_react.repository.*;
import backend_for_react.backend_for_react.service.OTPService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j(topic = "USER-SERVICE")
@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    AddressRepository addressRepository;
    UserHasAddressRepository userHasAddressRepository;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    SecurityUtils securityUtils;
    OTPService otpService;
    UserRankRepository userRankRepository;

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_ALL_USER')")
    // doi voi permission , phan quyen theo permission (nhieu role co nhieu permission nay)
//    @PreAuthorize("hasAuthority('CREATE_DATA')")

    public PageResponse<UserResponse> findAll(String keyword, String sort, int page, int size) {
        log.info("---Find All--");

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
        Page<User> users = null;
        if (keyword == null || keyword.isEmpty()) {
            users = userRepository.findAll(pageable);
        } else {
            keyword = "%" + keyword.toLowerCase() + "%";
            users = userRepository.searchByKeyword(keyword, pageable);
        }
        PageResponse response = getUserPageResponse(pageNo, size, users);
        return response;
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_USER')")
    @Transactional(rollbackFor = Exception.class)
    public Long save(UserCreationRequest req) throws IOException {
        log.info("Service create user");
        if(userRepository.existsByUsername(req.getUsername()))
            throw new BusinessException(ErrorCode.EXISTED,MessageError.USERNAME_EXISTED);
        User user = new User();
        user.setFullName(req.getFullName());
        user.setGender(req.getGender());
        user.setDateOfBirth(req.getDateOfBirth());
        user.setEmail(req.getEmail());
        user.setPhone(req.getPhone());
        user.setUsername(req.getUsername());
        user.setStatus(UserStatus.NONE);
        user.setTotalSpent(BigDecimal.ZERO);
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        UserRank userRank = userRankRepository.findByName(Rank.BRONZE.name())
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"User rank not found"));
        user.setUserRank(userRank);

        Set<Role> roles = new HashSet<>();
        for(Long roleId : req.getRoleId()){
            Role role = roleRepository.findByIdAndStatus(roleId,Status.ACTIVE)
                    .orElseThrow(()-> new BusinessException(ErrorCode.NOT_EXISTED , MessageError.ROLE_NOT_FOUND));
            roles.add(role);
        }
        user.setRoles(roles);
        userRepository.save(user);



        otpService.sendOTP(user, OTPType.VERIFICATION);
        return user.getId();
    }


    @Transactional(rollbackFor = Exception.class)
    public void update(UserUpdateRequest req) {
        User user = securityUtils.getCurrentUser();
        if(req.getFullName() != null){
            user.setFullName(req.getFullName());
        }
        if(req.getGender() != null){
            user.setGender(req.getGender());
        }
        if(req.getDateOfBirth() != null){
            user.setDateOfBirth(req.getDateOfBirth());
        }
        if(req.getPhone() != null){
            user.setPhone(req.getPhone());
        }
        if(req.getAvatar() != null){
            user.setAvatarImage(req.getAvatar());
        }
        userRepository.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void changePassword(UserPasswordRequest req) {
        log.info("Changing user:{}", req);


        User user = securityUtils.getCurrentUser();
        if(!passwordEncoder.matches(req.getOldPassword(),user.getPassword())){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Old password does not match");
        }
        if (req.getPassword().equals(req.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }else {
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Password and Confirm Password not match");
        }
        userRepository.save(user);
        log.info("Changed user:{}", req);
    }

    @Transactional(rollbackFor = Exception.class)
    public void forgotPassword(ForgotPasswordRequest req , String resetToken) {
        log.info("Forgot password");
        User user = userRepository.findByIdAndStatus(req.getUserId(), UserStatus.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        if(!user.getEmailVerified()){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Email not verified");
        }
        boolean verify = otpService.verifyResetToken(req.getUserId() , resetToken);
        if(!verify){
            throw new BusinessException(ErrorCode.BAD_REQUEST , "User not verify OTP ");
        }
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Password and Confirm Password not match");

        }
        user.setPassword(passwordEncoder.encode(req.getPassword()));
    }


    @Transactional
    public void verifyAccount(Long userId, String resetToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EXISTED,MessageError.USER_NOT_FOUND));
        boolean verify = otpService.verifyResetToken(userId , resetToken);
        if(verify){
            user.setEmailVerified(true);
            user.setStatus(UserStatus.ACTIVE);
        }else {
            throw new BusinessException(ErrorCode.BAD_REQUEST , "User not verify OTP ");
        }
    }


    @Transactional
    public List<UserResponse> getAllUserByEmail(String email){
        List<User> users = userRepository.findAllByEmail(email);
        if(users.isEmpty()){
            throw new BusinessException(ErrorCode.BAD_REQUEST, "No user found with email " + email);
        }
        return users.stream()
                .map(UserMapper::getPublicUserResponse)
                .toList();
    }

    @Transactional
    public void changeEmail(String newEmail , String resetToken) {
        User user = securityUtils.getCurrentUser();
        boolean verify = otpService.verifyResetToken(user.getId() , resetToken);
        if(!verify){
            throw new BusinessException(ErrorCode.BAD_REQUEST , "User not verify OTP ");
        }
        user.setEmail(newEmail);
    }

    @Transactional
    public void updateRank(User user){
        UserRank userRank = userRankRepository.findTopEligibleRank(user.getTotalSpent());
        user.setUserRank(userRank);
        userRepository.save(user);
    }


    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_USER')")
    public void delete(Long id) {
        User user = userRepository.findByIdAndStatus(id,UserStatus.ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException(MessageError.USER_NOT_FOUND));
        user.setStatus(UserStatus.INACTIVE);
        userRepository.delete(user);
    }

    @PreAuthorize("hasRole('ADMIN') or hasAuthority('VIEW_DETAIL_USER')")
    public UserResponse getUserById(Long id) {
        log.info("Get user by Id");
        User user = userRepository.findByIdAndStatus(id,UserStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXISTED,MessageError.USER_NOT_FOUND));
        UserResponse userResponse = UserMapper.getUserResponse(user);
        return userResponse;
    }

//    //  Kiem tra username tra ve neu trung voi username dang dang nhap thi cho xem hien tai , tranh xem thong tin nguoi khac
//    @PostAuthorize("returnObject.userName == authentication.name")
    public UserResponse getMyInfo() {
        User user = securityUtils.getCurrentUser();
        return UserMapper.getUserResponse(user);
    }


    @Transactional(rollbackFor = Exception.class)
    public void addAddress(UserCreationAddressRequest req) {
        User user = securityUtils.getCurrentUser();
        Address newAddress = new Address();
        newAddress.setAddress(req.getStreetAddress());
        newAddress.setWard(req.getWard());
        newAddress.setDistrict(req.getDistrict());
        newAddress.setProvince(req.getProvince());
        newAddress.setProvinceId(req.getProvinceId());
        newAddress.setDistrictId(req.getDistrictId());
        newAddress.setWardId(req.getWardId());
        addressRepository.save(newAddress);

        UserHasAddress userHasAddress = new UserHasAddress();
        userHasAddress.setUser(user);
        userHasAddress.setIsDefault(req.isDefaultAddress());
        userHasAddress.setAddress(newAddress);
        userHasAddress.setIsDefault(true);
        userHasAddress.setStatus(Status.ACTIVE);
        if(req.getAddressType() != null){
            userHasAddress.setAddressType(req.getAddressType());
        }

        userHasAddressRepository.updateAllIsDefaultFalse(user.getId());
        userHasAddressRepository.save(userHasAddress);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateAddress(Long userHasAddressId, UserCreationAddressRequest req) {
        User user = securityUtils.getCurrentUser();

        UserHasAddress userHasAddress = userHasAddressRepository.findById(userHasAddressId)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"User has address not found"));
        if(userHasAddress.getUser() != user){
            throw new BusinessException(ErrorCode.BAD_REQUEST,"Address not yours");
        }
        Address address = userHasAddress.getAddress();
        if (req.getStreetAddress() != null) address.setAddress(req.getStreetAddress());
        if(req.getWard() != null) address.setWard(req.getWard());
        if(req.getDistrict() != null) address.setDistrict(req.getDistrict());
        if(req.getProvince() != null) address.setProvince(req.getProvince());
        if(req.getProvinceId() != null) address.setProvinceId(req.getProvinceId());
        if(req.getDistrictId() != null) address.setDistrictId(req.getDistrictId());
        if(req.getWardId() != null) address.setWardId(req.getWardId());
        userHasAddress.setAddress(address);
        if(req.getAddressType() != null) userHasAddress.setAddressType(req.getAddressType());

        userHasAddressRepository.save(userHasAddress);
    }

    @Transactional(rollbackFor = Exception.class)
    public void setDefaultAddress(Long id) {
        User user = securityUtils.getCurrentUser();
        UserHasAddress userHasAddress = userHasAddressRepository.findByIdAndUserAndStatus(id,user,Status.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"Address not found or not yours"));

        userHasAddressRepository.updateAllIsDefaultFalse(user.getId());
        userHasAddress.setIsDefault(true);
        userHasAddressRepository.save(userHasAddress);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(Long id) {
        User user = securityUtils.getCurrentUser();
        UserHasAddress userHasAddress = userHasAddressRepository.findByIdAndUserAndStatus(id,user,Status.ACTIVE)
                .orElseThrow(()-> new BusinessException(ErrorCode.BAD_REQUEST,"Address not found or not yours"));
        userHasAddressRepository.delete(userHasAddress);
    }


    // doi voi permission , phan quyen theo permission (nhieu role co nhieu permission nay)
//    @PreAuthorize("hasAuthority('CREATE_DATA')")

    public PageResponse<AddressResponse> findAllAddressUser(String sort, int page, int size) {
        log.info("---Find All--");

        User user = securityUtils.getCurrentUser();
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
        Page<UserHasAddress> userHasAddresses = null;
        userHasAddresses = userHasAddressRepository.findAllByUserAndStatus(user,pageable,Status.ACTIVE);
        PageResponse response = getAddressUserPageResponse(pageNo, size, userHasAddresses);
        return response;
    }




    private PageResponse<AddressResponse> getAddressUserPageResponse(int page, int size, Page<UserHasAddress> userHasAddresses) {
        List<AddressResponse> addressResponseList = userHasAddresses.stream()
                .map(UserMapper::getAddressResponse)
                .toList();

        PageResponse<AddressResponse> response = new PageResponse<>();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(userHasAddresses.getTotalElements());
        response.setTotalPages(userHasAddresses.getTotalPages());
        response.setData(addressResponseList);
        return response;
    }


    private PageResponse<UserResponse> getUserPageResponse(int page, int size, Page<User> users) {
        List<UserResponse> userList = users.stream()
                .map(UserMapper::getUserResponse)
                .toList();

        PageResponse<UserResponse> response = new PageResponse<>();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(users.getTotalElements());
        response.setTotalPages(users.getTotalPages());
        response.setData(userList);
        return response;
    }
}
