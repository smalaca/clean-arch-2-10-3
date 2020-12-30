package com.smalaca.rentalapplication.infrastructure.persistence.jpa.apartment;

import com.google.common.collect.ImmutableMap;
import com.smalaca.rentalapplication.domain.apartment.Apartment;
import com.smalaca.rentalapplication.domain.apartment.ApartmentAssertion;
import com.smalaca.rentalapplication.domain.apartment.ApartmentFactory;
import com.smalaca.rentalapplication.domain.apartment.ApartmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class JpaApartmentRepositoryIntegrationTest {
    private static final String OWNER_ID = "1234";
    private static final String STREET = "Florianska";
    private static final String POSTAL_CODE = "12-345";
    private static final String HOUSE_NUMBER = "1";
    private static final String APARTMENT_NUMBER = "13";
    private static final String CITY = "Cracow";
    private static final String COUNTRY = "Poland";
    private static final String DESCRIPTION = "Nice place to stay";
    private static final Map<String, Double> ROOMS_DEFINITION = ImmutableMap.of("Toilet", 10.0, "Bedroom", 30.0);

    @Autowired
    private ApartmentRepository apartmentRepository;

    private final ApartmentFactory apartmentFactory = new ApartmentFactory();

    @Test
    void shouldThrowExceptionWhenApartmentDoesNotExist() {
        String nonExistingApartmentId = UUID.randomUUID().toString();

        ApartmentDoesNotExistException actual = assertThrows(ApartmentDoesNotExistException.class, () -> {
            apartmentRepository.findById(nonExistingApartmentId);
        });

        assertThat(actual).hasMessage("Apartment with id " + nonExistingApartmentId + " does not exist");
    }

    @Test
    @Transactional
    void shouldReturnExistingApartment() {
        Apartment apartment = createApartment();
        String existingId = apartmentRepository.save(apartment);

        Apartment actual = apartmentRepository.findById(existingId);

        ApartmentAssertion.assertThat(actual)
                .hasOwnerIdEqualsTo(OWNER_ID)
                .hasDescriptionEqualsTo(DESCRIPTION)
                .hasAddressEqualsTo(STREET, POSTAL_CODE, HOUSE_NUMBER, APARTMENT_NUMBER, CITY, COUNTRY)
                .hasRoomsEqualsTo(ROOMS_DEFINITION);
    }

    private Apartment createApartment() {
        return apartmentFactory.create(
                OWNER_ID, STREET, POSTAL_CODE, HOUSE_NUMBER, APARTMENT_NUMBER, CITY, COUNTRY,
                DESCRIPTION, ROOMS_DEFINITION);
    }
}