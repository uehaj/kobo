package org.jggug.kobo

import org.codehaus.groovy.transform.GroovyASTTransformationClass;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.SOURCE)
@Target([ElementType.METHOD])
@GroovyASTTransformationClass("org.jggug.kobo.WithTimeoutASTTransformation")
public @interface WithTimeout {
  int value() default -1
}
