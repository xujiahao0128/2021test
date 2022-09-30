package com.wangzaiplus.test.context;

import org.springframework.core.NamedThreadLocal;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * @author Mr.Xu
 * @Description 日志上下文容器
 * @date 2022年01月24日 16:31
 */
public class LogContext {

    private static final ThreadLocal<StandardEvaluationContext> CONTEXT_THREAD_LOCAL = new NamedThreadLocal<>("ThreadLocal StandardEvaluationContext");

    public static StandardEvaluationContext getContext() {
        return CONTEXT_THREAD_LOCAL.get() == null ? new StandardEvaluationContext(): CONTEXT_THREAD_LOCAL.get();
    }

    public static void putVariables(String key, Object value) {
        StandardEvaluationContext context = CONTEXT_THREAD_LOCAL.get() == null ? new StandardEvaluationContext(): CONTEXT_THREAD_LOCAL.get();
        context.setVariable(key, value);
        CONTEXT_THREAD_LOCAL.set(context);
    }

    public static void clearContext() {
        CONTEXT_THREAD_LOCAL.remove();
    }

}
