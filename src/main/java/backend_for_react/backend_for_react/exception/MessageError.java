package backend_for_react.backend_for_react.exception;

public interface MessageError {
    /***
     * Not found
     */
    String USER_NOT_FOUND = "User not found or user is locked";
    String PERMISSION_NOT_FOUND = "Permission not found";
    String PRODUCT_NOT_FOUND = "Product not found";
    String URL_NOT_FOUND = "Url not found";
    String ROLE_NOT_FOUND = "Role not found";
    String CATEGORY_NOT_FOUND = "Category not found";
    String PRODUCT_VARIANT_NOT_FOUND = "Product variant not found";
    String ORDER_NOT_FOUND = "Order not found";
    String VOUCHER_NOT_FOUND = "Voucher not found";


    /***
     * Not blank
     */
    String GENDER_NOT_BLANK = "Gender must be not blank";
    String FULLNAME_NOT_BLANK = " Fullname must be not blank";
    String EMAIL_NOT_BLANK = "Email must be not blank";
    String PHONE_NOT_BLANK = "Phone must be not blank";
    String PASSWORD_NOT_BLANK = "Password must be not blank";
    String USERNAME_NOT_BLANK = "Username must be not blank";
    String ADDRESS_NOT_BLANK = "Address must be not blank";

    /***
     * Existed
     */
    String USERNAME_EXISTED = "Username existed";
    /***
     * Invalid
     */

    String EMAIL_INVALID = "Email invalid";
    String DOB_INVALID = "Date of birth must be least {min}";
    String TOKEN_INVALID = "Token is invalid";
    /***
     * Auth
     */
    String UNAUTHENTICATED = "Unauthenticated";
    String UNAUTHORIZED = "You don't have permission";

    /***
     * Not Empty
     */
    String ROLE_NOT_EMPTY = "Role must be not empty";


}
