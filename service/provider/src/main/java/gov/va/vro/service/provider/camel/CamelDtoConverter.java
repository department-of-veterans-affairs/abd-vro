package gov.va.vro.service.provider.camel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.support.TypeConverterSupport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * Data Transfer Object (DTO) Converter for Camel. Needed for sending POJOs through rabbitmq, saving
 * to text files, etc. When registered, Camel uses this class to automatically convert message body
 * objects of a specified class into the target class, depending on the endpoint.
 */
@Slf4j
public class CamelDtoConverter extends TypeConverterSupport {

  public CamelDtoConverter(Collection<Class> dtoClasses, ObjectMapper mapper) {
    this.dtoClasses = dtoClasses;
    this.mapper = mapper;
  }

  public final Collection<Class> dtoClasses;

  @Override
  public <T> T convertTo(Class<T> targetClass, Exchange exchange, Object value)
      throws TypeConversionException {
    try {
      log.info("class: {}, targetClass: {}", value.getClass(), targetClass);
      if (dtoClasses.contains(value.getClass())) {
        if (targetClass == byte[].class) return (T) toByteArray(value);
        if (targetClass == InputStream.class) return (T) toInputStream(value);
      } else if (value.getClass() == byte[].class) {
        return toPojo(targetClass, (byte[]) value);
      }
    } catch (IOException e) {
      throw new TypeConversionException(value, targetClass, e);
    }
    return null;
  }

  // https://stackoverflow.com/questions/33397359/how-to-configure-jackson-objectmapper-for-camel-in-spring-boot
  private final ObjectMapper mapper;

  public byte[] toByteArray(Object obj) throws JsonProcessingException {
    ObjectWriter writer = mapper.writer();
    log.warn(writer.toString());
    // log.trace("convert toByteArray: {}", writer.writeValueAsString(obj));
    return writer.writeValueAsBytes(obj);
  }

  public <T> T toPojo(Class<T> targetClass, byte[] bytes) throws IOException {
    ObjectReader reader = mapper.reader();
    log.warn(reader.toString());
    return reader.readValue(new String(bytes), targetClass);
  }

  public InputStream toInputStream(Object obj) throws JsonProcessingException {
    ObjectWriter writer = mapper.writer();
    byte[] bytes = writer.writeValueAsBytes(obj);
    return new ByteArrayInputStream(bytes);
  }
}
