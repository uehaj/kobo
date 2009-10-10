package org.jggug.kobo

import org.codehaus.groovy.ast.*;
import org.codehaus.groovy.ast.expr.*;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.ast.builder.AstBuilder;
import org.objectweb.asm.Opcodes;

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class WithTimeoutASTTransformation extends ClassCodeVisitorSupport implements ASTTransformation {
  private SourceUnit sourceUnit;

  SourceUnit getSourceUnit() {
    return sourceUnit;
  }

  void visit(ASTNode[] nodes, SourceUnit source) {
    sourceUnit = source;

    def methodNode = nodes[1];
    def annotation = nodes[0];
    def timeout = annotation.getMember("value").value

    def originalCode = methodNode.code
    def originalParam = methodNode.parameters
    def targetMethodName = '_' + methodNode.name
    def paramString = originalParam.name.join(',')
    methodNode.setCode(new AstBuilder().buildFromString("""
import java.util.concurrent.*
import static java.util.concurrent.TimeUnit.*

Closure c = {$paramString ->$targetMethodName($paramString)}

${ originalParam.name.collect{"c = c.curry(${it})"}.join("\n")  }

def se = Executors.newSingleThreadExecutor()
def future = se.submit(c as Callable);
try {
  return future.get(Integer.parseInt(\"${timeout}\"), SECONDS)
}
finally {
  se.shutdown()
}
"""))
    
    List<ASTNode> result = new AstBuilder().buildFromSpec {
      method(targetMethodName, Opcodes.ACC_PUBLIC, methodNode.returnType.typeClass) {
        parameters {}
        exceptions {}
        block {}
        annotations {}
      }
    }
    result[0].setCode(originalCode)
    result[0].setParameters(originalParam)

    def mn = source.getAST()
    def cu = mn.getUnit();
    def cns = cu.getClasses();
    cns.each {
      if (it == methodNode.declaringClass) {
        it.addMethod(result[0])
      }
    }
  }

}
