import { useState } from "react";
import dayjs from "dayjs";
import isoWeek from "dayjs/plugin/isoWeek";
import {
  getWeekDays,
  formatDateFull,
  splitIntoContinuousRanges,
} from "../../utils/dateUtils";
import { createLeaveRequest } from "../../api/leaveRequests";

dayjs.extend(isoWeek);

const DAY_LABELS = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];

export default function CalendarGrid({
  members,
  leaveRequests,
  oncallSchedule,
  onSaved,
}) {
  const [currentWeekStart, setCurrentWeekStart] = useState(
    dayjs().startOf("isoWeek"),
  );
  const [selected, setSelected] = useState({}); // { memberId: Set of dateStrings }
  const [saving, setSaving] = useState(false);
  const [reasonModal, setReasonModal] = useState(null); // { ranges, memberId, memberName }
  const [oncallWarning, setOncallWarning] = useState(null);

  const weekDays = getWeekDays(currentWeekStart);

  const getOnCallForWeek = (weekStart) => {
    const iso = weekStart.isoWeek();
    const year = weekStart.isoWeekYear();
    return (
      oncallSchedule.find((w) => w.weekNumber === iso && w.year === year) ??
      null
    );
  };

  const getLeaveStatusForCell = (memberId, dateStr) => {
    return leaveRequests.find(
      (lr) =>
        lr.member.id === memberId &&
        dateStr >= lr.startDate &&
        dateStr <= lr.endDate,
    );
  };

  const toggleCell = (memberId, dateStr) => {
    setSelected((prev) => {
      const memberDates = new Set(prev[memberId] ?? []);
      if (memberDates.has(dateStr)) {
        memberDates.delete(dateStr);
      } else {
        memberDates.add(dateStr);
      }
      return { ...prev, [memberId]: memberDates };
    });
  };

  const handleSave = () => {
    // Collect all selected cells
    const entries = Object.entries(selected).filter(
      ([, dates]) => dates.size > 0,
    );
    if (!entries.length) return;

    // Check on-call warning
    const weekOnCall = getOnCallForWeek(currentWeekStart);
    if (weekOnCall) {
      const onCallMemberId = weekOnCall.memberId;
      const onCallSelected = selected[onCallMemberId];
      if (onCallSelected && onCallSelected.size > 0) {
        setOncallWarning({ weekOnCall, entries });
        return;
      }
    }

    openReasonModal(entries);
  };

  const openReasonModal = (entries) => {
    setOncallWarning(null);
    setReasonModal({ entries, reason: "" });
  };

  const handleConfirmSave = async (reason) => {
    setSaving(true);
    try {
      for (const [memberId, dates] of reasonModal.entries) {
        const ranges = splitIntoContinuousRanges([...dates]);
        for (const range of ranges) {
          await createLeaveRequest({
            memberId: Number(memberId),
            startDate: range.startDate,
            endDate: range.endDate,
            reason: reason || null,
          });
        }
      }
      setSelected({});
      setReasonModal(null);
      onSaved();
    } catch (err) {
      const msg =
        err.response?.data?.detail ??
        err.response?.data?.message ??
        "Failed to save";
      alert(msg);
    } finally {
      setSaving(false);
    }
  };

  const weekOnCall = getOnCallForWeek(currentWeekStart);
  const hasSelection = Object.values(selected).some((s) => s.size > 0);

  return (
    <div className="calendar-wrapper">
      {/* Week navigation */}
      <div className="week-nav">
        <button
          onClick={() => setCurrentWeekStart((d) => d.subtract(1, "week"))}
        >
          ← Prev
        </button>
        <span className="week-label">
          {currentWeekStart.format("MMM D")} –{" "}
          {currentWeekStart.add(6, "day").format("MMM D, YYYY")}
        </span>
        <button onClick={() => setCurrentWeekStart((d) => d.add(1, "week"))}>
          Next →
        </button>
      </div>

      {/* Grid */}
      <div className="grid-container">
        <table className="calendar-table">
          <thead>
            <tr>
              <th className="name-col">
                {weekOnCall && (
                  <div className="oncall-badge">
                    On-call: <strong>{weekOnCall.memberName}</strong>
                    {weekOnCall.hasConflict && (
                      <span className="conflict-icon">⚠️</span>
                    )}
                  </div>
                )}
              </th>
              {weekDays.map((day) => (
                <th
                  key={day.format("YYYY-MM-DD")}
                  className={`day-col${day.isoWeekday() >= 6 ? " weekend" : ""}`}
                >
                  <div className="day-name">
                    {DAY_LABELS[day.isoWeekday() - 1]}
                  </div>
                  <div className="day-num">{day.format("D")}</div>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {members.map((member) => (
              <tr key={member.id}>
                <td className="name-cell">
                  {member.name}
                  {weekOnCall?.memberId === member.id && (
                    <span className="oncall-dot" title="On call this week">
                      📅
                    </span>
                  )}
                </td>
                {weekDays.map((day) => {
                  const dateStr = formatDateFull(day);
                  const existing = getLeaveStatusForCell(member.id, dateStr);
                  const isSelected = selected[member.id]?.has(dateStr) ?? false;
                  const isWeekend = day.isoWeekday() >= 6;
                  const isPast = day.isBefore(dayjs(), "day");

                  return (
                    <td
                      key={dateStr}
                      className={[
                        "day-cell",
                        isWeekend ? "weekend" : "",
                        isPast ? "past" : "",
                        existing
                          ? `status-${existing.status.toLowerCase()}`
                          : "",
                        isSelected ? "selected" : "",
                      ].join(" ")}
                      onClick={() =>
                        !existing && !isWeekend && !isPast && toggleCell(member.id, dateStr)
                      }
                      title={
                        existing
                          ? `${existing.status}${existing.reason ? ": " + existing.reason : ""}`
                          : ""
                      }
                    >
                      {isSelected && !existing && (
                        <span className="check">✓</span>
                      )}
                      {existing && <span className="status-dot" />}
                    </td>
                  );
                })}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Save button */}
      <div className="save-row">
        <button
          className="save-btn"
          onClick={handleSave}
          disabled={!hasSelection || saving}
        >
          {saving ? "Saving..." : "Save Leave Request"}
        </button>
      </div>

      {/* On-call warning modal */}
      {oncallWarning && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>⚠️ On-Call Warning</h3>
            <p>
              <strong>{oncallWarning.weekOnCall.memberName}</strong> is on call
              this week. Are you sure you want to submit a leave request?
            </p>
            <div className="modal-actions">
              <button onClick={() => setOncallWarning(null)}>Cancel</button>
              <button
                className="btn-primary"
                onClick={() => openReasonModal(oncallWarning.entries)}
              >
                Save Anyway
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Reason modal */}
      {reasonModal && (
        <div className="modal-overlay">
          <div className="modal">
            <h3>Add a reason (optional)</h3>
            <textarea
              className="reason-input"
              placeholder="Vacation, medical appointment..."
              value={reasonModal.reason}
              onChange={(e) =>
                setReasonModal((prev) => ({ ...prev, reason: e.target.value }))
              }
              rows={3}
            />
            <div className="modal-actions">
              <button onClick={() => setReasonModal(null)}>Cancel</button>
              <button onClick={() => handleConfirmSave("")}>Skip</button>
              <button
                className="btn-primary"
                onClick={() => handleConfirmSave(reasonModal.reason)}
              >
                Save
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
