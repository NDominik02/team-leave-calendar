import dayjs from 'dayjs'
import isoWeek from 'dayjs/plugin/isoWeek'
import weekOfYear from 'dayjs/plugin/weekOfYear'

dayjs.extend(isoWeek)
dayjs.extend(weekOfYear)

export const formatDate = (date) => dayjs(date).format('MMM D')
export const formatDateFull = (date) => dayjs(date).format('YYYY-MM-DD')

export const getWeekDays = (mondayDate) => {
    const monday = dayjs(mondayDate)
    return Array.from({ length: 7 }, (_, i) => monday.add(i, 'day'))
}

export const splitIntoContinuousRanges = (selectedDates) => {
    if (!selectedDates.length) return []
    const sorted = [...selectedDates].sort()
    const ranges = []
    let start = sorted[0]
    let prev = sorted[0]

    for (let i = 1; i < sorted.length; i++) {
        const curr = sorted[i]
        const diff = dayjs(curr).diff(dayjs(prev), 'day')
        if (diff === 1) {
            prev = curr
        } else {
            ranges.push({ startDate: start, endDate: prev })
            start = curr
            prev = curr
        }
    }
    ranges.push({ startDate: start, endDate: prev })
    return ranges
}