package com.lcyanxi.cache.parser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;

/**
 * @author lichang
 * @date 2020/9/11
 */
public class ExpressionEvaluator {

    public static final Object NO_RESULT = new Object();

    private final SpelExpressionParser parser = new SpelExpressionParser();

    // shared param discoverer since it caches data internally
    private final ParameterNameDiscoverer paramNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();

    private final Map<String, Expression> keyCache = new ConcurrentHashMap<String, Expression>(64);

    private final Map<String, Method> targetMethodCache = new ConcurrentHashMap<String, Method>(64);

    /**
     * Create an {@link EvaluationContext} without a return value.
     *
     * @see #createEvaluationContext(ApplicationContext, Method, Object[], Object,
     *      Class, Object)
     */
    public EvaluationContext createEvaluationContext(ApplicationContext applicationContext, Method method,
                                                     Object[] args, Object target, Class<?> targetClass) {
        return createEvaluationContext(applicationContext, method, args, target, targetClass, NO_RESULT);
    }

    /**
     * Create an {@link EvaluationContext}.
     *
     * @param applicationContext the current applicationContext
     * @param method the method
     * @param args the method arguments
     * @param target the target object
     * @param targetClass the target class
     * @param result the return value (can be {@code null}) or
     *        {@link #NO_RESULT} if there is no return at this time
     * @return the evalulation context
     */
    public EvaluationContext createEvaluationContext(ApplicationContext applicationContext, Method method,
                                                     Object[] args, Object target, Class<?> targetClass, final Object result) {
        LazyParamAwareEvaluationContext evaluationContext = new LazyParamAwareEvaluationContext(target,
                applicationContext, this.paramNameDiscoverer, method, args, targetClass, this.targetMethodCache);
        if (result != NO_RESULT) {
            evaluationContext.setVariable("result", result);
        }
        return evaluationContext;
    }

    public <T> T key(String keyExpression, Method method, EvaluationContext evalContext, Class<T> type) {
        return getExpression(this.keyCache, keyExpression, method).getValue(evalContext, type);
    }

    private Expression getExpression(Map<String, Expression> cache, String expression, Method method) {
        String key = toString(method, expression);
        Expression rtn = cache.get(key);
        if (rtn == null) {
            rtn = this.parser.parseExpression(expression);
            cache.put(key, rtn);
        }
        return rtn;
    }

    private String toString(Method method, String expression) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getName());
        sb.append("#");
        sb.append(method.toString());
        sb.append("#");
        sb.append(expression);
        return sb.toString();
    }

    @Slf4j
    public static class LazyParamAwareEvaluationContext extends StandardEvaluationContext {

        private final ApplicationContext applicationContext;

        private final ParameterNameDiscoverer paramDiscoverer;

        private final Object target;

        private final Method method;

        private final Object[] args;

        private final Class<?> targetClass;

        private final Map<String, Method> methodCache;

        private boolean variableLoaded = false;

        LazyParamAwareEvaluationContext(Object target, ApplicationContext applicationContext,
                                        ParameterNameDiscoverer paramDiscoverer, Method method, Object[] args, Class<?> targetClass,
                                        Map<String, Method> methodCache) {
            super(target);
            this.target = target;
            this.applicationContext = applicationContext;
            this.paramDiscoverer = paramDiscoverer;
            this.method = method;
            this.args = args;
            this.targetClass = targetClass;
            this.methodCache = methodCache;
        }

        /**
         * Load the param information only when needed.
         */
        @Override
        public Object lookupVariable(String name) {
            Object variable = super.lookupVariable(name);
            if (variable != null) {
                return variable;
            }
            if (!this.variableLoaded) {
                loadAllValueAsVariables();
                this.variableLoaded = true;
                variable = super.lookupVariable(name);
            }
            if (variable == null) {
                try {
                    variable = applicationContext.getBean(name);
                }
                catch (Exception e) {}
            }
            return variable;
        }

        private void loadAllValueAsVariables() {
            if (!ObjectUtils.isEmpty(this.args)) {
                // save arguments as indexed variables
                for (int i = 0; i < this.args.length; i++) {
                    setVariable("a" + i, this.args[i]);
                    setVariable("p" + i, this.args[i]);
                }
                if (this.method != null) {
                    String mKey = toString(this.method);
                    Method targetMethod = this.methodCache.get(mKey);
                    if (targetMethod == null) {
                        targetMethod = AopUtils.getMostSpecificMethod(this.method, this.targetClass);
                        if (targetMethod == null) {
                            targetMethod = this.method;
                        }
                        this.methodCache.put(mKey, targetMethod);
                    }
                    String[] parameterNames = this.paramDiscoverer.getParameterNames(targetMethod);
                    // save parameter names (if discovered)
                    if (parameterNames != null) {
                        for (int i = 0; i < parameterNames.length; i++) {
                            setVariable(parameterNames[i], this.args[i]);
                        }
                    }
                }
            }
            if (targetClass != null) {
                Field[] fields = targetClass.getDeclaredFields();
                for (Field field : fields) {
                    try {
                        setVariable(field.getName(), FieldUtils.readField(field, target, true));
                    }
                    catch (Exception e) {
                        log.warn(e.getMessage(), e);
                    }
                }
            }
            setVariable("applicationContext", applicationContext);

        }

        private String toString(Method m) {
            StringBuilder sb = new StringBuilder();
            sb.append(m.getDeclaringClass().getName());
            sb.append("#");
            sb.append(m.toString());
            return sb.toString();
        }
    }
}
