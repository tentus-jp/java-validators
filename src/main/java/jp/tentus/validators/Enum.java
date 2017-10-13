package jp.tentus.validators;

import org.apache.commons.lang3.StringUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 列挙型のバリデーションを行います。
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {Enum.EnumValidator.class})
public @interface Enum {

    Class<?>[] groups() default {};

    String message() default "{javax.validation.constraints.Enum.message}";

    Class<? extends Payload>[] payload() default {};

    Class<? extends java.lang.Enum> value();

    @Documented
    @Target({ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface List {
        Enum[] value();
    }

    public static class EnumValidator implements ConstraintValidator<Enum, String> {

        private java.util.List<String> enumEntries;

        @Override
        public void initialize(Enum constraintAnnotation) {
            this.enumEntries = Arrays
                    .stream(constraintAnnotation.value().getEnumConstants())
                    .map((entry) -> entry.name().toLowerCase(Locale.US))
                    .collect(Collectors.toList());
        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (StringUtils.isNotBlank(value)) {
                return (0 <= this.enumEntries.indexOf(value.toLowerCase(Locale.US)));
            } else {
                return true;
            }
        }

    }

}
