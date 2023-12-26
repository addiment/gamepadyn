//package computer.living.gamepadyn.autumn;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
//import javax.annotation.processing.AbstractProcessor;
//import javax.annotation.processing.Processor;
//import javax.annotation.processing.SupportedAnnotationTypes;
//import javax.annotation.processing.SupportedSourceVersion;
//import javax.lang.model.SourceVersion;
//
//@Retention(RetentionPolicy.RUNTIME)
//@Target(ElementType.METHOD)
//public @interface Test {
//}
//
//@SupportedAnnotationTypes("com.baeldung.annotation.processor.BuilderProperty")
//@SupportedSourceVersion(SourceVersion.RELEASE_8)
//@AutoService(Processor.class)
//public class AutumnFall extends AbstractProcessor {
//
//    @Override
//    public boolean process(Set<? extends TypeElement> annotations,
//                           RoundEnvironment roundEnv) {
//        return false;
//    }
//}