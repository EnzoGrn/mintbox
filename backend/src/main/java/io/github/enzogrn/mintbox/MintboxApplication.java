package io.github.enzogrn.mintbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

/**
 * Entry point of the Mintbox backend.
 *
 * <p>Each direct sub-package of this package is an application module (see
 * {@code docs/adr/0002-modular-monolith-with-spring-modulith.md}). The {@code shared} module
 * holds the few types every module needs, such as {@code Money}.
 */
@SpringBootApplication
@Modulithic(sharedModules = "shared")
public class MintboxApplication {

    public static void main(String[] args) {
        SpringApplication.run(MintboxApplication.class, args);
    }
}
