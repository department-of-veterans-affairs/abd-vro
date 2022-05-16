package gov.va.starter.example.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@AllArgsConstructor
// @RequiredArgsConstructor
@Getter
@Schema(name = "PagedResponse", description = "Bundled list of resources with paging metadata")
public class PagedResponse<T> {

  @NonNull
  @Schema(description = "List of found resources")
  private final List<T> items;

  @NonNull
  @Schema(description = "Total number of pages", example = "100")
  private final Integer totalPages;

  @NonNull
  @Schema(description = "Total number of items", example = "1000")
  private final Long totalItems;

  @NonNull
  @Schema(description = "Current page number", example = "1")
  private final Integer pageNumber;

  @NonNull
  @Schema(description = "Current page size", example = "10")
  private final Integer pageSize;
}
