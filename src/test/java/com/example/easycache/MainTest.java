package com.example.easycache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Main 主类单元测试
 */
public class MainTest {

    @Test
    void testMainMethodExists() throws Exception {
        // 测试main方法存在且可调用
        // 注意：实际运行main方法可能会因为需要特定环境而失败
        // 这里只测试方法可以被调用

        assertDoesNotThrow(() -> {
            Class<?> mainClass = Class.forName("com.example.easycache.Main");
            mainClass.getMethod("main", String[].class);
        });
    }

    @Test
    void testMainClassIsPublic() {
        // 测试Main类是public的
        Class<Main> mainClass = Main.class;

        assertTrue(java.lang.reflect.Modifier.isPublic(mainClass.getModifiers()));
    }

    @Test
    void testMainHasMainMethod() throws Exception {
        // 测试Main类有main方法
        Class<?> mainClass = Class.forName("com.example.easycache.Main");

        assertDoesNotThrow(() -> {
            mainClass.getMethod("main", String[].class);
        });
    }

    @Test
    void testMainMethodIsStatic() throws Exception {
        // 测试main方法是静态的
        Class<?> mainClass = Class.forName("com.example.easycache.Main");
        var mainMethod = mainClass.getMethod("main", String[].class);

        assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
    }

    @Test
    void testMainMethodIsVoid() throws Exception {
        // 测试main方法返回void
        Class<?> mainClass = Class.forName("com.example.easycache.Main");
        var mainMethod = mainClass.getMethod("main", String[].class);

        assertEquals(void.class, mainMethod.getReturnType());
    }
}
