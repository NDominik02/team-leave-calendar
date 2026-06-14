import { useAppData } from "./hooks/useAppData";
import CalendarGrid from "./components/calendar/CalendarGrid";
import RequestList from "./components/requests/RequestList";
import OnCallSchedule from "./components/oncall/OnCallSchedule";
import "./App.css";

export default function App() {
  const { members, leaveRequests, oncallSchedule, loading, refresh } =
    useAppData();

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>Team Leave Calendar</h1>
      </header>
      <div className="app-body">
        <main className="left-panel">
          <CalendarGrid
            members={members}
            leaveRequests={leaveRequests}
            oncallSchedule={oncallSchedule}
            onSaved={refresh}
          />
        </main>
        <aside className="right-panel">
          <RequestList
            leaveRequests={leaveRequests}
            members={members}
            onRefresh={refresh}
          />
          <OnCallSchedule schedule={oncallSchedule} onRefresh={refresh} />
        </aside>
      </div>
    </div>
  );
}
