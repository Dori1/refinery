/*
 * generated by Xtext 2.25.0
 */
package tools.refinery.language.scoping;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;

import tools.refinery.language.model.ProblemUtil;
import tools.refinery.language.model.problem.ClassDeclaration;
import tools.refinery.language.model.problem.ExistentialQuantifier;
import tools.refinery.language.model.problem.PredicateDefinition;
import tools.refinery.language.model.problem.Problem;
import tools.refinery.language.model.problem.ProblemPackage;
import tools.refinery.language.model.problem.ReferenceDeclaration;
import tools.refinery.language.model.problem.Variable;
import tools.refinery.language.model.problem.VariableOrNodeArgument;

/**
 * This class contains custom scoping description.
 * 
 * See
 * https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
public class ProblemScopeProvider extends AbstractProblemScopeProvider {

	@Override
	public IScope getScope(EObject context, EReference reference) {
		var scope = super.getScope(context, reference);
		if (reference == ProblemPackage.Literals.NODE_ASSERTION_ARGUMENT__NODE
				|| reference == ProblemPackage.Literals.NODE_VALUE_ASSERTION__NODE) {
			return getNodesScope(context, scope);
		}
		if (reference == ProblemPackage.Literals.VARIABLE_OR_NODE_ARGUMENT__VARIABLE_OR_NODE) {
			return getVariableScope(context, scope);
		}
		if (reference == ProblemPackage.Literals.REFERENCE_DECLARATION__OPPOSITE) {
			return getOppositeScope(context, scope);
		}
		return scope;
	}

	protected IScope getNodesScope(EObject context, IScope delegateScope) {
		var problem = EcoreUtil2.getContainerOfType(context, Problem.class);
		if (problem == null) {
			return delegateScope;
		}
		return Scopes.scopeFor(problem.getNodes(), delegateScope);
	}

	protected IScope getVariableScope(EObject context, IScope delegateScope) {
		List<Variable> variables = new ArrayList<>();
		EObject currentContext = context;
		if (context instanceof VariableOrNodeArgument argument) {
			Variable singletonVariable = argument.getSingletonVariable();
			if (singletonVariable != null) {
				variables.add(singletonVariable);
			}
		}
		while (currentContext != null && !(currentContext instanceof PredicateDefinition)) {
			if (currentContext instanceof ExistentialQuantifier quantifier) {
				variables.addAll(quantifier.getImplicitVariables());
			}
			currentContext = currentContext.eContainer();
		}
		if (currentContext != null) {
			PredicateDefinition definition = (PredicateDefinition) currentContext;
			variables.addAll(definition.getParameters());
		}
		return Scopes.scopeFor(variables, getNodesScope(context, delegateScope));
	}

	protected IScope getOppositeScope(EObject context, IScope delegateScope) {
		var referenceDeclaration = EcoreUtil2.getContainerOfType(context, ReferenceDeclaration.class);
		if (referenceDeclaration == null) {
			return delegateScope;
		}
		var relation = referenceDeclaration.getReferenceType();
		if (!(relation instanceof ClassDeclaration)) {
			return delegateScope;
		}
		var classDeclaration = (ClassDeclaration) relation;
		var referenceDeclarations = ProblemUtil.getAllReferenceDeclarations(classDeclaration);
		return Scopes.scopeFor(referenceDeclarations, delegateScope);
	}
}
