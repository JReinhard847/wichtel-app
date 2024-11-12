import {Box, Typography} from "@mui/material";
import {useEffect, useState} from "react";
import axios from "axios";

export default function LandingPage() {

    const [loggedIn, setLoggedIn] = useState(false)

    useEffect(() => {
        axios.get("api/user/me")
            .then(() => setLoggedIn(true))
            .catch(() => setLoggedIn(false))
    }, []);

    return (
        <Box sx={{ padding: 4 }}>
            <Typography variant="h3" gutterBottom>
                <h1>Welcome to my Wichtel App</h1>
            </Typography>

            <Typography variant="h5">
                Here you can view, join, and organize Wichtel Events with your friends or coworkers.
            </Typography>

            <Box sx={{ marginY: 2 }} />

            {!loggedIn ? (
                <Typography variant="h5" color="textSecondary" >
                    Click the icon on the sidebar to check out all currently open events. To join one of these events,
                    you need to first log in using one of the provided authorization services (currently only GitHub is supported).
                </Typography>
            ) : (
                <>
                    <Typography variant="h5" color="textSecondary" >
                        On the User Info tab on the left sidebar, you can change your username or email address,
                        or delete your account entirely.
                    </Typography>
                    <Box sx={{ marginY: 2 }} />
                    <Typography variant="h5" color="textSecondary" >
                        On the Open Events tab, you see all currently open events; the Organized by Me tab shows all events you
                        organized, and the Events I’m Participating In tab lists the events you are participating in or have
                        participated in.
                    </Typography>
                    <Box sx={{ marginY: 2 }} />
                    <Typography variant="h5" color="textSecondary">
                        On any of the event galleries, you can press the plus button in the bottom right to create a new event.
                        On the event page, click the edit icon in the top right to add a title, description, and budget. You can
                        invite others by adding them manually or sharing the event link.
                    </Typography>
                </>
            )}
        </Box>
    )
}