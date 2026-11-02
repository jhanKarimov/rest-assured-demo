package com.jhankarimov.restdemo.support;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/** Retries a failed test a couple of times to absorb network blips on a public API. */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRIES = 2;
    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempts < MAX_RETRIES) {
            attempts++;
            System.out.printf("RETRY %d/%d: %s%n", attempts, MAX_RETRIES, result.getName());
            return true;
        }
        return false;
    }
}
