package backend_for_react.backend_for_react.controller.request.Shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receiver {
    private String name;
    private String phone;
    private String address;
    private Integer districtId;
    private String wardCode;
}


