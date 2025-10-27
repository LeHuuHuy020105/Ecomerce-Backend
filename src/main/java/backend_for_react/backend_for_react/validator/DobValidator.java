package backend_for_react.backend_for_react.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DobValidator implements ConstraintValidator<DobConstraint, LocalDate> {
    private int min;
    // khoi tao khi constraint khoi tao , muc dich lay thong so cua annotation
    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min(); //lay gia tri min tu anotation
    }

    // ham xu li data co dung hay khong
    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        if(Objects.isNull(localDate)) return true; // neu null tuc la anotation not null thi cho vuot qua

        long years = ChronoUnit.YEARS.between(localDate,LocalDate.now());

        return years >= min;
    }
}
