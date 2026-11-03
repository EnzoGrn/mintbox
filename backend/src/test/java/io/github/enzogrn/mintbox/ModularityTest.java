package io.github.enzogrn.mintbox;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Checks the module boundaries described in {@code docs/domain/modules.md}.
 *
 * <p>The build fails if a module uses another module's internal types, or if two modules
 * depend on each other.
 */
class ModularityTest {

    private final ApplicationModules modules = ApplicationModules.of(MintboxApplication.class);

    @Test
    void module_boundaries_are_respected() {
        modules.verify();
    }

    /**
     * Generates diagrams and a description of each module in
     * {@code target/spring-modulith-docs}, from the actual code.
     */
    @Test
    void writes_module_documentation() {
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml()
                .writeModuleCanvases();
    }
}
