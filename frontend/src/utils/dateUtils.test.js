import { describe, it, expect } from 'vitest'
import { splitIntoContinuousRanges } from './dateUtils'

describe('splitIntoContinuousRanges', () => {
    it('should return empty array for empty input', () => {
        expect(splitIntoContinuousRanges([])).toEqual([])
    })

    it('should return single range for one date', () => {
        expect(splitIntoContinuousRanges(['2026-08-11'])).toEqual([
            { startDate: '2026-08-11', endDate: '2026-08-11' }
        ])
    })

    it('should return single range for continuous dates', () => {
        expect(splitIntoContinuousRanges(['2026-08-11', '2026-08-12', '2026-08-13'])).toEqual([
            { startDate: '2026-08-11', endDate: '2026-08-13' }
        ])
    })

    it('should return two ranges for non-continuous dates', () => {
        expect(splitIntoContinuousRanges(['2026-08-11', '2026-08-13'])).toEqual([
            { startDate: '2026-08-11', endDate: '2026-08-11' },
            { startDate: '2026-08-13', endDate: '2026-08-13' },
        ])
    })

    it('should handle unsorted input', () => {
        expect(splitIntoContinuousRanges(['2026-08-13', '2026-08-11', '2026-08-12'])).toEqual([
            { startDate: '2026-08-11', endDate: '2026-08-13' }
        ])
    })

    it('should return multiple ranges for mixed input', () => {
        expect(splitIntoContinuousRanges([
            '2026-08-11', '2026-08-12',
            '2026-08-14', '2026-08-15', '2026-08-16',
            '2026-08-20'
        ])).toEqual([
            { startDate: '2026-08-11', endDate: '2026-08-12' },
            { startDate: '2026-08-14', endDate: '2026-08-16' },
            { startDate: '2026-08-20', endDate: '2026-08-20' },
        ])
    })
})