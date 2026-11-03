package io.github.enzogrn.mintbox.catalog.domain;

import static io.github.enzogrn.mintbox.catalog.domain.CatalogEntryFixtures.LINK_REFERENCE;
import static io.github.enzogrn.mintbox.catalog.domain.CatalogEntryFixtures.linkDetails;
import static io.github.enzogrn.mintbox.catalog.domain.CatalogEntryFixtures.linkDetailsNamed;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.enzogrn.mintbox.catalog.CatalogEntryAdded;
import io.github.enzogrn.mintbox.catalog.CatalogEntryUpdated;
import io.github.enzogrn.mintbox.shared.CatalogEntryId;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CatalogEntryTest {

    @Nested
    class Registration {

        @Test
        void creates_an_entry_with_a_new_identifier() {
            CatalogEntry first = CatalogEntry.register(ProductLine.AMIIBO, LINK_REFERENCE, linkDetails());
            CatalogEntry second = CatalogEntry.register(ProductLine.AMIIBO, LINK_REFERENCE, linkDetails());

            assertThat(first.id()).isNotEqualTo(second.id());
            assertThat(first.productLine()).isEqualTo(ProductLine.AMIIBO);
            assertThat(first.sourceReference()).isEqualTo(LINK_REFERENCE);
            assertThat(first.details()).isEqualTo(linkDetails());
        }

        @Test
        void records_that_the_entry_was_added() {
            CatalogEntry entry = CatalogEntry.register(ProductLine.AMIIBO, LINK_REFERENCE, linkDetails());

            assertThat(entry.domainEvents()).containsExactly(new CatalogEntryAdded(entry.id()));
        }

        @Test
        void rejects_attributes_the_product_line_does_not_declare() {
            CatalogEntryDetails link = linkDetails();
            CatalogEntryDetails withUnknownAttribute = new CatalogEntryDetails(
                    link.name(), link.character(), link.series(), link.franchise(),
                    link.figureType(), link.releaseDates(), link.imageUrl(),
                    new LineAttributes(Map.of("element", "fire")));

            assertThatThrownBy(() ->
                    CatalogEntry.register(ProductLine.AMIIBO, LINK_REFERENCE, withUnknownAttribute))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("element");
        }
    }

    @Nested
    class Restoration {

        @Test
        void rebuilds_an_entry_without_recording_any_event() {
            CatalogEntryId id = CatalogEntryId.generate();

            CatalogEntry entry = CatalogEntry.restore(id, ProductLine.AMIIBO, LINK_REFERENCE, linkDetails());

            assertThat(entry.id()).isEqualTo(id);
            assertThat(entry.domainEvents()).isEmpty();
        }
    }

    @Nested
    class DetailsUpdate {

        @Test
        void applies_changed_details_and_records_an_update() {
            CatalogEntry entry = restoredLink();

            boolean changed = entry.updateDetails(linkDetailsNamed("Link (Breath of the Wild)"));

            assertThat(changed).isTrue();
            assertThat(entry.details().name()).isEqualTo("Link (Breath of the Wild)");
            assertThat(entry.domainEvents()).containsExactly(new CatalogEntryUpdated(entry.id()));
        }

        @Test
        void ignores_identical_details_so_that_synchronizations_stay_quiet() {
            CatalogEntry entry = restoredLink();

            boolean changed = entry.updateDetails(linkDetails());

            assertThat(changed).isFalse();
            assertThat(entry.domainEvents()).isEmpty();
        }

        @Test
        void validates_the_new_details_against_the_product_line() {
            CatalogEntry entry = restoredLink();
            CatalogEntryDetails link = linkDetails();
            CatalogEntryDetails withUnknownAttribute = new CatalogEntryDetails(
                    link.name(), link.character(), link.series(), link.franchise(),
                    link.figureType(), link.releaseDates(), link.imageUrl(),
                    new LineAttributes(Map.of("element", "fire")));

            assertThatThrownBy(() -> entry.updateDetails(withUnknownAttribute))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThat(entry.details()).isEqualTo(linkDetails());
        }
    }

    @Nested
    class Events {

        @Test
        void can_be_cleared_once_published() {
            CatalogEntry entry = CatalogEntry.register(ProductLine.AMIIBO, LINK_REFERENCE, linkDetails());

            entry.clearDomainEvents();

            assertThat(entry.domainEvents()).isEmpty();
        }

        @Test
        void cannot_be_modified_from_outside() {
            CatalogEntry entry = CatalogEntry.register(ProductLine.AMIIBO, LINK_REFERENCE, linkDetails());

            assertThatThrownBy(() -> entry.domainEvents().clear())
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    class Identity {

        @Test
        void two_entries_with_the_same_identifier_are_equal_whatever_their_details() {
            CatalogEntryId id = CatalogEntryId.generate();
            CatalogEntry entry = CatalogEntry.restore(id, ProductLine.AMIIBO, LINK_REFERENCE, linkDetails());
            CatalogEntry sameEntryRenamed = CatalogEntry.restore(
                    id, ProductLine.AMIIBO, LINK_REFERENCE, linkDetailsNamed("Another name"));

            assertThat(entry).isEqualTo(sameEntryRenamed).hasSameHashCodeAs(sameEntryRenamed);
        }

        @Test
        void two_entries_with_different_identifiers_are_different_even_with_the_same_details() {
            assertThat(restoredLink()).isNotEqualTo(restoredLink());
        }
    }

    private static CatalogEntry restoredLink() {
        return CatalogEntry.restore(CatalogEntryId.generate(), ProductLine.AMIIBO, LINK_REFERENCE, linkDetails());
    }
}
