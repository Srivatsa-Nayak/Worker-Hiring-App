export type SalaryPeriod = "HOUR" | "DAY" | "WEEK" | "MONTH";
export type JobStatus = "OPEN" | "CLOSED";
export type ApplicationStatus = "APPLIED" | "CALL_LATER" | "INTERVIEW" | "HIRED" | "REJECTED";

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address: string;
  skills: string | null;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface CategoryOption {
  value: string;
  label: string;
}

export interface Job {
  id: number;
  title: string;
  category: string;
  categoryLabel: string;
  location: string;
  salaryAmount: number;
  salaryPeriod: SalaryPeriod;
  description: string;
  requirements: string;
  startDate: string;
  openings: number;
  contactPhone: string | null;
  contactEmail: string | null;
  status: JobStatus;
  createdAt: string;
  postedBy: { id: number; name: string };
  applicationCount: number;
  owner: boolean;
  myApplicationStatus: ApplicationStatus | null;
  myApplicationId: number | null;
}

export interface JobInput {
  title: string;
  category: string;
  location: string;
  salaryAmount: number | "";
  salaryPeriod: SalaryPeriod;
  description: string;
  requirements: string;
  startDate: string;
  openings: number | "";
  contactPhone: string;
  contactEmail: string;
}

export interface Application {
  id: number;
  jobId: number;
  status: ApplicationStatus;
  message: string | null;
  interviewAt: string | null;
  recruiterNote: string | null;
  createdAt: string;
  updatedAt: string;
  applicant: {
    id: number;
    name: string;
    phone: string;
    email: string;
    address: string;
    skills: string | null;
  } | null;
  job: {
    id: number;
    title: string;
    location: string;
    salaryAmount: number;
    salaryPeriod: SalaryPeriod;
    startDate: string;
    status: JobStatus;
    postedByName: string;
    contactPhone: string;
    contactEmail: string;
  } | null;
}

export interface NotificationItem {
  id: number;
  message: string;
  link: string | null;
  read: boolean;
  createdAt: string;
}
