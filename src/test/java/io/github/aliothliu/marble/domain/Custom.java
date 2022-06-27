package io.github.aliothliu.marble.domain;

import io.github.aliothliu.marble.acl.CreatorJpaAclStrategy;
import io.github.aliothliu.marble.infrastructure.acl.Acl;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Acl(strategy = CreatorJpaAclStrategy.class)
public class Custom {

    @Id
    private String id;

    private String name;

    private String department;

    private String createdBy;

    protected Custom() {
    }

    public Custom(String id, String name, String department, String createdBy) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.createdBy = createdBy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
