package org.eclipse.viatra.solver.data.map.tests.smoke.slow;

import java.util.Arrays;
import java.util.stream.Stream;

import org.eclipse.viatra.solver.data.map.tests.smoke.fast.CommitSmokeTest;
import org.eclipse.viatra.solver.data.map.tests.smoke.fast.MutableImmutableCompareSmokeTest;
import org.eclipse.viatra.solver.data.map.tests.smoke.fast.MutableSmokeTest;
import org.eclipse.viatra.solver.data.map.tests.smoke.fast.RestoreSmokeTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Disabled
public class SlowSmokeTest {

	private static int slowStepCount = 32 * 32 * 32 * 32;

	private static Stream<Arguments> changeStepCount(Stream<Arguments> arguments) {
		return arguments.map(x -> Arguments.of(updatedStepCount(x.get())));

	}

	private static Object[] updatedStepCount(Object[] arguments) {
		Object[] copy = Arrays.copyOf(arguments, arguments.length);
		copy[1] = slowStepCount;
		return copy;
	}

	@ParameterizedTest(name = "Mutable Smoke {index}/{0} Steps={1} Keys={2} Values={3} seed={4} evil-hash={5}")
	@MethodSource
	void smoke1(int test, int steps, int noKeys, int noValues, int seed, boolean evilHash) {
		(new MutableSmokeTest()).runSmokeTest(
				"SmokeS" + steps + "K" + noKeys + "V" + noValues + "s" + seed + "H" + (evilHash ? "Evil" : "Normal"),
				seed, steps, noKeys, noValues, evilHash);
	}

	private static Stream<Arguments> smoke1() {
		return changeStepCount(MutableSmokeTest.parametrizedSmoke());
	}

	@ParameterizedTest(name = "Immutable Smoke {index}/{0} Steps={1} Keys={2} Values={3} commit frequency={4} seed={5} evil-hash={6}")
	@MethodSource
	void smoke2(int tests, int steps, int noKeys, int noValues, int commitFrequency, int seed, boolean evilHash) {
		(new CommitSmokeTest()).runSmokeTest("SmokeCommitS" + steps + "K" + noKeys + "V" + noValues + "s" + seed, seed,
				steps, noKeys, noValues, commitFrequency, evilHash);
	}

	private static Stream<Arguments> smoke2() {
		return changeStepCount(CommitSmokeTest.parametrizedSmoke());
	}

	@ParameterizedTest(name = "Mutable-Immutable Compare Smoke {index}/{0} Steps={1} Keys={2} Values={3} commit frequency={4} seed={5} evil-hash={6}")
	@MethodSource
	void smoke3(int tests, int steps, int noKeys, int noValues, int commitFrequency, int seed, boolean evilHash) {
		(new MutableImmutableCompareSmokeTest()).runSmokeTest(
				"SmokeMutableImmutableCompareS" + steps + "K" + noKeys + "V" + noValues + "s" + seed, seed, steps,
				noKeys, noValues, commitFrequency, evilHash);
	}

	private static Stream<Arguments> smoke3() {
		return changeStepCount(MutableImmutableCompareSmokeTest.parametrizedSmoke());
	}

	@ParameterizedTest(name = "Mutable-Immutable Compare Smoke {index}/{0} Steps={1} Keys={2} Values={3} commit frequency={4} seed={5} evil-hash={6}")
	@MethodSource
	void smoke4(int tests, int steps, int noKeys, int noValues, int commitFrequency, int seed, boolean evilHash) {
		(new RestoreSmokeTest()).runSmokeTest(
				"SmokeMutableImmutableCompareS" + steps + "K" + noKeys + "V" + noValues + "s" + seed, seed, steps,
				noKeys, noValues, commitFrequency, evilHash);
	}

	private static Stream<Arguments> smoke4() {
		return changeStepCount(RestoreSmokeTest.parametrizedSmoke());
	}
}
