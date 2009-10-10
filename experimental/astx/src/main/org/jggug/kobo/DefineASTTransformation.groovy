package org.jggug.kobo

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
public class DefineASTTransformation extends ClassCodeExpressionTransformer implements ASTTransformation {
  private SourceUnit sourceUnit;
  SourceUnit getSourceUnit() {
    return sourceUnit;
  }

  String symbol;
  String value;

  void visit(ASTNode[] nodes, SourceUnit source) {
    sourceUnit = source;
    symbol = nodes[0].getMember("symbol").text
    value  = nodes[0].getMember("value").text
    def parent = nodes[1]
    super.visitClass(parent)
  }

  Expression transform(Expression exp) {
    if (exp == null) return null;
    if (exp.class == VariableExpression.class) {
      if (exp.name == symbol) {
        return new VariableExpression(value)
      }
    }
    if (exp.class == ConstantExpression.class) {
      if (exp.value == symbol) {
        return new ConstantExpression(value)
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
