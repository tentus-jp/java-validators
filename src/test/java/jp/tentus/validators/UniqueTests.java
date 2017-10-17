package jp.tentus.validators;

import org.junit.Assert;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Unique バリデータの動作を確認します。
 */
public class UniqueTests {

    /**
     * テストデータを登録します。
     */
    private void createTestData() {
        new SampleEntity();

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("sample");
        EntityManager em = factory.createEntityManager();

        em.getTransaction().begin();

        SampleEntity sample1 = new SampleEntity("Hello !!");

        em.persist(sample1);
        em.flush();

        em.getTransaction().commit();
    }

    /**
     * カスタムバリデータの isValid メソッドについて動作を確認します。
     */
    @Test
    public void testIsValid() {
        this.createTestData();

        SampleDto dto1 = new SampleDto();

        dto1.setName("Hello !!");

        Unique u1 = dto1.getClass().getAnnotation(Unique.class);
        Unique.UniqueValidator v1 = new Unique.UniqueValidator();

        v1.initialize(u1);

        Assert.assertFalse(v1.isValid(dto1, null));
    }

}
