import {useParams} from "react-router-dom";
import isParticipatingIn, {WichtelEvent, WichtelParticipant, WichtelUser} from "../util/Utils.ts";
import {Fragment, useEffect, useState} from "react";
import axios from "axios";
import ParticipantListItem from "../components/ParticipantListItem.tsx";
import {Box, Button, Card, CardContent, IconButton, List, Typography} from "@mui/material";
import {useNotifications} from "@toolpad/core";
import EditIcon from '@mui/icons-material/Edit';
import EditEventDialog from "../components/EditEventDialog.tsx";
import AddWishListDialog from "../components/AddWishListDialog.tsx";
import ShowMyPairingDialog from "../components/ShowMyPairingDialog.tsx";
import AddUsersToEventDialog from "../components/AddUsersToEventDialog.tsx";

export default function SingleEventPage() {
    const params = useParams();
    const id: string | undefined = params.id;

    const dummyEvent: WichtelEvent = {
        budget: "", description: "", id: "", organizer: {
            id: "",
            name: "",
            email: "",
        }, participants: [], title: "", hasPairing: false
    }

    const [event, setEvent] = useState<WichtelEvent>(dummyEvent)
    const [user, setUser] = useState<WichtelUser>({
        id: "",
        name: "",
        email: "",
    })
    const [editDialogOpen, setEditDialogOpen] = useState(false)
    const [editEvent, setEditEvent] = useState<WichtelEvent>(dummyEvent)

    const [addUsersOpen, setAddUsersOpen] = useState(false)

    const [addWishListOpen, setAddWishListOpen] = useState(false)
    const [showPairingOpen, setShowPairingOpen] = useState(false)
    const [pairing, setPairing] = useState<WichtelParticipant>({
        address: "", participant: {
            id: "",
            name: "",
            email: ""
        }, wishList: ""
    })
    const notifications = useNotifications()


    function fetchEventData() {
        axios.get("/api/event/" + id)
            .then(response => {
                setEvent(response.data)
                setEditEvent(response.data)
            })
            .catch(err => console.log(err))
    }

    function handleRemoveUser(user: WichtelUser) {
        axios.delete("/api/event/" + event.id + "/" + user.id)
            .then(() => notifications.show("Successfully removed " + user.name + " from the event",
                {
                    autoHideDuration: 3000,
                    severity: "success"
                }))
            .catch(() => notifications.show("Error trying to remove user from event",
                {
                    autoHideDuration: 3000,
                    severity: "error"
                }))
            .finally(() => fetchEventData())
    }

    useEffect(() => {
        fetchEventData()
        axios.get("/api/user/me")
            .then(response => setUser(response.data))
    }, []);

    function handleJoin() {
        axios.post("/api/event/" + event.id + "/" + user.id)
            .then(() => notifications.show("Joined event",
                {
                    autoHideDuration: 3000,
                    severity: "success"
                }))
            .catch(() => {
                if (user.id !== "") {
                    notifications.show("Failed to join event",
                        {
                            autoHideDuration: 3000,
                            severity: "error"
                        })
                } else {
                    notifications.show("Login to join this event",
                        {
                            autoHideDuration: 3000,
                            severity: "warning"
                        })
                }
            })
            .finally(() => fetchEventData())
    }

    function amOrganizer(): boolean {
        return event.organizer.id === user.id
    }

    function handleLeave() {
        axios.delete("/api/event/" + event.id + "/" + user.id)
            .then(() => {
                notifications.show("Left event",
                    {
                        autoHideDuration: 3000,
                        severity: "success"
                    })
                fetchEventData()
            })
    }

    function handleOpenEditDialog() {
        setEditDialogOpen(true)
    }

    function handleShowPairing() {
        axios.get("/api/event/" + event.id + "/" + user.id)
            .then(response => {
                setPairing(response.data)
                setShowPairingOpen(true)
            })
            .catch(() => notifications.show("Error trying to fetch pairing",
                {
                    autoHideDuration: 3000,
                    severity: "error"
                }
            ))
    }

    function handleGeneratePairings() {
        axios.post("/api/event/pairings/" + event.id)
            .then(() => notifications.show("Generated pairings",
                {
                    autoHideDuration: 3000,
                    severity: "success"
                }))
            .catch(() => notifications.show("Failed to generate pairings",
                {
                    autoHideDuration: 3000,
                    severity: "error"
                }))
            .finally(() => fetchEventData())
    }

    function handleAddWishList() {
        setAddWishListOpen(true)
    }

    let userButtons;

    if (isParticipatingIn(user, event)) {
        if (event.hasPairing) {
            userButtons = (
                <Button variant="contained" color="primary" onClick={handleShowPairing}>
                    Show My Pairing
                </Button>
            );
        } else {
            userButtons = (
                <Button variant="contained" color="primary" onClick={handleLeave}>
                    Leave
                </Button>
            );
        }
    } else {
        if (event.hasPairing) {
            userButtons = (
                <Button variant="contained" color="secondary" disabled={true}>
                    Closed
                </Button>
            );
        } else {
            userButtons = (
                <Button variant="contained" color="primary" onClick={handleJoin}>
                    Join
                </Button>
            );
        }
    }

    return (
        <>
            <Card>
                <CardContent>
                    <Box display="flex" justifyContent="space-between" alignItems="center">
                        <Typography variant="h4" gutterBottom>
                            {event.title || "Title"}
                        </Typography>
                        {amOrganizer() && <IconButton onClick={handleOpenEditDialog}>
                            <EditIcon/>
                        </IconButton>}
                    </Box>
                    <Typography variant="h6">Organized
                        by {event.organizer.name} ({event.organizer.oauthName} on {event.organizer.oauthProvider})</Typography>
                    <Typography variant="h6" color="textSecondary">
                        {event.description || "Description"}
                    </Typography>
                    <Typography
                        variant="h6">{event.budget ? ("The budget has been set to " + event.budget) : "No budget was set"}
                    </Typography>
                    <Box display="flex" flexDirection="column" gap={2}>
                        <Box display="flex" gap={1} mt={2}>
                            <div>
                                {userButtons}
                            </div>
                            {isParticipatingIn(user, event) && !event.hasPairing && (
                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={event.hasPairing ? handleShowPairing : handleAddWishList}
                                >
                                    {event.hasPairing ? "Show my pairing" : "Add a wish list"}
                                </Button>
                            )}
                        </Box>

                        <Box display="flex" gap={1} mt={2}>
                            {amOrganizer() && !event.hasPairing && (
                                <>
                                    <Button variant="contained" color="primary" onClick={handleGeneratePairings}>
                                        Generate Pairings
                                    </Button>
                                    <Button variant="contained" onClick={() => setAddUsersOpen(true)}>Add users</Button>
                                </>
                            )}

                        </Box>
                    </Box>
                    <Box my={3}>
                        <Typography variant="h5" gutterBottom>
                            Participants
                        </Typography>
                        <List>
                            {event.participants.map(({participant}) => (
                                <Fragment key={participant.id}>
                                    <ParticipantListItem participant={participant}
                                                         canRemove={amOrganizer() && !event.hasPairing}
                                                         onRemove={handleRemoveUser}/>
                                </Fragment>
                            ))}
                        </List>
                    </Box>
                </CardContent>
            </Card>
            <Button onClick={() => setAddUsersOpen(true)}>Test</Button>
            <EditEventDialog open={editDialogOpen} setOpen={setEditDialogOpen} fetchData={fetchEventData}
                             event={editEvent} setEvent={setEditEvent}/>
            <AddWishListDialog participant={user} eventId={event.id} open={addWishListOpen}
                               setOpen={setAddWishListOpen}/>
            <ShowMyPairingDialog open={showPairingOpen} setOpen={setShowPairingOpen} pairing={pairing}/>
            <AddUsersToEventDialog open={addUsersOpen} setOpen={setAddUsersOpen} event={event}
                                   fetchData={fetchEventData}/>
        </>
    )
}