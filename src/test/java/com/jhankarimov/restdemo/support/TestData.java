package com.jhankarimov.restdemo.support;

import org.testng.annotations.DataProvider;

public class TestData {

    @DataProvider(name = "userIds", parallel = true)
    public static Object[][] userIds() {
        return new Object[][]{{1}, {2}, {3}, {10}};
    }

    @DataProvider(name = "missingPostIds", parallel = true)
    public static Object[][] missingPostIds() {
        return new Object[][]{{0}, {101}, {99999}};
    }

    @DataProvider(name = "newPosts", parallel = true)
    public static Object[][] newPosts() {
        return new Object[][]{
                {1, "Short title", "Short body"},
                {7, "Title with unicode: sözaltı ✓", "Body with\nnewline and \"quotes\""},
                {42, "x".repeat(200), "y".repeat(1000)}
        };
    }
}
