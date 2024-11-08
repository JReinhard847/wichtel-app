import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import App from './App.tsx'
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Layout from "./layout/Layout.tsx";
import OpenEventsPage from "./pages/OpenEventsPage.tsx";
import LandingPage from "./pages/LandingPage.tsx";
import SingleEventPage from "./pages/SingleEventPage.tsx";
import EventsByMePage from "./pages/EventsByMePage.tsx";
import EventsWithMePage from "./pages/EventsWithMePage.tsx";
import UserInfoPage from "./pages/UserInfoPage.tsx";

const router = createBrowserRouter([
    {
        Component: App, // root layout route
        children: [
            {
                path: '/',
                Component: Layout,
                children: [
                    {
                        path: '',
                        Component: LandingPage
                    },
                    {
                        path: 'me',
                        Component: UserInfoPage
                    },
                    {
                        path: 'events',
                        Component: OpenEventsPage
                    },
                    {
                        path: 'events/by-me',
                        Component: EventsByMePage
                    },
                    {
                        path: 'events/with-me',
                        Component: EventsWithMePage
                    },
                    {
                        path: 'events/:id',
                        Component: SingleEventPage
                    }
                ]
            },
        ],
    },
]);


createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <RouterProvider router={router}/>
    </StrictMode>,
)
