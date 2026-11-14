package org.springframework.samples.petclinic.owner.domain;

/**
 * Number of pets of one {@link PetType}.
 *
 * @param name the pet type name
 * @param count the number of pets of that type (0 if none)
 */
public record PetTypeCount(String name, long count) {
}
