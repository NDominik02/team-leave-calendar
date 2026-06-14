# Team Leave Calendar

A full-stack web application for managing team leave requests and on-call schedules.

## Tech Stack

- **Backend:** Java 21, Spring Boot 4.1.0, SQLite, JPA/Hibernate
- **Frontend:** React 19, Vite, Day.js, Axios

---

## Prerequisites

| Tool | Minimum version |
|------|----------------|
| Java | 21 |
| Node.js | 18 |
| npm | 9 |

No database installation is required — SQLite runs as a file (`backend/leavecalendar.db`) created automatically on first start.

---

## Setup & Running

### 1. Clone the repository

```bash
git clone https://github.com/NDominik02/team-leave-calendar.git
cd team-leave-calendar
```

### 2. Start the backend

```bash
cd backend
./mvnw spring-boot:run        # macOS / Linux
mvnw.cmd spring-boot:run      # Windows
```

The server starts on **http://localhost:8080**.  
On first run it seeds the database with four team members: Alice, Bob, Charlie, Diana.

### 3. Start the frontend

Open a second terminal:

```bash
cd frontend
npm install
npm run dev
```

The app is available at **http://localhost:5173**.

---

## API Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/members` | List all team members |
| GET | `/api/leave-requests` | List all leave requests |
| POST | `/api/leave-requests` | Create a leave request |
| PATCH | `/api/leave-requests/{id}/status` | Approve or reject a request |
| DELETE | `/api/leave-requests/{id}` | Delete a request |
| GET | `/api/oncall/schedule?weeks=N` | On-call schedule for the next N weeks |
| GET | `/api/oncall/available?year=Y&weekNumber=W` | Members available to take on-call |
| PUT | `/api/oncall/override` | Override the on-call person for a week |

---

## Assumptions

- **Fixed team:** the four members (Alice, Bob, Charlie, Diana) are seeded automatically on first start. There is no UI for adding or removing members.
- **On-call rotation** follows a simple round-robin by member ID, one week per person, starting from the current ISO week.
- **Working days only:** the calendar blocks weekends (Saturday and Sunday) — they cannot be selected for leave.
- **No authentication:** the app has a single shared view; any user can approve, reject, or delete any request.
- **Whole-day requests only:** leave is submitted per calendar day, not by hour.
- **Overlap rule:** a member cannot have two non-rejected leave requests covering the same date.

---

## Features Completed

- Weekly calendar grid with navigation (previous / next week)
- Multi-cell leave selection per member, saved as one or more continuous date ranges
- Optional reason field on submission
- On-call warning modal when the current on-call person is included in a leave submission
- Leave request list with Approve / Reject / Delete actions
- Status badges (Pending / Approved / Rejected) with colour coding
- On-call schedule panel showing 12 weeks ahead
- Conflict indicator (⚠️) when the on-call person has an approved leave that week
- Override panel: click any on-call row to reassign that week to an available member
- Backend overlap validation with a `409 Conflict` response
- Unit and integration tests for leave request and on-call service logic

---

## Optional Improvements Added

- **Weekend blocking** — Saturday and Sunday cells are highlighted in red and cannot be clicked.
- **Override candidate filtering** — the current on-call person is excluded from their own replacement list.
- **Continuous range splitting** — selecting non-consecutive days creates separate leave requests per range automatically.

---

## Known Limitations / Not Implemented

- No user authentication or role-based access
- No support for public holidays
- No email or push notifications
- No team member management (add / remove members) through the UI
- No mobile-optimised layout
