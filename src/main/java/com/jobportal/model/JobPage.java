
package com.jobportal.model;

import java.util.List;

public class JobPage {

    private List<Job> jobs;
    private int page;
    private int size;
    private long totalJobs;
    private long totalPages;

    public JobPage(
            List<Job> jobs,
            int page,
            int size,
            long totalJobs,
            long totalPages) {

        this.jobs = jobs;
        this.page = page;
        this.size = size;
        this.totalJobs = totalJobs;
        this.totalPages = totalPages;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalJobs() {
        return totalJobs;
    }

    public long getTotalPages() {
        return totalPages;
    }
}
