package io.github.enzogrn.mintbox.catalog.domain;

import static io.github.enzogrn.mintbox.catalog.domain.CatalogEntryFixtures.linkDetails;
import static io.github.enzogrn.mintbox.catalog.domain.CatalogEntryFixtures.linkDetailsNamed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.URI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CatalogEntryDetailsTest {

    @Test
    void removes_surrounding_spaces_from_texts() {
        assertThat(linkDetailsNamed("  Link  ").name()).isEqualTo("Link");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void rejects_a_blank_name(String name) {
        assertThatThrownBy(() -> linkDetailsNamed(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("name");
    }

    @Test
    void rejects_a_name_that_is_too_long() {
        String tooLong = "x".repeat(CatalogEntryDetails.MAX_TEXT_LENGTH + 1);

        assertThatThrownBy(() -> linkDetailsNamed(tooLong))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejects_a_missing_name() {
        assertThatThrownBy(() -> linkDetailsNamed(null))
                .isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "http://example.com/link.png",   // plain HTTP
        "/images/link.png",              // relative
        "ftp://example.com/link.png",    // another protocol
        "https:///link.png"              // no host
    })
    void rejects_an_image_url_that_is_not_an_absolute_https_url(String url) {
        assertThatThrownBy(() -> withImage(URI.create(url)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("imageUrl");
    }

    @Test
    void accepts_an_https_image_url() {
        URI url = URI.create("https://example.com/link.png");

        assertThat(withImage(url).imageUrl()).isEqualTo(url);
    }

    @Test
    void two_details_with_the_same_values_are_equal() {
        assertThat(linkDetails()).isEqualTo(linkDetails());
    }

    private static CatalogEntryDetails withImage(URI imageUrl) {
        CatalogEntryDetails link = linkDetails();
        return new CatalogEntryDetails(
                link.name(), link.character(), link.series(), link.franchise(),
                link.figureType(), link.releaseDates(), imageUrl, link.attributes());
    }
}
