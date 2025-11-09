package backend_for_react.backend_for_react.mapper;

import backend_for_react.backend_for_react.controller.response.AddressResponse;
import backend_for_react.backend_for_react.controller.response.RoleResponse;
import backend_for_react.backend_for_react.controller.response.UserResponse;
import backend_for_react.backend_for_react.model.Address;
import backend_for_react.backend_for_react.model.User;
import backend_for_react.backend_for_react.model.UserHasAddress;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserMapper {

    public static UserResponse getUserResponse(User user) {
        Set<RoleResponse> roleResponses = new HashSet<>(user.getRoles().stream().map(RoleMapper::getRoleResponse).toList());
        return UserResponse.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .fullName(user.getFullName())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .email(user.getEmail())
                .point(user.getPoint())
                .avatar(user.getAvatarImage())
                .totalSpent(user.getTotalSpent())
                .phone(user.getPhone())
                .status(user.getStatus())
                .verifiedEmail(user.getEmailVerified())
                .addressResponses(user.getUserHasAddresses().stream().map(UserMapper::getAddressResponse).toList())
                .userRankResponse(UserRankMapper.toUserRankResponse(user.getUserRank()))
                .roles(roleResponses)
                .build();
    }

    public static UserResponse getPublicUserResponse(User user) {
        List<AddressResponse> addressResponses  = user.getUserHasAddresses().stream()
                .map(UserMapper::getAddressResponse)
                .toList();
        return UserResponse.builder()
                .id(user.getId())
                .userName(user.getUsername())
                .fullName(user.getFullName())
                .status(user.getStatus())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .email(user.getEmail())
                .point(user.getPoint())
                .verifiedEmail(user.getEmailVerified())
                .avatar(user.getAvatarImage())
                .addressResponses(user.getUserHasAddresses().stream().map(UserMapper::getAddressResponse).toList())
                .build();
    }

    public static AddressResponse getAddressResponse(UserHasAddress userHasAddress) {
        return AddressResponse.builder()
                .id(userHasAddress.getAddress().getId())
                .streetAddress(userHasAddress.getAddress().getAddress())
                .district(userHasAddress.getAddress().getDistrict())
                .ward(userHasAddress.getAddress().getWard())
                .customerName(userHasAddress.getAddress().getCustomerName())
                .phoneNumber(userHasAddress.getAddress().getPhoneNumber())
                .province(userHasAddress.getAddress().getProvince())
                .districtId(userHasAddress.getAddress().getDistrictId())
                .wardId(userHasAddress.getAddress().getWardId())
                .provinceId(userHasAddress.getAddress().getProvinceId())
                .addressType(userHasAddress.getAddressType())
                .isDefaultAddress(userHasAddress.getIsDefault())
                .status(userHasAddress.getStatus())
                .build();
    }
}
