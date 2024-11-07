import {WichtelEvent} from "../util/Utils.ts";
import {useEffect, useState} from "react";
import axios from "axios";
import EventGallery from "../components/EventGallery.tsx";
import AddNewEventButton from "../components/AddNewEventButton.tsx";

export default function OpenEventsPage() {

    const [events, setEvents] = useState<WichtelEvent[]>([])
    const [loggedIn, setLoggedIn] = useState(false)

    useEffect(() => {
        axios.get<WichtelEvent[]>("api/event")
            .then(response => setEvents(response.data.filter(event => !event.hasPairing)))
            .catch(err => console.log(err))
        axios.get("api/user/me")
            .then(() => setLoggedIn(true))
            .catch(() => setLoggedIn(false))
    }, []);

    return (
        <>
            <EventGallery eventList={events}/>
            {loggedIn && <AddNewEventButton/>}
        </>
    )
}