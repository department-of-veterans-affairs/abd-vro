package gov.va.vro.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@Schema(name = "PagedResponse", description = "Bundled list of resources with paging metadata")
public class PagedResponse<T> {

  @NotNull
  @Schema(description = "List of found resources")
  private final List<T> items;

  @NotNull
  @Schema(description = "Total number of pages", example = "100")
  private final Integer totalPages;

  @NotNull
  @Schema(description = "Total number of items", example = "1000")
  private final Long totalItems;

  @NotNull
  @Schema(description = "Current page number", example = "1")
  private final Integer pageNumber;

  @NotNull
  @Schema(description = "Current page size", example = "10")
  private final Integer pageSize;
}
