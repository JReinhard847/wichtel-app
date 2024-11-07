import {Card, CardContent, CardHeader, Typography, Divider, Button} from '@mui/material';
import {useNavigate} from "react-router-dom";
import {WichtelEvent} from "../util/Utils.ts";


type EventCardProps = {
    event: WichtelEvent;
};


export default function EventCard(props: EventCardProps) {
    const {title, description, budget, organizer, participants} = props.event;
    const navigate = useNavigate()

    return (
        <Card sx={{width: 345, height: 400, margin: '16px', boxShadow: 3, position: 'relative'}}>
            <CardHeader
                title={title || "Untitled"}
                subheader={`Organized by ${organizer.name} (aka ${organizer.oauthName} on ${organizer.oauthProvider})`}
            />
            <CardContent>
                <Divider sx={{mb: 1, mt: 1}}/>
                <Typography variant="body2" color="text.secondary" sx={{
                    mb: 1,
                    maxHeight: '200px',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    whiteSpace: 'wrap',
                    display: '-webkit-box',
                    WebkitLineClamp: 8,
                    WebkitBoxOrient: 'vertical'
                }}>
                    {description || "No description has been set"}
                </Typography>
                <Divider sx={{my: 1}}/>
                <Typography variant="subtitle2" color="text.secondary">
                    Budget: {budget || "No budget has been set"}
                </Typography>
                <Typography variant="subtitle2" color="text.secondary">
                    Participants: {participants.length}
                </Typography>
                <Button
                    variant="contained"
                    color="secondary"
                    sx={{position: 'absolute', bottom: 8, right: 8}}
                    onClick={() => {
                        navigate(`/events/${props.event.id}`)
                    }}
                >
                    Checkout Event
                </Button>
            </CardContent>
        </Card>
    );
};