/*******************************************************************************
 * Copyright (c) 2010-2016, Tamas Szabo, Istvan Rath and Daniel Varro
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-v20.html.
 * 
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package tools.refinery.viatra.runtime.matchers.util.resumable;

import java.util.Map;

import tools.refinery.viatra.runtime.matchers.tuple.Tuple;
import tools.refinery.viatra.runtime.matchers.util.timeline.Diff;

/**
 * A masked {@link Resumable} implementation, which maintains lazy folding per tuple signature. 
 * 
 * @author Tamas Szabo
 * @since 2.4
 */
public interface MaskedResumable<Timestamp extends Comparable<Timestamp>> extends Resumable<Timestamp> {

    /**
     * When called, the folding of the state shall be resumed at the given timestamp. The resumable is expected to
     * do a folding step at the given timestamp only. Afterwards, folding shall be interrupted, even if there is more
     * folding to do towards higher timestamps.
     */
    public Map<Tuple, Map<Tuple, Diff<Timestamp>>> resumeAt(final Timestamp timestamp);
    
    /**
     * Returns the set of signatures for which lazy folding shall be resumed at the next timestamp. 
     */
    public Iterable<Tuple> getResumableSignatures();

}
