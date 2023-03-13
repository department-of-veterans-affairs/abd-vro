package gov.va.vro.mockslack.api;

import gov.va.vro.mockslack.model.SlackMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/")
public interface MockSlackApi {
  /** POST /slack-message : Handle slack message posts. */
  @Operation(
      operationId = "postSlackMessage",
      summary = "Handles slack messages.",
      description = "Receives slack messages payload and stores for queries.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The JWT",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description = "Internal error",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            })
      })
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/slack-messages",
      consumes = {MediaType.APPLICATION_JSON_VALUE},
      produces = {MediaType.TEXT_PLAIN_VALUE})
  ResponseEntity<String> postSlackMessage(@RequestBody SlackMessage slackMessage);

  /** GET /slack-messages/{collectionId} : Get rhe Slack messages received for the collection. */
  @Operation(
      operationId = "getSlackMessage",
      summary = "Retrieves the slack message received for the collection.",
      description = "Retrieves the slack message received for the collection.",
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The slack message",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "Not found",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            }),
        @ApiResponse(
            responseCode = "500",
            description = "Internal error",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            })
      })
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/slack-messages/{collectionId}",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  ResponseEntity<SlackMessage> getSlackMessage(@PathVariable("collectionId") Integer collectionId);

  /** DELETE /slack-messages/{collectionId} : Deletes the Slack message for the collection. */
  @Operation(
      operationId = "deleteSlackMessage",
      summary = "Deletes the slack message for the collection.",
      description ="Deletes the slack message for the collection.",
      responses = {
        @ApiResponse(
            responseCode = "204",
            description = "Success."),
        @ApiResponse(
            responseCode = "500",
            description = "Internal error",
            content = {
              @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = String.class))
            })
      })
  @RequestMapping(
      method = RequestMethod.DELETE,
      value = "/slack-messages/{collectionId}")
  ResponseEntity<Void> deleteSlackMessage(@PathVariable("collectionId") Integer collectionId);
}
