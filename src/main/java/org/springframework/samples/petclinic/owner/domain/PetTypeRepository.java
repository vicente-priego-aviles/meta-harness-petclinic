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

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository class for <code>PetType</code> domain objects.
 *
 * @author Patrick Baumgartner
 */

public interface PetTypeRepository extends JpaRepository<PetType, Integer> {

	/**
	 * Retrieve all {@link PetType}s from the data store.
	 * @return a Collection of {@link PetType}s.
	 */
	@Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
	List<PetType> findPetTypes();

	/**
	 * Count the pets of every {@link PetType}, including types without pets.
	 * @return one {@link PetTypeCount} per type, ordered by type name.
	 */
	@Query("""
			SELECT new org.springframework.samples.petclinic.owner.domain.PetTypeCount(ptype.name, COUNT(pet))
			FROM PetType ptype LEFT JOIN Pet pet ON pet.type = ptype
			GROUP BY ptype.id, ptype.name
			ORDER BY ptype.name
			""")
	List<PetTypeCount> countPetsByType();

}
