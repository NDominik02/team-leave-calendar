import client from './client'

export const getSchedule = (weeks = 8) =>
    client.get(`/oncall/schedule?weeks=${weeks}`).then(r => r.data)

export const getAvailableMembers = (year, weekNumber) =>
    client.get(`/oncall/available?year=${year}&weekNumber=${weekNumber}`).then(r => r.data)

export const setOverride = (year, weekNumber, memberId) =>
    client.put('/oncall/override', { year, weekNumber, memberId }).then(r => r.data)