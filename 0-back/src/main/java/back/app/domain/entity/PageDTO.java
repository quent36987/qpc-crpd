package back.app.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(requiredProperties = {"totalElements", "page", "size", "content"})
public class PageDTO<T> {
    private long totalElements;
    private int page;
    private int size;
    private List<T> content;
}

