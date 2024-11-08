import isParticipatingIn, {WichtelEvent, WichtelUser} from "../util/Utils.ts";
import {SyntheticEvent, useEffect, useState} from "react";
import axios from "axios";
import {Autocomplete, Box, Button, Dialog, DialogTitle, TextField} from "@mui/material";
import {useNotifications} from "@toolpad/core";

type AddUsersToEventDialogProps = {
    open: boolean,
    setOpen: (b: boolean) => void,
    event: WichtelEvent,
    fetchData: () => void,
}

export default function AddUsersToEventDialog(props: AddUsersToEventDialogProps) {

    const [users, setUsers] = useState<WichtelUser[]>([])
    const [selected, setSelected] = useState<WichtelUser[]>([])

    const notifications = useNotifications()

    useEffect(() => {
        if (props.open) {
            axios.get<WichtelUser[]>("/api/user")
                .then(response => setUsers(response.data.filter(user => !isParticipatingIn(user, props.event))))
                .catch(err => console.log(err))
        }
    }, [props.open]);

    function handleClose() {
        props.setOpen(false)
    }

    function handleChange(_: SyntheticEvent<Element, Event>, newValue: WichtelUser[]) {
        setSelected(newValue)
    }

    function handleSubmit() {
        const requests = selected.map((user) =>
            axios.post("/api/event/" + props.event.id + "/" + user.id)
                .then(() => console.log("added user " + user.name))
                .catch(err => console.log(err)))

        Promise.all(requests)
            .then(() => notifications.show("Added all selected users to event", {
                autoHideDuration: 3000,
                severity: "success"
            }))
            .catch(() => notifications.show("There was an error adding users to this event",
                {
                    autoHideDuration: 3000,
                    severity: "error"
                }))
            .finally(() => {
                handleClose()
                props.fetchData()
            })
    }


    return (
        <Dialog
            PaperProps={{
                sx: {
                    width: 400
                }
            }}
            open={props.open}>
            <DialogTitle>Add users to this event</DialogTitle>
            <Autocomplete
                multiple
                renderInput={(params) => <TextField {...params} label="Select users to add them to the event"/>}
                options={users}
                onChange={(event, newValue) => handleChange(event, newValue)}
                getOptionLabel={(option) => option.name}
                filterOptions={(options, {inputValue}) => {
                    return options.filter(
                        (option) =>
                            option.name.toLowerCase().includes(inputValue.toLowerCase()) ||
                            (option.oauthName && option.oauthName.toLowerCase().includes(inputValue.toLowerCase()))
                    );
                }}
                renderOption={(props, option) => (
                    <li {...props} key={option.id}>
                        {option.name} {option.oauthName ? `(${option.oauthName} on ${option.oauthProvider})` : ''}
                    </li>
                )}
            />
            <Box display="flex" justifyContent="space-around">
                <Button variant="contained" onClick={handleClose}>Cancel</Button>
                <Button variant="contained" color="secondary" onClick={handleSubmit}>Add</Button>
            </Box>
        </Dialog>
    )
}