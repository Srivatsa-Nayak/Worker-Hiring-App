package com.workerhiring.application;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ApplicationRepository extends JpaRepository<JobApplication, Long> {

    Optional<JobApplication> findByJobIdAndApplicantId(Long jobId, Long applicantId);

    @Query("select a from JobApplication a where a.applicant.id = :applicantId and a.job.id in :jobIds")
    List<JobApplication> findByApplicantAndJobs(Long applicantId, Collection<Long> jobIds);

    @Query("select a.job.id, count(a) from JobApplication a where a.job.id in :jobIds group by a.job.id")
    List<Object[]> countByJobIds(Collection<Long> jobIds);

    @Query("""
            select a from JobApplication a join fetch a.job j join fetch j.postedBy
            where a.applicant.id = :applicantId order by a.createdAt desc
            """)
    List<JobApplication> findByApplicant(Long applicantId);

    @Query("""
            select a from JobApplication a join fetch a.applicant
            where a.job.id = :jobId order by a.createdAt asc
            """)
    List<JobApplication> findByJob(Long jobId);

    @Query("""
            select a from JobApplication a join fetch a.job j join fetch j.postedBy join fetch a.applicant
            where a.id = :id
            """)
    Optional<JobApplication> findFull(Long id);

    @Query("select a.applicant.id from JobApplication a where a.job.id = :jobId")
    List<Long> findApplicantIdsByJob(Long jobId);

    @Modifying
    @Query("delete from JobApplication a where a.job.id = :jobId")
    void deleteByJobId(Long jobId);

    @Modifying
    @Query("delete from JobApplication a where a.applicant.id = :userId")
    void deleteByApplicantId(Long userId);

    @Modifying
    @Query("delete from JobApplication a where a.job.id in (select j.id from Job j where j.postedBy.id = :userId)")
    void deleteByJobPostedById(Long userId);
}
