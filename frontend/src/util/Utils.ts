export type WichtelUser = {
    id: string,
    name: string,
    email: string,
    oauthName?: string,
    oauthProvider?: string
}


export type WichtelEvent = {
    id: string,
    description: string,
    title: string,
    budget: string,
    organizer: WichtelUser
    participants: { participant: WichtelUser }[],
    hasPairing?: boolean
}

export type WichtelParticipant = {
    wishList: string,
    address: string,
    participant: WichtelUser
}

export default function isParticipatingIn(user: WichtelUser, event: WichtelEvent): boolean {
    return event.participants.find(participant => participant.participant.id === user.id) !== undefined
}