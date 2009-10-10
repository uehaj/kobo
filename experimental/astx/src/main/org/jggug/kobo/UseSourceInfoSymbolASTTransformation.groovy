package org.jggug.kobo

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class UseSourceInfoSymbolASTTransformation extends ClassCodeExpressionTransformer implements ASTTransformation {
  private SourceUnit sourceUnit;
  SourceUnit getSourceUnit() {
    return sourceUnit;
  }

  String visitingMethod = null
  String visitingClass = null

  Map symbols = [
  __FILE__   : {ASTNode node -> new ConstantExpression(sourceUnit.name)},
  __LINE__   : {ASTNode node -> new ConstantExpression(node.lineNumber)},
  __COLUMN__ : {ASTNode node -> new ConstantExpression(node.columnNumber)},
  __CLASS__  : {ASTNode node -> new ConstantExpression(visitingClass)},
  __METHOD__ : {ASTNode node -> new ConstantExpression(visitingMethod)}
  ]

  void visit(ASTNode[] nodes, SourceUnit source) {
    sourceUnit = source;
    def parent = nodes[1]
    visitingClass = parent.name
    super.visitClass(parent)
  }

  void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
    visitingMethod = node.name
    super.visitConstructorOrMethod(node, isConstructor)
  }


  Expression transform(Expression exp) {
    if (exp == null) return null;
    if (exp.class == VariableExpression.class) {
      if (symbols.containsKey(exp.name)) {
        return symbols[exp.name].call(exp)
      }
    }
    if (exp.class == ConstantExpression.class) {
      if (symbols.containsKey(exp.value)) {
        return symbols[exp.value].call(exp)
      }
    }
    if (exp.class == MethodCallExpression.class) {
      def args = transform(exp.getArguments());
      def method = transform(exp.getMethod());
      def object = transform(exp.getObjectExpression());
      def result = new MethodCallExpression(object, method, args);
      return result
    }
    if (exp.class == ClosureExpression.class) {
      Statement code = exp.getCode();
      if (code != null) code.visit(this);
      return exp;
    }
    if (exp.class == ConstructorCallExpression.class) {
      return exp.transformExpression(this);
    }
    return exp.transformExpression(this);
  }
}
