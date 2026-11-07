# Worker Hiring

A web app where **small shops find local workers** and **workers find shop jobs nearby**.

A bakery that needs a cashier, a grocery store that needs a delivery rider, or a tailor who needs help with alterations posts a work ad. People looking for that kind of work browse the ads, apply in one step, and see the shop owner's response (call later, interview, hired or not selected) as soon as it's made.

The project started as a college mini project (Android app, Java + Firebase, VI semester Mobile Application Development, Sahyadri College of Engineering & Management, 2021–22). That version was never finished. This branch rebuilds it as a complete web app with a **Next.js** frontend and a **Spring Boot** backend.

---

## Features

**Accounts**
- Sign up with first name, last name, email, phone number, address and password. Log in and log out.
- One account works for both sides: you can post work and apply for work.
- Profile page with your details, editable "skills and experience" (shown to recruiters when you apply), and a summary of your ads.
- Settings: light/dark/match-device theme, change password, log out, delete account.

**For shop owners (recruiters)**
- Post a work ad with work name, category, location, salary (per hour/day/week/month), description, requirements, start date, number of openings and contact phone/email.
- *My ads*: every ad with its applicant count and open/closed state.
- Applicant manager for each ad: see each applicant's phone, email, address, skills and message. Then:
  - mark them **Call later**
  - **Schedule an interview** (date and time, plus an optional note such as what to bring)
  - **Hire** them
  - mark them **Not selected**
  - add a note at any stage
- Filter applicants by status. Edit, close, reopen or delete ads.

**For work seekers**
- Browse all open work without an account. Search by keyword and area, and filter by category.
- Job page with full details. Contact details are shown only to logged-in users.
- Apply with an optional message. You can't apply twice, apply to your own ad, or apply to a closed ad.
- *My applications*: the status of each application, interview time, recruiter notes, and the contact number once the recruiter responds. You can withdraw an application.

**Notifications**
- A bell with an unread count. A recruiter is notified when someone applies or withdraws. A worker is notified when their status changes or an ad they applied to is removed.

---

## Tech stack

| Layer    | Technology |
|----------|------------|
| Frontend | Next.js 15 (App Router), React 19, TypeScript, Tailwind CSS 4 |
| Backend  | Spring Boot 3.5 (Web, Data JPA, Security, Validation), Java 25 |
| Database | H2 (file-based, stored in `backend/data/`) |
| Auth     | Opaque bearer tokens (stored hashed in the database), BCrypt passwords |

```
Browser ──► Next.js (localhost:3000) ──/api/*──► Spring Boot (localhost:8080) ──► H2
```

The browser only talks to the Next.js server. Next.js forwards every `/api/*` request to Spring Boot (see `frontend/next.config.ts`), so there is no CORS setup to do in development.

---

## Project structure

```
backend/                     Spring Boot API
  src/main/java/com/workerhiring/
    auth/                    login/register/logout, token service + filter
    user/                    user entity, profile, password, account deletion
    job/                     job ads: search, create, edit, close, delete
    application/             applying, applicant management, statuses
    notification/            in-app notifications
    config/                  security, CORS, demo data seeder
    common/                  error handling
  src/test/                  integration tests (MockMvc + in-memory H2)
frontend/                    Next.js app
  src/app/                   pages (/, /jobs/[id], /post, /my-ads, /applications, ...)
  src/components/            header, forms, job card, UI pieces, auth context
  src/lib/                   API client, types, formatting
```

---

## Running locally

### Prerequisites
- **JDK 25** (the backend targets Java 25)
- **Node.js 20+** and npm

> If `JAVA_HOME` points to an older JDK, point it at JDK 25 for the backend commands.
> PowerShell: `$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.4"`
> Git Bash: `export JAVA_HOME="/c/Program Files/Java/jdk-25.0.4"`

### 1. Start the backend
```bash
cd backend
./mvnw spring-boot:run        # Windows CMD/PowerShell: mvnw.cmd spring-boot:run
```
The API runs at http://localhost:8080. On first start it fills the database with demo data.

### 2. Start the frontend
```bash
cd frontend
npm install
npm run dev
```
Open **http://localhost:3000**.

### Demo accounts
All demo accounts use the password `password123`.

| Email               | Who they are |
|---------------------|--------------|
| `ravi@example.com`  | Grocery shop owner with 2 ads, one of which already has an applicant |
| `meera@example.com` | Bakery owner with 3 ads |
| `arjun@example.com` | Work seeker who has applied to Ravi's grocery helper ad |

Log in as Ravi to manage Arjun's application. Then log in as Arjun to see the update and the notification.

### Tests and builds
```bash
cd backend  && ./mvnw test          # API integration tests
cd frontend && npm run typecheck    # TypeScript check
cd frontend && npm run build        # production build
```

---

## Database (H2)

- Data is stored in `backend/data/workerhiring.mv.db` and survives restarts. To start over, stop the backend, delete `backend/data/`, and start it again. The demo data will be recreated.
- Browse the data at **http://localhost:8080/h2-console** with:
  - JDBC URL: `jdbc:h2:file:./data/workerhiring`
  - user: `sa`
  - empty password
- The schema is created by Hibernate (`ddl-auto=update`). Tables: `app_user`, `auth_token`, `job`, `job_application`, `notification`.
- To turn off demo data, set `app.seed-demo-data=false` in `backend/src/main/resources/application.properties`.

---

## API overview

All endpoints are under `/api`. Authenticated endpoints need `Authorization: Bearer <token>`.

| Method | Endpoint | Auth | Purpose |
|--------|----------|------|---------|
| POST | `/auth/register` | | Create an account and get a token |
| POST | `/auth/login` | | Log in and get a token |
| POST | `/auth/logout` | ✓ | Revoke the current token |
| GET | `/auth/me` | ✓ | Current user |
| PUT | `/users/me` | ✓ | Update profile |
| PUT | `/users/me/password` | ✓ | Change password |
| POST | `/users/me/delete` | ✓ | Delete the account and its data (requires password) |
| GET | `/jobs?q=&location=&category=` | | Search open ads |
| GET | `/jobs/categories` | | Category list |
| GET | `/jobs/{id}` | | Ad details (contact info only when logged in) |
| GET | `/jobs/mine` | ✓ | Your ads |
| POST | `/jobs` | ✓ | Post an ad |
| PUT | `/jobs/{id}` | ✓ owner | Edit an ad |
| PATCH | `/jobs/{id}/status` | ✓ owner | Open or close an ad |
| DELETE | `/jobs/{id}` | ✓ owner | Delete an ad and its applications |
| POST | `/jobs/{id}/applications` | ✓ | Apply |
| GET | `/jobs/{id}/applications` | ✓ owner | Applicants for an ad |
| GET | `/applications/mine` | ✓ | Your applications |
| PATCH | `/applications/{id}` | ✓ ad owner | Set status, interview time and note |
| DELETE | `/applications/{id}` | ✓ applicant | Withdraw |
| GET | `/notifications` | ✓ | Latest notifications and unread count |
| POST | `/notifications/{id}/read`, `/notifications/read-all` | ✓ | Mark notifications as read |

Validation errors return `400` with `{ "message": "...", "fieldErrors": { "field": "reason" } }`.

---

## Possible next steps

- Swap H2 for PostgreSQL or MySQL for production (change the datasource settings and add the driver).
- Email or SMS alerts for new applications and status changes.
- Work recommendations based on a seeker's past applications (from the original report's future-work section).
- Resume tips for work seekers (also from the original report).
- Photos for shops and profiles. Location-based "near me" search.
