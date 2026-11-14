package org.springframework.samples.petclinic.owner.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.aot.DisabledInAotMode;

/**
 * Tests the pet count per type query (CA3, CA9).
 */
@DataJpaTest
@DisabledInAotMode
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PetTypeRepositoryTests {

	@Autowired
	private PetTypeRepository types;

	@Test
	void countsPetsPerTypeOrderedByName() {
		List<PetTypeCount> counts = this.types.countPetsByType();

		assertThat(counts).extracting(PetTypeCount::name)
			.containsExactly("bird", "cat", "dog", "hamster", "lizard", "snake");
		assertThat(counts).extracting(PetTypeCount::count).containsExactly(2L, 4L, 4L, 1L, 1L, 1L);
	}

	@Test
	void includesTypesWithoutPetsWithZero() {
		PetType axolotl = new PetType();
		axolotl.setName("axolotl");
		this.types.saveAndFlush(axolotl);

		List<PetTypeCount> counts = this.types.countPetsByType();

		assertThat(counts).hasSize(7);
		assertThat(counts.get(0)).isEqualTo(new PetTypeCount("axolotl", 0L));
	}

}
