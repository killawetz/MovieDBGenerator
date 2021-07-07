package Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RoleModel {

    @SerializedName("jobs")
    private List<String> jobs;

    @SerializedName("department")
    private String department;

    public List<String> getJobs() {
        return jobs;
    }

    public String getDepartment() {
        return department;
    }
}
