import {AppProvider} from '@toolpad/core/react-router-dom';
import {Outlet} from "react-router-dom";
import {createTheme} from "@mui/material";
import {green, grey, red} from "@mui/material/colors";
import type {Navigation} from "@toolpad/core";
import EventIcon from '@mui/icons-material/Event';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import FlagCircleIcon from '@mui/icons-material/FlagCircle';
import RedeemIcon from '@mui/icons-material/Redeem';
import {useEffect, useState} from "react";
import axios from "axios";

function App() {


    const [loggedIn, setLoggedIn] = useState(false)

    useEffect(() => {
        axios.get("api/user/me")
            .then(() => setLoggedIn(true))
            .catch(() => setLoggedIn(false))
    }, []);

    const customTheme = createTheme({
        cssVariables: {
            colorSchemeSelector: 'data-toolpad-color-scheme', // Selector for theme mode (light/dark)
        },
        colorSchemes: {
            light: {
                palette: {
                    mode: 'light',
                    primary: {
                        main: red[700], // Holiday red for light mode
                        contrastText: '#ffffff',
                    },
                    secondary: {
                        main: green[500], // Holiday green for light mode
                        contrastText: '#ffffff',
                    },
                    background: {
                        default: '#F9F9FE', // Light background
                        paper: '#EEEEF9',
                    },
                    text: {
                        primary: '#333333',
                        secondary: grey[700],
                    }
                },
            },
            dark: {
                palette: {
                    mode: 'dark',
                    primary: {
                        main: red[300], // Softer holiday red for dark mode
                        contrastText: '#ffffff',
                    },
                    secondary: {
                        main: green[300], // Softer holiday green for dark mode
                        contrastText: '#ffffff',
                    },
                    background: {
                        default: '#2A4364', // Darker background for night theme
                        paper: '#112E4D',
                    },
                    text: {
                        primary: '#ffffff',
                        secondary: grey[300],
                    }
                },
            },
        },
        breakpoints: {
            values: {
                xs: 0,
                sm: 600,
                md: 600,
                lg: 1200,
                xl: 1536,
            },
        },
    });


    const NAVIGATION: Navigation = [
        ...(loggedIn ? [
            {
                title: "Update User Info",
                segment: "me",
                icon: <AccountCircleIcon/>
            }
        ] : []),
        {
            kind: "header",
            title: "Events"
        },
        {
            title: "Open Events",
            segment: "events",
            icon: <EventIcon/>
        },
        ...(loggedIn ? [
            {
                title: "Organized by me",
                segment: "events/by-me",
                icon: <FlagCircleIcon/>
            },
            {
                title: "Events I'm participating in",
                segment: "events/with-me",
                icon: <RedeemIcon/>
            }
        ] : [])
    ];


    return (
        <AppProvider
            theme={customTheme}
            branding={{title: "Wichtel App", logo: <img src="/src/assets/christmas-hat.png" alt=""/>}}
            navigation={NAVIGATION}
        >
            <Outlet/>
        </AppProvider>
    )
}

export default App
