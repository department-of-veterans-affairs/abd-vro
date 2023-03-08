package gov.va.vro.camel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "vro.camel")
public class CamelDtoClassesProperties {
  private List<String> dtoClasses;

  List<Class> getActualDtoClasses() throws IOException {
    log.info("Loading classes specified in vro.camel.dto-classes: {}", dtoClasses);
    return dtoClasses.stream()
        .map(
            classname -> {
              if (classname.endsWith(".*")) {
                String packageName = classname.substring(0, classname.length() - 2);
                List<Class> foundClasses = classesInPackage(packageName).stream().toList();
                log.debug("Classes in package {}: {}", packageName, foundClasses);
                return foundClasses;
              } else {
                try {
                  return Arrays.asList(Class.forName(classname));
                } catch (ClassNotFoundException e) {
                  log.error("Check the dto-classes in conf-camel.yml", e);
                  return null;
                }
              }
            })
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .collect(Collectors.toUnmodifiableList());
  }

  // Adapted from https://github.com/spring-projects/spring-boot/issues/4375#issuecomment-154489971
  private Set<Class> classesInPackage(String packageName) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(loader);
    try {
      Resource[] resources = scanForClassesUsingSpring(loader, packageName);
      return Arrays.stream(resources)
          .map(
              (Resource resource) -> {
                Class<?> clazz = loadClass(loader, metadataReaderFactory, resource);
                if (clazz != null && !clazz.getName().matches(".*\\$.*Builder")) {
                  return clazz;
                }
                return null;
              })
          .filter(Objects::nonNull)
          .collect(Collectors.toUnmodifiableSet());
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  private Resource[] scanForClassesUsingSpring(ClassLoader loader, String packageName)
      throws IOException {
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);
    String pattern =
        ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
            + ClassUtils.convertClassNameToResourcePath(packageName)
            + "/**/*.class";
    return resolver.getResources(pattern);
  }

  private Class<?> loadClass(
      ClassLoader loader, MetadataReaderFactory readerFactory, Resource resource) {
    try {
      MetadataReader reader = readerFactory.getMetadataReader(resource);
      return ClassUtils.forName(reader.getClassMetadata().getClassName(), loader);
    } catch (Exception ex) {
      log.error("Could not load class: " + resource, ex);
      return null;
    }
  }
}
