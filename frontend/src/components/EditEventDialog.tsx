import {WichtelEvent} from "../util/Utils.ts";
import {FormEvent} from "react";
import {Box, Button, Dialog, DialogTitle, TextField} from "@mui/material";
import axios from "axios";
import {useNotifications} from "@toolpad/core";

type EditEventDialogProps = {
    open: boolean,
    setOpen: (value: boolean) => void,
    fetchData: () => void,
    event: WichtelEvent,
    setEvent: (e: WichtelEvent) => void
}

export default function EditEventDialog(props: EditEventDialogProps) {
    const notifications = useNotifications()

    function handleClose() {
        props.setOpen(false)
    }

    function handleSubmit(e: FormEvent<HTMLFormElement>) {
        e.preventDefault()

        axios.put("/api/event/" + props.event.id, props.event)
            .then(() => notifications.show("Event updated successfully",
                {
                    autoHideDuration: 3000,
                    severity: "success"
                }))
            .catch(() => notifications.show("Failed to update event",
                {
                    autoHideDuration: 3000,
                    severity: "error"
                }))
            .finally(() => {
                    props.fetchData()
                    handleClose()
                }
            )
    }

    return (
        <Dialog
            open={props.open}
            onClose={handleClose}
            PaperProps={{
                component: 'form',
                onSubmit: handleSubmit,
                sx: {
                    width: 400
                }
            }}
        >
            <DialogTitle>Update Event Data</DialogTitle>
            <TextField
                required
                margin="dense"
                id="title"
                name="title"
                label="Title"
                type="text"
                fullWidth
                variant="filled"
                value={props.event.title}
                onChange={(e) => props.setEvent({
                        ...props.event,
                        title: e.target.value
                    }
                )}
            />
            <TextField
                autoFocus
                margin="dense"
                id="name"
                name="description"
                label="Description"
                type="text"
                fullWidth
                variant="filled"
                multiline={true}
                minRows={3}
                maxRows={6}
                value={props.event.description}
                onChange={(e) => props.setEvent({
                        ...props.event,
                        description: e.target.value
                    }
                )}
            />
            <TextField
                required
                margin="dense"
                id="budget"
                name="budget"
                label="Budget"
                type="text"
                fullWidth
                variant="filled"
                value={props.event.budget}
                onChange={(e) => props.setEvent({
                        ...props.event,
                        budget: e.target.value
                    }
                )}
            />


            <Box display="flex" justifyContent="space-around" alignItems="center" my={1}>
                <Button variant="contained" onClick={handleClose}>Cancel</Button>
                <Button variant="contained" color="secondary" type="submit">Save</Button>
            </Box>

        </Dialog>
    )
}