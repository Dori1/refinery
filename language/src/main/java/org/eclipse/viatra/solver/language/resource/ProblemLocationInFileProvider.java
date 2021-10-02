package org.eclipse.viatra.solver.language.resource;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.viatra.solver.language.ProblemUtil;
import org.eclipse.viatra.solver.language.model.problem.ImplicitVariable;
import org.eclipse.viatra.solver.language.model.problem.Node;
import org.eclipse.xtext.resource.DefaultLocationInFileProvider;
import org.eclipse.xtext.util.ITextRegion;

public class ProblemLocationInFileProvider extends DefaultLocationInFileProvider {
	@Override
	protected ITextRegion doGetTextRegion(EObject obj, RegionDescription query) {
		if (obj instanceof Node node) {
			return getNodeTextRegion(node, query);
		}
		if (obj instanceof ImplicitVariable) {
			return ITextRegion.EMPTY_REGION;
		}
		return super.doGetTextRegion(obj, query);
	}

	protected ITextRegion getNodeTextRegion(Node node, RegionDescription query) {
		if (ProblemUtil.isUniqueNode(node)) {
			return super.doGetTextRegion(node, query);
		}
		if (ProblemUtil.isNewNode(node)) {
			EObject container = node.eContainer();
			return doGetTextRegion(container, query);
		}
		return ITextRegion.EMPTY_REGION;
	}
}
