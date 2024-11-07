import {useEffect, useState} from "react";
import axios from "axios";
import EventGallery from "../components/EventGallery.tsx";
import {useNotifications} from "@toolpad/core";
import AddNewEventButton from "../components/AddNewEventButton.tsx";
import {WichtelEvent, WichtelUser} from "../util/Utils.ts";

export default function EventsByMePage() {

    const [events, setEvents] = useState<WichtelEvent[]>([])
    const notification = useNotifications()

    useEffect(() => {
        axios.get<WichtelUser>("/api/user/me")
            .then(userResponse => {
                axios.get<WichtelEvent[]>("/api/event")
                    .then(response => {
                        setEvents(response.data.filter(event => event.organizer.id === userResponse.data.id))
                    })
            })
            .catch(() => notification.show("Login to see the events you are organizing",
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