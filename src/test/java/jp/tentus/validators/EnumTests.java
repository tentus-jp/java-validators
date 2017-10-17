package jp.tentus.validators;

import org.junit.Assert;
import org.junit.Test;

public class EnumTests {

    @Enum(type = Sample.class)
    private int num3;

    @Enum(type = Sample.class)
    private long num4;

    @Enum(type = Sample.class)
    private String str1;

    @Enum(type = Sample.class, ignoreCase = false)
    private String str2;

    @Enum(type = Sample2.class)
    private String str5;

    @Test
    public void testValidate1() throws NoSuchFieldException {
        Enum.EnumValidator v1 = new Enum.EnumValidator();
        Enum e1 = this.getClass().getDeclaredField("str1").getAnnotation(Enum.class);

        v1.initialize(e1);

        Assert.assertTrue(v1.isValid("A", null));
        Assert.assertTrue(v1.isValid("a", null));
        Assert.assertFalse(v1.isValid("D", null));
        Assert.assertFalse(v1.isValid("d", null));
    }

    @Test
    public void testValidate2() throws NoSuchFieldException {
        Enum.EnumValidator v2 = new Enum.EnumValidator();
        Enum e2 = this.getClass().getDeclaredField("str2").getAnnotation(Enum.class);

        v2.initialize(e2);

        Assert.assertTrue(v2.isValid("A", null));
        Assert.assertFalse(v2.isValid("a", null));
        Assert.assertFalse(v2.isValid("D", null));
        Assert.assertFalse(v2.isValid("d", null));
    }

    @Test
    public void testValidate3() throws NoSuchFieldException {
        Enum.EnumValidator v3 = new Enum.EnumValidator();
        Enum e3 = this.getClass().getDeclaredField("num3").getAnnotation(Enum.class);

        v3.initialize(e3);

        Assert.assertTrue(v3.isValid(0, null));
        Assert.assertTrue(v3.isValid(1, null));
        Assert.assertTrue(v3.isValid(2, null));
        Assert.assertFalse(v3.isValid(3, null));
    }

    @Test
    public void testValidate4() throws NoSuchFieldException {
        Enum.EnumValidator v4 = new Enum.EnumValidator();
        Enum e4 = this.getClass().getDeclaredField("num4").getAnnotation(Enum.class);

        v4.initialize(e4);

        Assert.assertTrue(v4.isValid(0L, null));
        Assert.assertTrue(v4.isValid(1L, null));
        Assert.assertTrue(v4.isValid(2L, null));
        Assert.assertFalse(v4.isValid(3L, null));
    }

    @Test
    public void testValidate5() throws NoSuchFieldException {
        Enum.EnumValidator v5 = new Enum.EnumValidator();
        Enum e5 = this.getClass().getDeclaredField("str5").getAnnotation(Enum.class);

        v5.initialize(e5);

        Assert.assertTrue(v5.isValid("A", null));
        Assert.assertTrue(v5.isValid("b", null));
        Assert.assertTrue(v5.isValid("C", null));
        Assert.assertFalse(v5.isValid("d", null));
    }

    private enum Sample {
        A,
        B,
        C
    }

    private enum Sample2 implements Enum.Validator {
        A,
        B,
        C;

        @Override
        public boolean isValid(Object value) {
            return this.name().equalsIgnoreCase(value.toString());
        }
    }

}
