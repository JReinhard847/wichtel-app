import {WichtelUser} from "../util/Utils.ts";
import {ChangeEvent, useEffect, useState} from "react";
import axios from "axios";
import {Box, Button, TextField, Typography} from "@mui/material";
import {useNotifications} from "@toolpad/core";

export default function UserInfoPage() {

    const [user, setUser] = useState<WichtelUser>()
    const notifications = useNotifications()

    useEffect(() => {
        axios.get("api/user/me")
            .then(response => setUser(response.data))
            .catch(err => console.log(err))
    }, []);

    function logout() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin
        window.open(host + '/api/auth/logout', '_self')
    }

    function handleChange(e: ChangeEvent<HTMLInputElement>) {
        const {name, value} = e.target;
        if (user) setUser({...user, [name]: value});
    }

    function handleSubmit() {
        axios.put("/api/user", user)
            .then(response => {
                setUser(response.data)
                notifications.show("User Data updated successfully!",
                    {
                        autoHideDuration: 3000,
                        severity: "info"
                    })
            })
            .catch(() => notifications.show("Error updating User Data",
                {
                    autoHideDuration: 3000,
                    severity: "error"
                }))
    }

    function handleDelete() {
        axios.delete("api/user/" + user?.id)
            .then(() => logout())
    }

    return (
        <Box component="form" sx={{display: 'flex', flexDirection: 'column', gap: 2, width: 300}}>

            {user?.oauthProvider && user?.oauthName && (
                <Typography variant="body2" color="textSecondary">
                    Logged in as {user.oauthName} via {user.oauthProvider}
                </Typography>)}


            <TextField
                label="Name"
                name="name"
                value={user?.name || ''}
                onChange={handleChange}
                required
            />
            <TextField
                label="Email"
                name="email"
                type="email"
                value={user?.email || ''}
                onChange={handleChange}
                required
            />

            <Button variant="contained" color="secondary" onClick={handleSubmit}>
                {'Update'}
            </Button>
            <Button variant="contained" color="error" onClick={handleDelete}>
                Delete Me
            </Button>
        </Box>
    )
}