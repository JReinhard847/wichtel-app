import {Fab} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import axios from "axios";
import {useNavigate} from "react-router-dom";


export default function AddNewEventButton() {

    const navigate = useNavigate()

    function handleAddEvent() {
        axios.post("/api/event")
            .then(response => navigate("/events/" + response.data))
    }


    return (<Fab
            color="secondary"
            sx={{
                position: "fixed",
                bottom: 70,
                right: 50
            }} onClick={handleAddEvent}> <AddIcon/></Fab>
    )
}