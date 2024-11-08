import {useEffect, useState} from "react";
import {WichtelEvent, WichtelUser} from "../util/Utils.ts";
import {useNotifications} from "@toolpad/core";
import axios from "axios";
import EventGallery from "../components/EventGallery.tsx";
import AddNewEventButton from "../components/AddNewEventButton.tsx";

export default function EventsWithMePage() {

    const [events, setEvents] = useState<WichtelEvent[]>([])
    const notification = useNotifications()

    useEffect(() => {
        axios.get<WichtelUser>("/api/user/me")
            .then(userResponse => {
                axios.get<WichtelEvent[]>("/api/event")
                    .then(response => {
                        setEvents(response.data.filter(event => event.participants.find(participant => participant.participant.id === userResponse.data.id)))
                    })
            })
            .catch(() => notification.show("Login to see the events you are participating in",
                {
                    autoHideDuration: 3000,
                    severity: "error"
                }))
    }, []);

    return (
        <>
            <EventGallery eventList={events}/>
            <AddNewEventButton/>
        </>
    )
}