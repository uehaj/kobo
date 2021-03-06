package org.jggug.kobo

import org.codehaus.groovy.transform.GroovyASTTransformationClass;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.TYPE])
@GroovyASTTransformationClass("org.jggug.kobo.DefineASTTransformation")
public @interface Define {
  String symbol();
  String value();
}
