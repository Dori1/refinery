package tools.refinery.data.query.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.viatra.query.runtime.matchers.context.IQueryRuntimeContextListener;
import org.eclipse.viatra.query.runtime.matchers.tuple.ITuple;

import tools.refinery.data.model.Tuple;
import tools.refinery.data.query.view.RelationView;

public class RelationUpdateListener {
	private final Map<RelationView<?>,Set<RelationUpdateListenerEntry<?>>> view2Listeners;
	
	public RelationUpdateListener(Set<RelationView<?>> relationViews) {
		view2Listeners = new HashMap<>();
		for(RelationView<?> relationView : relationViews) {
			view2Listeners.put(relationView, new HashSet<>());
		}
	}
	public boolean containsRelationalView(RelationView<?> relationalKey) {
		RelationView<?> relationView = relationalKey.getWrappedKey();
		return view2Listeners.containsKey(relationView);
	}
	public void addListener(RelationView<?> relationalKey, ITuple seed, IQueryRuntimeContextListener listener) {
		RelationView<?> relationView = relationalKey.getWrappedKey();
		if(view2Listeners.containsKey(relationView)) {
			RelationUpdateListenerEntry<?> entry = new RelationUpdateListenerEntry<>(relationalKey, seed, listener);
			view2Listeners.get(relationView).add(entry);
		} else throw new IllegalArgumentException();
	}
	public void removeListener(RelationView<?> relationalKey, ITuple seed, IQueryRuntimeContextListener listener) {
		RelationView<?> relationView = relationalKey.getWrappedKey();
		if(view2Listeners.containsKey(relationView)) {
			RelationUpdateListenerEntry<?> entry = new RelationUpdateListenerEntry<>(relationalKey, seed, listener);
			view2Listeners.get(relationView).remove(entry);
		} else throw new IllegalArgumentException();
	}
	
	public <D> void processChange(RelationView<D> relationView, Tuple tuple, D oldValue, D newValue) {
		Set<RelationUpdateListenerEntry<?>> listeners = view2Listeners.get(relationView);
		if(listeners != null) {
			for(RelationUpdateListenerEntry<?> listener : listeners) {
				@SuppressWarnings("unchecked")
				RelationUpdateListenerEntry<D> typeCorrectListener = (RelationUpdateListenerEntry<D>) listener;
				typeCorrectListener.processChange(tuple, oldValue, newValue);
			}
		} else throw new IllegalArgumentException("View was not indexed in constructor "+relationView);
	}
}
