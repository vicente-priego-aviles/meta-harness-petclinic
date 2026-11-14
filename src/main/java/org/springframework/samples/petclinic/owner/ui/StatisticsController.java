package org.springframework.samples.petclinic.owner.ui;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.samples.petclinic.owner.domain.PetTypeCount;
import org.springframework.samples.petclinic.owner.domain.PetTypeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Shows how many pets there are of each type.
 */
@Controller
class StatisticsController {

	private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

	private static final long BAR_MAX_WIDTH = 240;

	private final PetTypeRepository types;

	StatisticsController(PetTypeRepository types) {
		this.types = types;
	}

	@GetMapping("/statistics")
	String petsByType(Model model) {
		List<PetTypeCount> counts = this.types.countPetsByType();
		long total = counts.stream().mapToLong(PetTypeCount::count).sum();
		long max = counts.stream().mapToLong(PetTypeCount::count).max().orElse(0);
		List<Row> rows = counts.stream()
			.map(c -> new Row(c.name(), c.count(), percentage(c.count(), total), barWidth(c.count(), max)))
			.toList();
		model.addAttribute("total", total);
		model.addAttribute("rows", rows);
		return "statistics/petsByType";
	}

	/**
	 * Bar length in chart units: proportional to the largest count, and at least 1 for a
	 * non-empty type so it stays distinguishable from a type with 0 pets.
	 */
	private static long barWidth(long count, long max) {
		if (count == 0 || max == 0) {
			return 0;
		}
		return Math.max(1, count * BAR_MAX_WIDTH / max);
	}

	private static BigDecimal percentage(long count, long total) {
		if (total == 0) {
			return BigDecimal.ZERO.setScale(1);
		}
		return BigDecimal.valueOf(count).multiply(HUNDRED).divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP);
	}

	/**
	 * One row of the statistics page.
	 */
	record Row(String name, long count, BigDecimal percent, long barWidth) {
	}

}
