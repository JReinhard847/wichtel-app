import {Grid2} from "@mui/material";
import EventCard from "./EventCard.tsx";
import {WichtelEvent} from "../util/Utils.ts";

type EventGalleryProps = {
    eventList: WichtelEvent[]
}


export default function EventGallery(props: EventGalleryProps) {
    return (
        <>
            <Grid2 container>
                {props.eventList.map(event => <EventCard key={event.id} event={event}/>)}
            </Grid2>
        </>
    )
}