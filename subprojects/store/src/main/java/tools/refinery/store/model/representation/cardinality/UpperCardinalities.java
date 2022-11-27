package tools.refinery.store.model.representation.cardinality;

public final class UpperCardinalities {
	public static final UpperCardinality UNBOUNDED = UnboundedUpperCardinality.INSTANCE;

	public static final UpperCardinality ZERO;

	public static final UpperCardinality ONE;

	private static final FiniteUpperCardinality[] cache = new FiniteUpperCardinality[256];

	static {
		for (int i = 0; i < cache.length; i++) {
			cache[i] = new FiniteUpperCardinality(i);
		}
		ZERO = cache[0];
		ONE = cache[1];
	}

	private UpperCardinalities() {
		throw new IllegalStateException("This is a static utility class and should not be instantiated directly");
	}

	public static UpperCardinality valueOf(int upperBound) {
		if (upperBound < 0) {
			return UNBOUNDED;
		}
		if (upperBound < cache.length) {
			return cache[upperBound];
		}
		return new FiniteUpperCardinality(upperBound);
	}
}