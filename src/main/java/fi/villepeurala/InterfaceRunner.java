package fi.villepeurala;

import org.junit.Test;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class InterfaceRunner extends BlockJUnit4ClassRunner {
    public InterfaceRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected TestClass createTestClass(Class<?> testClass) {
        return new InterfaceEnabledTestClass(testClass);
    }

    private static class InterfaceEnabledTestClass extends TestClass {
        public InterfaceEnabledTestClass(Class<?> clazz) {
            super(clazz);
        }

        @Override
        protected void scanAnnotatedMembers(Map<Class<? extends Annotation>, List<FrameworkMethod>> methodsForAnnotations, Map<Class<? extends Annotation>, List<FrameworkField>> fieldsForAnnotations) {
            super.scanAnnotatedMembers(methodsForAnnotations, fieldsForAnnotations);
            for (Class<?> eachInterface : getInterfaces(getJavaClass())) {
                for (Method method : eachInterface.getMethods()) {
                    if (method.getAnnotationsByType(Test.class).length != 0) {
                        addToAnnotationLists(new FrameworkMethod(method), methodsForAnnotations);
                    }
                }
            }
        }

        protected static List<Class<?>> getInterfaces(Class<?> testClass) {
            Set<Class<?>> results = new HashSet<Class<?>>();
            Stack<Class<?>> currentInterfaces = new Stack<Class<?>>();
            for (Class<?> intf : testClass.getInterfaces()) {
                currentInterfaces.push(intf);
            }
            while (!currentInterfaces.empty()) {
                Class<?> current = currentInterfaces.pop();
                results.add(current);
                for (Class<?> intf : current.getInterfaces()) {
                    currentInterfaces.push(intf);
                }
            }
            return new ArrayList<Class<?>>(results);
        }
    }
}
