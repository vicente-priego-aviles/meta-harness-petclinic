/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.owner.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PetVisitsOrderTests {

	private static Visit visit(LocalDate date, String description) {
		Visit visit = new Visit();
		visit.setDate(date);
		visit.setDescription(description);
		return visit;
	}

	@Test
	void visitsNewestFirstOrdersByDateDescending() {
		Pet pet = new Pet();
		pet.addVisit(visit(LocalDate.of(2024, 1, 10), "old"));
		pet.addVisit(visit(LocalDate.of(2026, 5, 1), "newest"));
		pet.addVisit(visit(LocalDate.of(2025, 3, 3), "middle"));

		assertThat(pet.getVisitsNewestFirst()).extracting(Visit::getDescription)
			.containsExactly("newest", "middle", "old");
	}

	@Test
	void visitsNewestFirstPutsVisitsWithoutDateLast() {
		Pet pet = new Pet();
		pet.addVisit(visit(null, "undated"));
		pet.addVisit(visit(LocalDate.of(2025, 3, 3), "dated"));

		assertThat(pet.getVisitsNewestFirst()).extracting(Visit::getDescription).containsExactly("dated", "undated");
	}

	@Test
	void visitsNewestFirstIsEmptyWithoutVisits() {
		assertThat(new Pet().getVisitsNewestFirst()).isEmpty();
	}

}
