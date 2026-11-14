package org.springframework.samples.petclinic.owner.ui;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.samples.petclinic.owner.domain.PetTypeCount;
import org.springframework.samples.petclinic.owner.domain.PetTypeRepository;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the {@link StatisticsController} (CA3-CA9).
 */
@WebMvcTest(StatisticsController.class)
@DisabledInNativeImage
@DisabledInAotMode
class StatisticsControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PetTypeRepository types;

	/** Regex for one table row: name, count and percentage. */
	private static String tableRow(String name, long count, String percent) {
		return "<td>" + name + "</td>\\s*<td>" + count + "</td>\\s*<td><span>" + percent + "</span>%</td>";
	}

	/** Regex for one legend entry: name, count and percentage. */
	private static String legendEntry(String name, long count, String percent) {
		return "<span>" + name + "</span>:\\s*<span>" + count + "</span>\\s*\\(<span>" + percent + "</span>%\\)";
	}

	@Test
	void percentagesAreRoundedAndMatchInLegendAndTable() throws Exception {
		given(this.types.countPetsByType()).willReturn(
				List.of(new PetTypeCount("bird", 2), new PetTypeCount("cat", 4), new PetTypeCount("hamster", 7)));

		this.mockMvc.perform(get("/statistics").locale(Locale.ENGLISH))
			.andExpect(status().isOk())
			.andExpect(view().name("statistics/petsByType"))
			.andExpect(model().attribute("total", 13L))
			.andExpect(content().string(matchesRegex("(?s).*" + legendEntry("bird", 2, "15.4") + ".*"
					+ legendEntry("cat", 4, "30.8") + ".*" + legendEntry("hamster", 7, "53.8") + ".*")))
			.andExpect(content().string(matchesRegex("(?s).*" + tableRow("bird", 2, "15.4") + ".*"
					+ tableRow("cat", 4, "30.8") + ".*" + tableRow("hamster", 7, "53.8") + ".*")))
			.andExpect(content().string(containsString("<th><span>100.0</span>%</th>")));
	}

	@Test
	void halfIsRoundedUp() throws Exception {
		given(this.types.countPetsByType())
			.willReturn(List.of(new PetTypeCount("cat", 1), new PetTypeCount("dog", 15)));

		// 1/16 = 6.25 -> 6.3 (HALF_UP; HALF_EVEN would give 6.2)
		this.mockMvc.perform(get("/statistics").locale(Locale.ENGLISH))
			.andExpect(status().isOk())
			.andExpect(content().string(matchesRegex("(?s).*" + tableRow("cat", 1, "6.3") + ".*")))
			.andExpect(content().string(matchesRegex("(?s).*" + tableRow("dog", 15, "93.8") + ".*")));
	}

	@Test
	void zeroPetsShowsEveryTypeWithZeroWithoutDividingByZero() throws Exception {
		given(this.types.countPetsByType())
			.willReturn(List.of(new PetTypeCount("axolotl", 0), new PetTypeCount("cat", 0)));

		this.mockMvc.perform(get("/statistics").locale(Locale.ENGLISH))
			.andExpect(status().isOk())
			.andExpect(model().attribute("total", 0L))
			.andExpect(content().string(matchesRegex(
					"(?s).*" + legendEntry("axolotl", 0, "0.0") + ".*" + legendEntry("cat", 0, "0.0") + ".*")))
			.andExpect(content().string(
					matchesRegex("(?s).*" + tableRow("axolotl", 0, "0.0") + ".*" + tableRow("cat", 0, "0.0") + ".*")))
			.andExpect(content().string(containsString("<th><span>0.0</span>%</th>")))
			.andExpect(content().string(not(containsString("NaN"))));
	}

	@Test
	void keepsRepositoryOrderInLegendAndTable() throws Exception {
		given(this.types.countPetsByType())
			.willReturn(List.of(new PetTypeCount("bird", 2), new PetTypeCount("cat", 4)));

		this.mockMvc.perform(get("/statistics").locale(Locale.ENGLISH))
			.andExpect(content().string(matchesRegex(
					"(?s).*" + legendEntry("bird", 2, "33.3") + ".*" + legendEntry("cat", 4, "66.7") + ".*")))
			.andExpect(content().string(
					matchesRegex("(?s).*" + tableRow("bird", 2, "33.3") + ".*" + tableRow("cat", 4, "66.7") + ".*")));
	}

	@Test
	void chartIsAccessibleAndScalesToTheScreen() throws Exception {
		given(this.types.countPetsByType())
			.willReturn(List.of(new PetTypeCount("bird", 2), new PetTypeCount("cat", 4)));

		this.mockMvc.perform(get("/statistics").locale(Locale.ENGLISH))
			.andExpect(status().isOk())
			.andExpect(
					content().string(matchesRegex("(?s).*<svg[^>]*role=\"img\"[^>]*>\\s*<title[^>]*>[^<]+</title>.*")))
			.andExpect(content().string(matchesRegex("(?s).*<svg[^>]*viewBox=\"0 0 400 60\"[^>]*>.*")))
			.andExpect(content().string(not(matchesRegex("(?s).*<svg[^>]*\\s(width|height)=[^>]*>.*"))))
			.andExpect(content().string(containsString("<div class=\"table-responsive\">")));
	}

	@Test
	void smallNonEmptyTypeKeepsAVisibleBar() throws Exception {
		given(this.types.countPetsByType())
			.willReturn(List.of(new PetTypeCount("cat", 1000), new PetTypeCount("dog", 1), new PetTypeCount("emu", 0)));

		this.mockMvc.perform(get("/statistics").locale(Locale.ENGLISH))
			.andExpect(content().string(matchesRegex("(?s).*width=\"240\".*width=\"1\".*width=\"0\".*")));
	}

}
