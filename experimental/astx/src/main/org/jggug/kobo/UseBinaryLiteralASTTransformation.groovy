package org.jggug.kobo

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class UseBinaryLiteralASTTransformation extends ClassCodeExpressionTransformer implements ASTTransformation {
  private SourceUnit sourceUnit;
  SourceUnit getSourceUnit() {
    return sourceUnit;
  }

  String visitingMethod = null
  String visitingClass = null

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

  ConstantExpression binaryLiteral(String s) {
    s = s.replaceAll('_', '')
    println s[2..-1]
    return new ConstantExpression(Integer.parseInt(s[2..-1], 2))
  }

  Expression transform(Expression exp) {
    if (exp == null) return null;
    if (exp.class == VariableExpression.class) {
      if (exp.name.startsWith("\$b")) {
        return binaryLiteral(exp.name)
      }
    }
    if (exp.class == ConstantExpression.class) {
      if (exp.value.startsWith("\$b")) {
        return binaryLiteral(exp.value)
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
