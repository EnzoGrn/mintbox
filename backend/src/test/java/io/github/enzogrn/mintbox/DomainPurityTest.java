package io.github.enzogrn.mintbox;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

/**
 * Checks that the domain model stays free of frameworks.
 *
 * <p>See {@code docs/adr/0009-framework-free-domain-model.md}.
 */
class DomainPurityTest {

    private static final String[] FRAMEWORK_PACKAGES = {
        "org.springframework..",
        "jakarta..",
        "org.hibernate..",
        "com.fasterxml..",
        "tools.jackson.."
    };

    private final JavaClasses productionClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("io.github.enzogrn.mintbox");

    @Test
    void domain_packages_do_not_depend_on_frameworks() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat().resideInAnyPackage(FRAMEWORK_PACKAGES)
                .because("the domain model must stay plain Java (ADR 0009)")
                .check(productionClasses);
    }

    @Test
    void shared_types_do_not_depend_on_frameworks() {
        noClasses()
                .that().resideInAPackage("io.github.enzogrn.mintbox.shared..")
                .should().dependOnClassesThat().resideInAnyPackage(FRAMEWORK_PACKAGES)
                .because("shared types are used by every domain model (ADR 0009)")
                .check(productionClasses);
    }
}
