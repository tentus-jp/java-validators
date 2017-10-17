package jp.tentus.validators;

import org.apache.commons.lang3.StringUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.*;
import java.util.Arrays;

/**
 * 列挙型のバリデーションを行います。
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {Enum.EnumValidator.class})
public @interface Enum {

    Class<?>[] groups() default {};

    /**
     * 対象が文字列の場合、列挙型名と比較する際に大文字・小文字を区別するかどうかを表します。
     *
     * @return 大文字・小文字を区別しない場合 true, それ以外は false 。
     */
    boolean ignoreCase() default true;

    String message() default "{javax.validation.constraints.Enum.message}";

    Class<? extends Payload>[] payload() default {};

    /**
     * 対象の列挙型を表します。
     *
     * @return 対象の列挙型のクラスオブジェクト。
     */
    Class<? extends java.lang.Enum> type();

    @Documented
    @Target({ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        Enum[] value();
    }

    /**
     * 対象の列挙型がこのインターフェースを実装している場合、バリデーションをこのメソッドで行うようになります。
     */
    interface Validator {

        boolean isValid(Object value);

    }

    /**
     * 列挙型の検証を行います。
     */
    class EnumValidator implements ConstraintValidator<Enum, Object> {

        /**
         * 対象の列挙型クラスを表します。
         */
        private Class<? extends java.lang.Enum> enumClass;

        /**
         * 大文字・小文字を区別するかどうかを表します。
         */
        private boolean ignoreCase;

        @Override
        public void initialize(Enum constraintAnnotation) {
            this.ignoreCase = constraintAnnotation.ignoreCase();
            this.enumClass = constraintAnnotation.type();
        }

        private boolean isEquals(CharSequence chars1, CharSequence chars2) {
            int length = chars1.length();

            if (length != chars2.length()) {
                return false;
            }

            for (int index = 0; index < length; index++) {
                char c1 = chars1.charAt(index);
                char c2 = chars2.charAt(index);

                if (c1 == c2) {
                    continue;
                }

                if (this.ignoreCase) {
                    char u1 = Character.toUpperCase(c1);
                    char u2 = Character.toUpperCase(c2);

                    if (u1 == u2) {
                        continue;
                    }

                    // グルジア文字の場合、大文字変換が正常に機能しないケースがあるとの事で、
                    // 念のため小文字変換も行って比較しておく必要がある。
                    if (Character.toLowerCase(u1) == Character.toLowerCase(u2)) {
                        continue;
                    }
                }

                return false;
            }

            return true;
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            if (Validator.class.isAssignableFrom(this.enumClass)) {
                return this.validateBy(value);
            } else if (value instanceof Number) {
                return this.validateOrdinal((Number) value);
            } else if (value instanceof CharSequence) {
                return this.validateName((CharSequence) value);
            } else {
                return true;
            }
        }

        private boolean validateBy(Object value) {
            return 0 < Arrays
                    .stream(this.enumClass.getEnumConstants())
                    .filter((e) -> ((Enum.Validator) e).isValid(value))
                    .count();
        }

        private boolean validateName(CharSequence value) {
            if (StringUtils.isNotBlank(value)) {
                return 0 < Arrays
                        .stream(this.enumClass.getEnumConstants())
                        .filter((e) -> this.isEquals(e.name(), value))
                        .count();
            } else {
                return true;
            }
        }

        private boolean validateOrdinal(Number value) {
            if (value != null) {
                return 0 < Arrays
                        .stream(this.enumClass.getEnumConstants())
                        .filter((e) -> e.ordinal() == value.intValue())
                        .count();
            } else {
                return true;
            }
        }

    }

}
