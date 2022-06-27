package io.github.aliothliu.marble.domain.page;

import io.github.aliothliu.marble.MarbleRegistry;
import io.github.aliothliu.marble.domain.ValidationException;
import io.github.aliothliu.marble.domain.ValidationHandler;
import io.github.aliothliu.marble.domain.Validator;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PageElementsValidator extends Validator<Set<String>> {

    private final PageId pageId;

    public PageElementsValidator(ValidationHandler handler, PageId pageId) {
        super(handler);
        Assert.notNull(pageId, "页面ID不能为空");
        this.pageId = pageId;
    }

    @Override
    public void validate(Set<String> elements) {
        Set<String> localElements = new HashSet<>(elements);
        Optional<Page> pageOptional = MarbleRegistry.pageRepository().findById(this.pageId);
        if (!pageOptional.isPresent()) {
            this.notificationHandler().handleException(new ValidationException("未找到符合条件的页面信息"));
            return;
        }
        Page page = pageOptional.get();
        Set<String> stored = page.getElements().stream().map(Element::getName).collect(Collectors.toSet());
        localElements.removeAll(stored);

        if (!localElements.isEmpty()) {
            this.notificationHandler().handleException(new ElementNotExistsException(localElements));
        }
    }

    public static class ElementNotExistsException extends ValidationException {

        public ElementNotExistsException(Set<String> elements) {
            super("非法的页面元素 [" + String.join(",", elements) + "]");
        }
    }
}
