package com.workerhiring.config;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.workerhiring.application.ApplicationService;
import com.workerhiring.job.Job;
import com.workerhiring.job.JobCategory;
import com.workerhiring.job.JobDtos.JobRequest;
import com.workerhiring.job.JobService;
import com.workerhiring.user.User;
import com.workerhiring.user.UserDtos.RegisterRequest;
import com.workerhiring.user.UserDtos.UpdateProfileRequest;
import com.workerhiring.user.UserRepository;
import com.workerhiring.user.UserService;

/** Fills an empty database with a few accounts and ads so the app isn't blank on first run. */
@Component
@ConditionalOnProperty(name = "app.seed-demo-data", havingValue = "true")
public class DemoDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);
    public static final String DEMO_PASSWORD = "password123";

    private final UserRepository users;
    private final UserService userService;
    private final JobService jobService;
    private final ApplicationService applicationService;

    public DemoDataSeeder(UserRepository users, UserService userService, JobService jobService,
                          ApplicationService applicationService) {
        this.users = users;
        this.userService = userService;
        this.jobService = jobService;
        this.applicationService = applicationService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (users.count() > 0) {
            return;
        }
        User ravi = userService.register(new RegisterRequest("Ravi", "Shetty", "ravi@example.com",
                "9845012345", "Car Street, Mangaluru", DEMO_PASSWORD));
        User meera = userService.register(new RegisterRequest("Meera", "Kamath", "meera@example.com",
                "9900112233", "Hampankatta, Mangaluru", DEMO_PASSWORD));
        User arjun = userService.register(new RegisterRequest("Arjun", "Rao", "arjun@example.com",
                "9886001122", "Kankanady, Mangaluru", DEMO_PASSWORD));
        userService.updateProfile(arjun.getId(), new UpdateProfileRequest("Arjun", "Rao", "9886001122",
                "Kankanady, Mangaluru",
                "Two years as a shop assistant in a grocery store. Can handle billing and stock. Own two-wheeler with licence."));

        LocalDate soon = LocalDate.now().plusDays(7);
        var groceryHelper = jobService.create(new JobRequest("Grocery shop helper", JobCategory.SHOP_ASSISTANT,
                "Car Street, Mangaluru", 14000, Job.SalaryPeriod.MONTH,
                "Help customers find items, keep shelves stocked and the shop clean. Shop is open 8am to 8pm with a lunch break.",
                "Basic Kannada or Tulu. Able to lift 20 kg bags. Honest and on time.",
                soon, 2, "9845012345", "ravi@example.com"), ravi.getId());
        jobService.create(new JobRequest("Delivery rider", JobCategory.DELIVERY,
                "Car Street, Mangaluru", 600, Job.SalaryPeriod.DAY,
                "Deliver grocery orders to homes within 5 km of the shop. Fuel paid separately.",
                "Own two-wheeler and valid driving licence. Smartphone for order updates.",
                soon, 1, "9845012345", "ravi@example.com"), ravi.getId());
        jobService.create(new JobRequest("Cashier for bakery", JobCategory.CASHIER,
                "Hampankatta, Mangaluru", 15000, Job.SalaryPeriod.MONTH,
                "Handle billing and UPI payments at the counter, pack orders, and close the till at end of day.",
                "Comfortable with simple billing software. Previous counter experience preferred.",
                soon.plusDays(3), 1, "9900112233", "meera@example.com"), meera.getId());
        jobService.create(new JobRequest("Kitchen helper", JobCategory.KITCHEN,
                "Hampankatta, Mangaluru", 500, Job.SalaryPeriod.DAY,
                "Prep vegetables, wash utensils and help the baker during the morning rush (5am to 11am).",
                "Early riser. Clean habits. No experience needed, we will train you.",
                soon, 2, "9900112233", "meera@example.com"), meera.getId());
        jobService.create(new JobRequest("Tailor for alterations", JobCategory.TAILORING,
                "Hampankatta, Mangaluru", 120, Job.SalaryPeriod.HOUR,
                "Part-time alterations on sarees, blouses and school uniforms. Flexible hours, work on our machines.",
                "Must be able to do hemming, fitting and blouse stitching neatly.",
                soon.plusDays(10), 1, "9900112233", "meera@example.com"), meera.getId());

        applicationService.apply(groceryHelper.id(), arjun.getId(),
                "I live 2 km from Car Street and can start next week.");

        log.info("Seeded demo data. Log in as ravi@example.com, meera@example.com or arjun@example.com with password '{}'",
                DEMO_PASSWORD);
    }
}
