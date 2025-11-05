package backend_for_react.backend_for_react.controller.response;

import backend_for_react.backend_for_react.common.enums.AddressType;
import backend_for_react.backend_for_react.common.enums.Status;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AddressResponse {
    private Long id;

    private String province;

    private String district;

    private String ward;

    private Integer provinceId;

    private Integer districtId;

    private String wardId;

    private String streetAddress;

    private AddressType addressType;

    private boolean isDefaultAddress;

    private String customerName;
    private String phoneNumber;


    private Status status;
}
