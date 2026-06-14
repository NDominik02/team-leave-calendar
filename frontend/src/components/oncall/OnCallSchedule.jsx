import { useState } from "react";
import { getAvailableMembers, setOverride } from "../../api/oncall";
import { formatDate } from "../../utils/dateUtils";

export default function OnCallSchedule({ schedule, onRefresh }) {
  const [expandedWeek, setExpandedWeek] = useState(null);
  const [availableMembers, setAvailableMembers] = useState([]);

  const handleRowClick = async (week) => {
    if (
      expandedWeek?.weekNumber === week.weekNumber &&
      expandedWeek?.year === week.year
    ) {
      setExpandedWeek(null);
      return;
    }
    const members = await getAvailableMembers(week.year, week.weekNumber);
    setAvailableMembers(members);
    setExpandedWeek(week);
  };

  const handleOverride = async (memberId) => {
    await setOverride(expandedWeek.year, expandedWeek.weekNumber, memberId);
    setExpandedWeek(null);
    onRefresh();
  };

  return (
    <div className="oncall-schedule">
      <h2 className="panel-title">On-Call Schedule</h2>
      {schedule.map((week) => (
        <div key={`${week.year}-${week.weekNumber}`}>
          <div
            className={[
              "oncall-row",
              week.hasConflict ? "oncall-conflict" : "",
              expandedWeek?.weekNumber === week.weekNumber
                ? "oncall-expanded"
                : "",
            ].join(" ")}
            onClick={() => handleRowClick(week)}
            title="Click to change on-call person"
          >
            <div className="oncall-week-dates">
              {formatDate(week.weekStart)} – {formatDate(week.weekEnd)}
            </div>
            <div className="oncall-member">
              {week.memberName}
              {week.override && <span className="override-tag">override</span>}
            </div>
            {week.hasConflict && (
              <span
                className="conflict-icon"
                title="On-call person has approved leave this week"
              >
                ⚠️
              </span>
            )}
          </div>

          {expandedWeek?.weekNumber === week.weekNumber &&
            expandedWeek?.year === week.year && (
              <div className="override-panel">
                <p className="override-label">Select replacement:</p>
                {availableMembers.length === 0 && (
                  <p className="empty-msg">No available members this week.</p>
                )}
                {availableMembers.filter((m) => m.id !== expandedWeek.memberId).map((m) => (
                  <button
                    key={m.id}
                    className="override-member-btn"
                    onClick={() => handleOverride(m.id)}
                  >
                    {m.name}
                  </button>
                ))}
                <button
                  className="btn-cancel-override"
                  onClick={() => setExpandedWeek(null)}
                >
                  Cancel
                </button>
              </div>
            )}
        </div>
      ))}
    </div>
  );
}
