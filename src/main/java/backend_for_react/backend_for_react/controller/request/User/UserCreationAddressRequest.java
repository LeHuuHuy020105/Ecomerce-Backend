package backend_for_react.backend_for_react.controller.request.User;

import backend_for_react.backend_for_react.common.enums.AddressType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreationAddressRequest {

    @NotNull(message = "province must be not blank")
    @Min(value = 1, message = "province invalid")
    private String province;

    @NotNull(message = "district must be not blank")
    @Min(value = 1, message = "district invalid")
    private String district;

    @NotNull(message = "ward must be not blank")
    @Min(value = 1, message = "ward invalid")
    private String ward;


    @NotNull(message = "province id must be not blank")
    @Min(value = 1, message = "province invalid")
    private Integer provinceId;


    @NotNull(message = "district id must be not blank")
    @Min(value = 1, message = "district invalid")
    private Integer districtId;

    @NotNull(message = "ward id must be not blank")
    @Min(value = 1, message = "ward invalid")
    private Integer wardId;

    @NotNull(message = "address must be not blank")
    private String streetAddress;

    private AddressType addressType;
}
