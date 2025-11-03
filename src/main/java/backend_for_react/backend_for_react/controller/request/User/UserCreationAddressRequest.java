package backend_for_react.backend_for_react.controller.request.User;

import backend_for_react.backend_for_react.common.enums.AddressType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreationAddressRequest {

    @NotBlank(message = "province must be not blank")
    private String province;

    @NotBlank(message = "district must be not blank")
    private String district;

    @NotBlank(message = "ward must be not blank")
    private String ward;


    @NotNull(message = "province id must be not blank")
    @Min(value = 1, message = "province invalid")
    private Integer provinceId;


    @NotNull(message = "district id must be not blank")
    @Min(value = 1, message = "district invalid")
    private Integer districtId;

    @NotBlank(message = "ward id must be not blank")
    private String wardId;

    @NotBlank(message = "address must be not blank")
    private String streetAddress;

    private AddressType addressType;
}
