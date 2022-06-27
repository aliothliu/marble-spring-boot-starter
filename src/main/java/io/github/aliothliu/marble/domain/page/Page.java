package io.github.aliothliu.marble.domain.page;

import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "#{marbleProperties.jpa.pageTableName}")
@Getter
@FieldNameConstants
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class Page {

    @EmbeddedId
    @NonNull
    private PageId pageId;

    @Column(name = "name", nullable = false, unique = true, length = 128)
    @NonNull
    private String name;

    @Embedded
    @NonNull
    private Path path;

    @OneToMany(targetEntity = Element.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "#{marbleProperties.jpa.pageElementRefTableName}",
            joinColumns = @JoinColumn(name = "page_id",
                    referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "element_id",
                    referencedColumnName = "ID"))
    private Set<Element> elements;

    protected Page() {
    }

    public void changeName(String name) {
        Assert.notNull(name, "页面名称不能为空");
        this.name = name;
    }

    public void changePath(Path path) {
        Assert.notNull(path, "页面路径不能为空");
        this.path = path;
    }

    public void changeElements(Set<Element> elements) {
        Assert.notNull(elements, "页面元素不能为空");
        this.elements = elements;
    }

    public String path() {
        return this.path.getPath();
    }
}
