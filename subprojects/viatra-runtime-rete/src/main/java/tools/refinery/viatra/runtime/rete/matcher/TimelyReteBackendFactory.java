/*******************************************************************************
 * Copyright (c) 2010-2019, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package tools.refinery.viatra.runtime.rete.matcher;

import tools.refinery.viatra.runtime.matchers.backend.IQueryBackend;
import tools.refinery.viatra.runtime.matchers.context.IQueryBackendContext;
import tools.refinery.viatra.runtime.rete.matcher.TimelyConfiguration.AggregatorArchitecture;
import tools.refinery.viatra.runtime.rete.matcher.TimelyConfiguration.TimelineRepresentation;

/**
 * A {@link ReteBackendFactory} implementation that creates {@link ReteEngine}s that use non-scattered timely
 * evaluation.
 * 
 * @author Tamas Szabo
 * @since 2.4
 */
public class TimelyReteBackendFactory extends ReteBackendFactory {

    private final TimelyConfiguration configuration;
    
    public static final TimelyReteBackendFactory FIRST_ONLY_SEQUENTIAL = new TimelyReteBackendFactory(
            new TimelyConfiguration(TimelineRepresentation.FIRST_ONLY, AggregatorArchitecture.SEQUENTIAL));
    public static final TimelyReteBackendFactory FIRST_ONLY_PARALLEL = new TimelyReteBackendFactory(
            new TimelyConfiguration(TimelineRepresentation.FIRST_ONLY, AggregatorArchitecture.PARALLEL));
    public static final TimelyReteBackendFactory FAITHFUL_SEQUENTIAL = new TimelyReteBackendFactory(
            new TimelyConfiguration(TimelineRepresentation.FAITHFUL, AggregatorArchitecture.SEQUENTIAL));
    public static final TimelyReteBackendFactory FAITHFUL_PARALLEL = new TimelyReteBackendFactory(
            new TimelyConfiguration(TimelineRepresentation.FAITHFUL, AggregatorArchitecture.PARALLEL));    
    
    public TimelyReteBackendFactory(final TimelyConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public IQueryBackend create(final IQueryBackendContext context) {
        return create(context, false, configuration);
    }

    @Override
    public int hashCode() {
        return TimelyReteBackendFactory.class.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TimelyReteBackendFactory)) {
            return false;
        }
        return true;
    }

}
