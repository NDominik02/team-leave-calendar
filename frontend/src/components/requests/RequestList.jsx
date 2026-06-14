import { updateStatus, deleteLeaveRequest } from "../../api/leaveRequests";
import { formatDate } from "../../utils/dateUtils";

const STATUS_LABELS = {
  PENDING: "Pending",
  APPROVED: "Approved",
  REJECTED: "Rejected",
};

export default function RequestList({ leaveRequests, onRefresh }) {
  const handleStatus = async (id, status) => {
    await updateStatus(id, status);
    onRefresh();
  };

  const handleDelete = async (id) => {
    await deleteLeaveRequest(id);
    onRefresh();
  };

  const sorted = [...leaveRequests].sort(
    (a, b) => new Date(b.startDate) - new Date(a.startDate),
  );

  return (
    <div className="request-list">
      <h2 className="panel-title">Leave Requests</h2>
      {sorted.length === 0 && (
        <p className="empty-msg">No leave requests yet.</p>
      )}
      {sorted.map((lr) => (
        <div
          key={lr.id}
          className={`request-card status-card-${lr.status.toLowerCase()}`}
        >
          <div className="request-header">
            <span className="request-member">{lr.member.name}</span>
            <span className={`badge badge-${lr.status.toLowerCase()}`}>
              {STATUS_LABELS[lr.status]}
            </span>
          </div>
          <div className="request-dates">
            {formatDate(lr.startDate)} – {formatDate(lr.endDate)}
          </div>
          {lr.reason && <div className="request-reason">"{lr.reason}"</div>}
          {lr.status === "PENDING" && (
            <div className="request-actions">
              <button
                className="btn-approve"
                onClick={() => handleStatus(lr.id, "APPROVED")}
              >
                Approve
              </button>
              <button
                className="btn-reject"
                onClick={() => handleStatus(lr.id, "REJECTED")}
              >
                Reject
              </button>
              <button
                className="btn-delete"
                onClick={() => handleDelete(lr.id)}
              >
                Delete
              </button>
            </div>
          )}
        </div>
      ))}
    </div>
  );
}
