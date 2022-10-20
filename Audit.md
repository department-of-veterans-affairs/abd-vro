# Auditing Events in VRO

## Approach 1: AOP

AOP operates at the method level and injects advice before or after 
a method is called, as well as when an exception occurs.

To enable event logging on a method, the method has to be annotated thus:

```java
@Audited(
      eventType = EventProcessingType.AUTOMATED_CLAIM,
      payloadClass = MasAutomatedClaimPayload.class,
      idProperty = "collectionId")
  public void process(MasAutomatedClaimPayload payload) {
```
Each annotated method must provide some parameters to characterize the event:

- Event Type: What category of business logic caused this event
- Payload class: The object that is being audited
- id Property: The property in the Payload Class that identifies the object and consequently the event.

Advice is added to all methods with this annotation on the pointcut definition:

```java
public class AuditEventAspect {
    

  @Around(value = "@annotation(gov.va.vro.service.event.Audited)")
  public Object logAuditEvent(ProceedingJoinPoint joinPoint) {
        // extract event information
        // log event
        }
```

The above class is responsible for extracting the event information and transmitting an event.


There are some problems with this approach:

### Issue 1 
It is somewhat brittle. If the name of the property identifying the event changes, or the annotation is moved, the code will stop working. 
### Issue 2
@Audited objects must be Spring beans. This will not work for beans that are not managed by Spring. 
### Issue 3
This problem is more significant: 
Because we are using camel we often call rabbitmq endpoints without the interventions of any java beans. 
This means that there are no bean to add advice to. 
Consider for example the following route definition:

```java
from("entrypoint")
        .routeId("some route")
        .to("rabbitmq:queue1")
        .to("rabbitmq:queue2");
```
The above route calls two services via queues. 
We want to record these calls, but there are no Java Beans involved that we can attach advice to.
In order to make this work, we would have to add some beans before every route.

## Approach 2: Explicit Route Definition

Based on the considerations described above, 
it appears that event generation should not occur at the method level but rather at the endpoint level.
In this approach we interleave explicit event generation before and after calling routes:

```java
    from(ENDPOINT_MAS)
        .routeId("mas-claim-processing")
        .unmarshal(new JacksonDataFormat(MasAutomatedClaimPayload.class))
        .process(
            auditProcessor.eventProcessor(EventType.ENTERING, EventProcessingType.AUTOMATED_CLAIM))
        .process(masPollingProcessor)
        .process(
            auditProcessor.eventProcessor(EventType.EXITING, EventProcessingType.AUTOMATED_CLAIM))
        .setExchangePattern(ExchangePattern.InOnly)
        .log("MAS response: ${body}");
```

The auditProcessor bean injected above is responsible for providing a processor that will generate and transmit the event.

We still need a mechanism to extract the event identifier from the payloads.
Instead of relying on reflection, we require that auditable payloads implement an Auditable interface like this:

```java
public interface AuditableObject {

  String getEventId();
}

```

This approach adresses all the issues with the first approach: 
It is robust under refactoring, it does not require Spring Beans,
and it addresses event at the endpoint level rather than the method level.

There are still a couple of minor issues:

### Issue 1
Event processing must be injected explicitly, thus making the route definitions a bit more complicated.
It does however give us the ability to log events exactly where they are needed.

### Issue 2
It is a bit harder to extract the event Id when an exception occurs
(working on a solution).