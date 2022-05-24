package gov.va.vro;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.TypeConversionException;
import org.apache.camel.spi.TypeConverterRegistry;
import org.apache.camel.support.TypeConverterSupport;

import java.io.IOException;
import java.util.Set;

// Data Transfer Object Converter
// needed for sending POJOs through activemq, rabbitmq, etc.
@RequiredArgsConstructor
public class DtoConverter extends TypeConverterSupport {

  public final Set<Class> dtoClasses;

  public TypeConverterRegistry registerWith(CamelContext camelContext) {
    TypeConverterRegistry registry = camelContext.getTypeConverterRegistry();
    // registry.setTypeConverterExists(TypeConverterExists.Override);
    dtoClasses.forEach(
        clazz -> {
          registry.addTypeConverter(clazz, byte[].class, this);
          registry.addTypeConverter(byte[].class, clazz, this);
        });
    return registry;
  }

  @Override
  public <T> T convertTo(Class<T> targetClass, Exchange exchange, Object value)
      throws TypeConversionException {
    try {
      //            System.err.println("targetClass: " + targetClass + " value: " +
      // value.getClass());
      if (dtoClasses.contains(value.getClass()) && targetClass == byte[].class) {
        return (T) toByteArray(value);
      } else if (value.getClass() == byte[].class) {
        return toPojo(targetClass, (byte[]) value);
      }
    } catch (IOException e) {
      throw new TypeConversionException(value, targetClass, e);
    }
    return null;
  }

  static final ObjectMapper mapper = new ObjectMapper();
  static final ObjectWriter writer = mapper.writer();
  static final ObjectReader reader = mapper.reader();

  public static byte[] toByteArray(Object obj) throws JsonProcessingException {
    System.err.println("convert toByteArray: " + writer.writeValueAsString(obj));
    return writer.writeValueAsBytes(obj);
  }

  public static <T> T toPojo(Class<T> targetClass, byte[] bytes) throws IOException {
    System.err.println("convert toPojo: " + targetClass);
    return reader.readValue(new String(bytes), targetClass);
  }
}
