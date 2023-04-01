package gov.va.vro.camel;

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
 * https://camel.apache.org/manual/type-converter.html
 */
@Slf4j
public class CamelDtoConverter extends TypeConverterSupport {

  private final ObjectWriter writer;
  private final ObjectReader reader;

  /***
   * <p>Summary.</p>
   *
   * @param dtoClasses DTO Classes
   * @param mapper mapper
   */
  public CamelDtoConverter(Collection<Class> dtoClasses, ObjectMapper mapper) {
    this.dtoClasses = dtoClasses;
    this.writer = mapper.writer();
    this.reader = mapper.reader();
  }

  public final Collection<Class> dtoClasses;

  @Override
  @SuppressWarnings("unchecked")
  public <T> T convertTo(Class<T> targetClass, Exchange exchange, Object value)
      throws TypeConversionException {
    try {
      if (dtoClasses.contains(value.getClass())) {
        if (targetClass == byte[].class) {
          return (T) toByteArray(value);
        } else if (targetClass == InputStream.class) {
          return (T) toInputStream(value);
        }
      } else if (value.getClass() == byte[].class) {
        return toPojo(targetClass, (byte[]) value);
      }
    } catch (IOException e) {
      throw new TypeConversionException(value, targetClass, e);
    }
    return null;
  }

  public byte[] toByteArray(Object obj) throws JsonProcessingException {
    return writer.writeValueAsBytes(obj);
  }

  public <T> T toPojo(Class<T> targetClass, byte[] bytes) throws IOException {
    return reader.readValue(new String(bytes), targetClass);
  }

  public InputStream toInputStream(Object obj) throws JsonProcessingException {
    byte[] bytes = writer.writeValueAsBytes(obj);
    return new ByteArrayInputStream(bytes);
  }
}
