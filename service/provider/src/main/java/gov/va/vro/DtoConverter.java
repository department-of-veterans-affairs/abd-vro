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
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

// Data Transfer Object Converter
// needed for sending POJOs through activemq, rabbitmq, etc.
@RequiredArgsConstructor
public class DtoConverter extends TypeConverterSupport {

  public final Collection<Class> dtoClasses;

  public static TypeConverterRegistry registerWith(
      CamelContext camelContext, Collection<Class> dtoClasses) {
    DtoConverter dtoConverter = new DtoConverter(dtoClasses);

    TypeConverterRegistry registry = camelContext.getTypeConverterRegistry();
    // registry.setTypeConverterExists(TypeConverterExists.Override);
    dtoClasses.forEach(
        clazz -> {
          registry.addTypeConverter(clazz, byte[].class, dtoConverter);
          registry.addTypeConverter(byte[].class, clazz, dtoConverter);

          registry.addTypeConverter(clazz, InputStream.class, dtoConverter);
          //          registry.addTypeConverter(InputStream.class, clazz, dtoConverter);
        });
    return registry;
  }

  @Override
  public <T> T convertTo(Class<T> targetClass, Exchange exchange, Object value)
      throws TypeConversionException {
    try {
      //            System.err.println("targetClass: " + targetClass + " value: " +
      // value.getClass());
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

  @Autowired ObjectMapper mapper;

  public byte[] toByteArray(Object obj) throws JsonProcessingException {
    ObjectWriter writer = mapper.writer();
    System.err.println("convert toByteArray: " + writer.writeValueAsString(obj));
    return writer.writeValueAsBytes(obj);
  }

  public <T> T toPojo(Class<T> targetClass, byte[] bytes) throws IOException {
    ObjectReader reader = mapper.reader();
    System.err.println("convert toPojo: " + targetClass);
    return reader.readValue(new String(bytes), targetClass);
  }

  public InputStream toInputStream(Object obj) throws JsonProcessingException {
    ObjectWriter writer = mapper.writer();
    System.err.println("convert toInputStream: " + writer.writeValueAsString(obj));
    byte[] bytes = writer.writeValueAsBytes(obj);
    return new ByteArrayInputStream(bytes);
  }
}
