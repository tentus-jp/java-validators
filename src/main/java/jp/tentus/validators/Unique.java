package jp.tentus.validators;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {Unique.UniqueValidator.class})
public @interface Unique {

    String[] attributeNames() default {};

    Class<?> entity();

    String[] excludeAttributeNames() default {};

    Class<?>[] groups() default {};

    String message() default "{javax.validation.constraints.Unique.message}";

    Class<? extends Payload>[] payload() default {};

    String unitName() default "";

    @Documented
    @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        Unique[] value();
    }

    class UniqueValidator implements ConstraintValidator<Unique, Object> {

        /**
         * 対象の属性名を表します。
         */
        private String[] attributeNames;

        /**
         * エンティティクラスを表します。
         */
        private Class<?> entityClass;

        /**
         * 除外する属性名を表します。
         */
        private String[] excludeAttributeNames;

        /**
         * EntityManager のユニット名を表します。
         */
        private String unitName;

        /**
         * プロパティの値を取得します。
         *
         * @param object       対象のオブジェクト。
         * @param propertyName 取得する属性名。
         * @return 取得された値。
         * @throws IntrospectionException    PropertyDescriptor の取得に失敗した場合。
         * @throws InvocationTargetException 取得できる状態にない場合。
         * @throws IllegalAccessException    取得できるアクセス権が無い場合。
         */
        private Object getPropertyValue(Object object, String propertyName) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
            PropertyDescriptor pd = new PropertyDescriptor(propertyName, object.getClass());
            Method readMethod = pd.getReadMethod();

            return readMethod.invoke(object);
        }

        @Override
        public void initialize(Unique constraintAnnotation) {
            this.entityClass = constraintAnnotation.entity();
            this.unitName = constraintAnnotation.unitName();
            this.attributeNames = constraintAnnotation.attributeNames();
            this.excludeAttributeNames = constraintAnnotation.excludeAttributeNames();
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            try {
                EntityManagerFactory factory = Persistence.createEntityManagerFactory(this.unitName);
                EntityManager em = factory.createEntityManager();

                CriteriaBuilder builder = em.getCriteriaBuilder();
                CriteriaQuery<Long> query = builder.createQuery(Long.class);
                Root<?> root = query.from(this.entityClass);
                Collection<Predicate> predicates = new ArrayList<>();

                // 一意制を確認する属性名を処理します。
                for (String attributeName : this.attributeNames) {
                    Object attributeValue = this.getPropertyValue(value, attributeName);

                    if (attributeValue != null) {
                        predicates.add(builder.equal(root.get(attributeName), attributeValue));
                    }
                }

                // 除外対象の属性名を処理します。
                // 除外条件は更新時に対象から除外するような用途で利用します。
                if (0 < this.excludeAttributeNames.length) {
                    for (String attributeName : this.excludeAttributeNames) {
                        Object attributeValue = this.getPropertyValue(value, attributeName);

                        if (attributeValue != null) {
                            predicates.add(builder.notEqual(root.get(attributeName), attributeValue));
                        }
                    }
                }

                query.select(builder.count(root));
                query.where(predicates.toArray(new Predicate[0]));

                // 件数を取得します。
                long count = em.createQuery(query).getSingleResult();

                return count == 0;
            } catch (IntrospectionException | InvocationTargetException | IllegalAccessException ex) {
                return false;
            }
        }

    }

}
