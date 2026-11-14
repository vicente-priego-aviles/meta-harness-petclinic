package org.springframework.samples.petclinic.owner.ui;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.samples.petclinic.owner.domain.Owner;
import org.springframework.samples.petclinic.owner.domain.OwnerRepository;
import org.springframework.samples.petclinic.owner.domain.Pet;
import org.springframework.samples.petclinic.owner.domain.PetTypeRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * End to end test of the statistics page with the sample data (CA1, CA2, CA6).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StatisticsIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OwnerRepository owners;

	@Autowired
	private PetTypeRepository types;

	@Test
	void menuLinksToStatistics() throws Exception {
		this.mockMvc.perform(get("/").locale(Locale.ENGLISH))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("href=\"/statistics\"")))
			.andExpect(content().string(containsString("href=\"/owners/find\"")))
			.andExpect(content().string(containsString("href=\"/vets.html\"")))
			.andExpect(content().string(containsString("href=\"/oups\"")));
	}

	@Test
	void showsSampleData() throws Exception {
		this.mockMvc.perform(get("/statistics").locale(Locale.ENGLISH))
			.andExpect(status().isOk())
			.andExpect(view().name("statistics/petsByType"))
			.andExpect(model().attribute("total", 13L))
			.andExpect(content()
				.string(org.hamcrest.Matchers.matchesRegex("(?s).*nav-link active\"[^>]*href=\"/statistics\".*")))
			.andExpect(content().string(org.hamcrest.Matchers.matchesRegex("(?s).*" + String.join(".*", row("bird", 2),
					row("cat", 4), row("dog", 4), row("hamster", 1), row("lizard", 1), row("snake", 1)) + ".*")))
			.andExpect(content().string(containsString("<th>13</th>")));
	}

	private static String row(String name, int count) {
		return "<td>" + name + "</td>\\s*<td>" + count + "</td>";
	}

	@Test
	void reflectsNewPetWithoutRestart() throws Exception {
		this.mockMvc.perform(get("/statistics").locale(Locale.ENGLISH)).andExpect(model().attribute("total", 13L));

		Owner owner = this.owners.findById(1).orElseThrow();
		Pet pet = new Pet();
		pet.setName("Tom");
		pet.setBirthDate(java.time.LocalDate.of(2020, 1, 1));
		pet.setType(
				this.types.findPetTypes().stream().filter(t -> "cat".equals(t.getName())).findFirst().orElseThrow());
		owner.addPet(pet);
		this.owners.saveAndFlush(owner);

		this.mockMvc.perform(get("/statistics").locale(Locale.ENGLISH)).andExpect(model().attribute("total", 14L));
		assertThat(this.types.countPetsByType()).anyMatch(c -> c.name().equals("cat") && c.count() == 5L);
	}

}
