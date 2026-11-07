package com.workerhiring;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class HiringFlowTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    private String register(String first, String email) throws Exception {
        var body = Map.of("firstName", first, "lastName", "Test", "email", email,
                "phone", "9876543210", "address", "Mangaluru", "password", "secret123");
        var res = mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();
        return json.readTree(res.getResponse().getContentAsString()).get("token").asText();
    }

    private Map<String, Object> jobBody(String title) {
        return Map.ofEntries(
                Map.entry("title", title), Map.entry("category", "SHOP_ASSISTANT"),
                Map.entry("location", "Car Street"), Map.entry("salaryAmount", 12000),
                Map.entry("salaryPeriod", "MONTH"), Map.entry("description", "Help in the shop"),
                Map.entry("requirements", "Be on time"), Map.entry("startDate", LocalDate.now().plusDays(3).toString()),
                Map.entry("openings", 1), Map.entry("contactPhone", "9876543210"),
                Map.entry("contactEmail", "shop@example.com"));
    }

    private JsonNode body(org.springframework.test.web.servlet.ResultActions r) throws Exception {
        return json.readTree(r.andReturn().getResponse().getContentAsString());
    }

    @Test
    void fullHiringFlow() throws Exception {
        String recruiter = register("Ramesh", "ramesh@test.com");
        String seeker = register("Mahesh", "mahesh@test.com");

        // Recruiter posts an ad
        long jobId = body(mvc.perform(post("/api/jobs").header("Authorization", "Bearer " + recruiter)
                .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(jobBody("Shop helper"))))
                .andExpect(status().isCreated())).get("id").asLong();

        // Anyone can browse; contact details are hidden from anonymous visitors
        mvc.perform(get("/api/jobs").param("q", "helper"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].contactPhone").value(nullValue()));

        // Recruiter can't apply to own ad
        mvc.perform(post("/api/jobs/" + jobId + "/applications").header("Authorization", "Bearer " + recruiter)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());

        // Seeker applies, and a second attempt is rejected
        long appId = body(mvc.perform(post("/api/jobs/" + jobId + "/applications")
                .header("Authorization", "Bearer " + seeker)
                .contentType(MediaType.APPLICATION_JSON).content("{\"message\":\"I can start Monday\"}"))
                .andExpect(status().isCreated())).get("id").asLong();
        mvc.perform(post("/api/jobs/" + jobId + "/applications").header("Authorization", "Bearer " + seeker)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isConflict());

        // Recruiter got notified and sees the applicant
        mvc.perform(get("/api/notifications").header("Authorization", "Bearer " + recruiter))
                .andExpect(jsonPath("$.unread").value(1));
        mvc.perform(get("/api/jobs/" + jobId + "/applications").header("Authorization", "Bearer " + recruiter))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].applicant.name").value("Mahesh Test"));

        // Seeker can't see applicants of someone else's ad
        mvc.perform(get("/api/jobs/" + jobId + "/applications").header("Authorization", "Bearer " + seeker))
                .andExpect(status().isForbidden());

        // Interview requires a time
        mvc.perform(patch("/api/applications/" + appId).header("Authorization", "Bearer " + recruiter)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"INTERVIEW\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(patch("/api/applications/" + appId).header("Authorization", "Bearer " + recruiter)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INTERVIEW\",\"interviewAt\":\"2030-01-05T10:30:00\",\"recruiterNote\":\"Bring ID\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INTERVIEW"));

        // Seeker sees the update
        mvc.perform(get("/api/applications/mine").header("Authorization", "Bearer " + seeker))
                .andExpect(jsonPath("$[0].status").value("INTERVIEW"))
                .andExpect(jsonPath("$[0].recruiterNote").value("Bring ID"))
                .andExpect(jsonPath("$[0].job.title").value("Shop helper"));
        mvc.perform(get("/api/jobs/" + jobId).header("Authorization", "Bearer " + seeker))
                .andExpect(jsonPath("$.myApplicationStatus").value("INTERVIEW"))
                .andExpect(jsonPath("$.contactPhone").value("9876543210"));

        // Closing the ad hides it from the public list and blocks new applicants
        mvc.perform(patch("/api/jobs/" + jobId + "/status").header("Authorization", "Bearer " + recruiter)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"CLOSED\"}"))
                .andExpect(status().isOk());
        mvc.perform(get("/api/jobs")).andExpect(jsonPath("$", hasSize(0)));

        // Deleting the ad removes its applications
        mvc.perform(delete("/api/jobs/" + jobId).header("Authorization", "Bearer " + recruiter))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/applications/mine").header("Authorization", "Bearer " + seeker))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void authRules() throws Exception {
        mvc.perform(get("/api/jobs/mine")).andExpect(status().isUnauthorized());
        String token = register("Suresh", "suresh@test.com");

        // Duplicate email
        var dup = Map.of("firstName", "M", "lastName", "K", "email", "SURESH@test.com",
                "phone", "9876543210", "address", "x", "password", "secret123");
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(dup))).andExpect(status().isConflict());

        // Validation errors come back per field
        mvc.perform(post("/api/jobs").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.title").exists());

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"suresh@test.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());

        mvc.perform(post("/api/auth/logout").header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }
}
