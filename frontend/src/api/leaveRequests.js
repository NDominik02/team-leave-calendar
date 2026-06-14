import client from './client'

export const getLeaveRequests = () => client.get('/leave-requests').then(r => r.data)

export const createLeaveRequest = (data) => client.post('/leave-requests', data).then(r => r.data)

export const updateStatus = (id, status) =>
    client.patch(`/leave-requests/${id}/status`, { status }).then(r => r.data)

export const deleteLeaveRequest = (id) => client.delete(`/leave-requests/${id}`)