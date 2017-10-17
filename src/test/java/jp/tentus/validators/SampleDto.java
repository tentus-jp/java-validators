package jp.tentus.validators;

import java.io.Serializable;

@Unique(
        unitName = "sample",
        entity = SampleEntity.class,
        attributeNames = "name",
        excludeAttributeNames = "id"
)
public class SampleDto implements Serializable {

    private Long id;

    private String name;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

}
