package com.workerhiring.job;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface JobRepository extends JpaRepository<Job, Long> {

    /**
     * Search open/closed ads. {@code q} and {@code location} are lower-case LIKE patterns (or null to skip).
     */
    @Query("""
            select j from Job j join fetch j.postedBy
            where j.status = :status
              and (:q is null or lower(j.title) like :q or lower(j.description) like :q or lower(j.requirements) like :q)
              and (:location is null or lower(j.location) like :location)
              and (:category is null or j.category = :category)
            order by j.createdAt desc
            """)
    List<Job> search(Job.Status status, String q, String location, JobCategory category);

    @Query("select j from Job j join fetch j.postedBy where j.id = :id")
    Optional<Job> findWithPoster(Long id);

    @Query("select j from Job j join fetch j.postedBy where j.postedBy.id = :userId order by j.createdAt desc")
    List<Job> findByPoster(Long userId);

    @Modifying
    @Query("delete from Job j where j.postedBy.id = :userId")
    void deleteByPostedById(Long userId);
}
