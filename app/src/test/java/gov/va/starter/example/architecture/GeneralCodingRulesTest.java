package gov.va.starter.example.architecture;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackage;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.Predefined;
import com.tngtech.archunit.library.GeneralCodingRules;
import org.junit.jupiter.api.Test;

public class GeneralCodingRulesTest {
  private final String packageName = "gov.va.starter.example";
  private final JavaClasses classes =
      new ClassFileImporter()
          .withImportOption(Predefined.DO_NOT_INCLUDE_TESTS)
          .importPackages(packageName);

  @Test
  public void noGenericExceptions() {
    GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS.check(classes);
  }

  @Test
  public void noJavaUtil() {
    GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING.check(classes);
  }

  @Test
  public void noAccessToStdStream() {
    GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.check(classes);
  }

  @Test
  public void noFieldInjection() {
    GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION.check(
        classes.that(resideOutsideOfPackage("gov.va.starter.example.config")));
  }

  @Test
  public void noJodaTime() {
    GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME.check(classes);
  }
}
