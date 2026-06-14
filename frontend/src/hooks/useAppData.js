import { useState, useEffect, useCallback } from 'react'
import { getMembers } from '../api/members'
import { getLeaveRequests } from '../api/leaveRequests'
import { getSchedule } from '../api/oncall'

export const useAppData = () => {
    const [members, setMembers] = useState([])
    const [leaveRequests, setLeaveRequests] = useState([])
    const [oncallSchedule, setOncallSchedule] = useState([])
    const [loading, setLoading] = useState(true)

    const refresh = useCallback(async () => {
        try {
            const [m, lr, oc] = await Promise.all([
                getMembers(),
                getLeaveRequests(),
                getSchedule(12),
            ])
            setMembers(m)
            setLeaveRequests(lr)
            setOncallSchedule(oc)
        } finally {
            setLoading(false)
        }
    }, [])

    useEffect(() => { refresh() }, [refresh])

    return { members, leaveRequests, oncallSchedule, loading, refresh }
}