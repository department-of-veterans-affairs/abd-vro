package gov.va.vro.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.Predefined;
import org.junit.jupiter.api.Test;

public class NamingConventionTest {
  private final JavaClasses classes =
      new ClassFileImporter()
          .withImportOption(Predefined.DO_NOT_INCLUDE_TESTS)
          .importPackages("gov.va");

  @Test
  public void controllerShouldBeSuffixed() {
    classes()
        .that()
        .resideInAPackage("..controller.")
        .should()
        .haveSimpleNameEndingWith("Controller")
        .orShould()
        .haveSimpleNameEndingWith("Mapper")
        .check(classes);
  }

  @Test
  public void controllerMapperShouldBeSuffixed() {
    classes()
        .that()
        .resideInAPackage("..controller..mapper")
        .should()
        .haveSimpleNameEndingWith("Mapper")
        .orShould()
        .haveSimpleNameEndingWith("MapperImpl")
        .check(classes);
  }

  @Test
  public void controllerClassesShouldBeInImplPackage() {
    classes()
        .that()
        .haveSimpleNameContaining("Controller")
        .should()
        .resideInAPackage("..controller..")
        .check(classes);
  }

  @Test
  public void repositoryShouldBeSuffixed() {
    classes()
        .that()
        .resideInAPackage("..persistence..")
        .should()
        .haveSimpleNameEndingWith("Repository")
        .orShould()
        .haveSimpleNameEndingWith("Entity")
        .orShould()
        .beEnums()
        .check(classes);
  }

  @Test
  public void repositoryClassesShouldBeInRepositoryPackage() {
    classes()
        .that()
        .haveSimpleNameEndingWith("Repository")
        .or()
        .haveSimpleNameEndingWith("Entity")
        .should()
        .resideInAPackage("..persistence..")
        .check(classes);
  }

  @Test
  public void serviceShouldBeSuffixed() {
    classes()
        .that()
        .resideInAPackage("..service.spi..")
        .and()
        .resideOutsideOfPackage("..service.spi..model")
        .should()
        .haveSimpleNameEndingWith("Service")
        .check(classes);
  }

  @Test
  public void serviceProviderMapperShouldBeSuffixed() {
    classes()
        .that()
        .resideInAPackage("..service.provider.mapper.")
        .should()
        .haveSimpleNameEndingWith("Mapper")
        .check(classes);
  }

  @Test
  public void serviceClassesShouldBeInServicePackage() {
    classes()
        .that()
        .haveSimpleNameContaining("Service")
        .and()
        .doNotHaveFullyQualifiedName("gov.va.vro.model.ServiceLocation")
        .should()
        .resideInAPackage("..service..")
        .check(classes);
  }

  @Test
  public void requestOrResponseObjectsShouldBeInApiPackage() {
    classes()
        .that()
        .haveSimpleNameEndingWith("Request")
        .or()
        .haveSimpleNameEndingWith("Response")
        .should()
        .resideInAnyPackage("..api..", "..model..")
        .check(classes);
  }

  @Test
  public void entityObjectsShouldBeInPersistencePackage() {
    classes()
        .that()
        .haveSimpleNameEndingWith("Entity")
        .should()
        .resideInAPackage("..persistence.model..")
        .check(classes);
  }

  @Test
  public void entityMapperObjectsShouldBeInPersistencePackage() {
    classes()
        .that()
        .haveSimpleNameContaining("EntityMapper")
        .should()
        .resideInAPackage("..service.provider..mapper..")
        .check(classes);
  }
}
