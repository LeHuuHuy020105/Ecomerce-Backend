package backend_for_react.backend_for_react.controller.request.Shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sender {
    private String name;
    private String phone;
    private String address;
    private String provinceName;
    private String districtName;
    private String wardName;
}
