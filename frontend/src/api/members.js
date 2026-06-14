import client from './client'

export const getMembers = () => client.get('/members').then(r => r.data)