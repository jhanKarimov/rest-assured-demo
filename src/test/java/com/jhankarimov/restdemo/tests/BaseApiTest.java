package com.jhankarimov.restdemo.tests;

import com.jhankarimov.restdemo.client.ApiClient;

public abstract class BaseApiTest {

    /** Stateless, so one shared instance is safe under parallel execution. */
    protected final ApiClient api = new ApiClient();

    /** Soft latency budget: warn above this, fail only above the hard limit. */
    protected static final long SOFT_BUDGET_MS = 1500;
    protected static final long HARD_BUDGET_MS = 8000;
}
