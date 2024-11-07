import {Button} from "@mui/material";
import {useEffect, useState} from "react";
import axios from "axios";

export default function SessionButton() {


    const [loggedIn, setLoggedIn] = useState<boolean>(false)


    useEffect(() => {
        axios.get("api/user/me")
            .then(() => setLoggedIn(true))
            .catch(() => setLoggedIn(false))
    }, []);


    function login() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin
        window.open(host + '/oauth2/authorization/github', '_self')
    }

    function logout() {
        const host = window.location.host === 'localhost:5173' ? 'http://localhost:8080' : window.location.origin
        window.open(host + '/api/auth/logout', '_self')
    }


    return (
        <Button
            variant="contained"
            color="secondary"
            onClick={loggedIn ? logout : login}>{loggedIn ? "logout" : "login"}</Button>
    );
}