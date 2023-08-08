/*
 * SPDX-FileCopyrightText: 2021-2023 The Refinery Authors <https://refinery.tools/>
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package tools.refinery.store.query.viatra.internal;

import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngineOptions;
import org.eclipse.viatra.query.runtime.localsearch.matcher.integration.LocalSearchHintOptions;
import org.eclipse.viatra.query.runtime.matchers.backend.IQueryBackendFactory;
import org.eclipse.viatra.query.runtime.matchers.backend.QueryEvaluationHint;
import org.eclipse.viatra.query.runtime.rete.matcher.ReteBackendFactory;
import tools.refinery.store.adapter.AbstractModelAdapterBuilder;
import tools.refinery.store.model.ModelStore;
import tools.refinery.store.model.ModelStoreBuilder;
import tools.refinery.store.query.dnf.AnyQuery;
import tools.refinery.store.query.dnf.Dnf;
import tools.refinery.store.query.viatra.ViatraModelQueryBuilder;
import tools.refinery.store.query.viatra.internal.localsearch.FlatCostFunction;
import tools.refinery.store.query.viatra.internal.localsearch.RelationalLocalSearchBackendFactory;
import tools.refinery.store.query.viatra.internal.matcher.RawPatternMatcher;
import tools.refinery.store.query.viatra.internal.pquery.Dnf2PQuery;

import java.util.*;
import java.util.function.Function;

public class ViatraModelQueryBuilderImpl extends AbstractModelAdapterBuilder<ViatraModelQueryStoreAdapterImpl>
		implements ViatraModelQueryBuilder {
	private ViatraQueryEngineOptions.Builder engineOptionsBuilder;
	private QueryEvaluationHint defaultHint = new QueryEvaluationHint(Map.of(
			// Use a cost function that ignores the initial (empty) model but allows higher arity input keys.
			LocalSearchHintOptions.PLANNER_COST_FUNCTION, new FlatCostFunction()
	), (IQueryBackendFactory) null);
	private final Dnf2PQuery dnf2PQuery = new Dnf2PQuery();
	private final Set<AnyQuery> vacuousQueries = new LinkedHashSet<>();
	private final Map<AnyQuery, IQuerySpecification<RawPatternMatcher>> querySpecifications = new LinkedHashMap<>();

	public ViatraModelQueryBuilderImpl() {
		engineOptionsBuilder = new ViatraQueryEngineOptions.Builder()
				.withDefaultBackend(ReteBackendFactory.INSTANCE)
				.withDefaultCachingBackend(ReteBackendFactory.INSTANCE)
				.withDefaultSearchBackend(RelationalLocalSearchBackendFactory.INSTANCE);
	}

	@Override
	public ViatraModelQueryBuilder engineOptions(ViatraQueryEngineOptions engineOptions) {
		checkNotConfigured();
		engineOptionsBuilder = new ViatraQueryEngineOptions.Builder(engineOptions);
		return this;
	}

	@Override
	public ViatraModelQueryBuilder defaultHint(QueryEvaluationHint queryEvaluationHint) {
		checkNotConfigured();
		defaultHint = defaultHint.overrideBy(queryEvaluationHint);
		return this;
	}

	@Override
	public ViatraModelQueryBuilder backend(IQueryBackendFactory queryBackendFactory) {
		checkNotConfigured();
		engineOptionsBuilder.withDefaultBackend(queryBackendFactory);
		return this;
	}

	@Override
	public ViatraModelQueryBuilder cachingBackend(IQueryBackendFactory queryBackendFactory) {
		checkNotConfigured();
		engineOptionsBuilder.withDefaultCachingBackend(queryBackendFactory);
		return this;
	}

	@Override
	public ViatraModelQueryBuilder searchBackend(IQueryBackendFactory queryBackendFactory) {
		checkNotConfigured();
		engineOptionsBuilder.withDefaultSearchBackend(queryBackendFactory);
		return this;
	}

	@Override
	public ViatraModelQueryBuilder query(AnyQuery query) {
		checkNotConfigured();
		if (querySpecifications.containsKey(query) || vacuousQueries.contains(query)) {
			// Ignore duplicate queries.
			return this;
		}
		var dnf = query.getDnf();
		var reduction = dnf.getReduction();
		switch (reduction) {
		case NOT_REDUCIBLE -> {
			var pQuery = dnf2PQuery.translate(dnf);
			querySpecifications.put(query, pQuery.build());
		}
		case ALWAYS_FALSE -> vacuousQueries.add(query);
		case ALWAYS_TRUE -> throw new IllegalArgumentException(
				"Query %s is relationally unsafe (it matches every tuple)".formatted(query.name()));
		default -> throw new IllegalArgumentException("Unknown reduction: " + reduction);
		}
		return this;
	}

	@Override
	public ViatraModelQueryBuilder query(AnyQuery query, QueryEvaluationHint queryEvaluationHint) {
		hint(query.getDnf(), queryEvaluationHint);
		query(query);
		return this;
	}

	@Override
	public ViatraModelQueryBuilder computeHint(Function<Dnf, QueryEvaluationHint> computeHint) {
		checkNotConfigured();
		dnf2PQuery.setComputeHint(computeHint);
		return this;
	}

	@Override
	public ViatraModelQueryBuilder hint(Dnf dnf, QueryEvaluationHint queryEvaluationHint) {
		checkNotConfigured();
		dnf2PQuery.hint(dnf, queryEvaluationHint);
		return this;
	}

	@Override
	public void doConfigure(ModelStoreBuilder storeBuilder) {
		dnf2PQuery.assertNoUnusedHints();
	}

	@Override
	public ViatraModelQueryStoreAdapterImpl doBuild(ModelStore store) {
		validateSymbols(store);
		return new ViatraModelQueryStoreAdapterImpl(store, buildEngineOptions(), dnf2PQuery.getSymbolViews(),
				Collections.unmodifiableMap(querySpecifications), Collections.unmodifiableSet(vacuousQueries));
	}

	private ViatraQueryEngineOptions buildEngineOptions() {
		// Workaround: manually override the default backend, because {@link ViatraQueryEngineOptions.Builder}
		// ignores all backend requirements except {@code SPECIFIC}.
		switch (defaultHint.getQueryBackendRequirementType()) {
		case SPECIFIC -> engineOptionsBuilder.withDefaultBackend(defaultHint.getQueryBackendFactory());
		case DEFAULT_CACHING -> engineOptionsBuilder.withDefaultBackend(
				engineOptionsBuilder.build().getDefaultCachingBackendFactory());
		case DEFAULT_SEARCH -> engineOptionsBuilder.withDefaultBackend(
				engineOptionsBuilder.build().getDefaultSearchBackendFactory());
		case UNSPECIFIED -> {
			// Nothing to do, leave the default backend unchanged.
		}
		}
		engineOptionsBuilder.withDefaultHint(defaultHint);
		return engineOptionsBuilder.build();
	}

	private void validateSymbols(ModelStore store) {
		var symbols = store.getSymbols();
		for (var symbolView : dnf2PQuery.getSymbolViews().keySet()) {
			var symbol = symbolView.getSymbol();
			if (!symbols.contains(symbol)) {
				throw new IllegalArgumentException("Cannot query view %s: symbol %s is not in the model"
						.formatted(symbolView, symbol));
			}
		}
	}
}