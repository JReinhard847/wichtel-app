import {Box, IconButton, ListItem, ListItemText, Paper} from "@mui/material";
import {WichtelUser} from "../util/Utils.ts";
import ClearIcon from '@mui/icons-material/Clear';

type ParticipantListItemProps = {
    participant: WichtelUser
    canRemove: boolean
    onRemove: (user:WichtelUser) => void;
}

export default function ParticipantListItem(props: ParticipantListItemProps) {


    return (
        <Paper
            elevation={15}
            sx={{
                my: 1,
                width: 300
            }}>
            <ListItem
                sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}
            >
                <Box flexGrow={1}>
                    <ListItemText
                        primary={props.participant.name}
                        secondary={
                            props.participant.oauthName
                                ? `aka ${props.participant.oauthName} on ${props.participant.oauthProvider}`
                                : props.participant.oauthProvider ?? ""
                        }
                    />
                </Box>
                {props.canRemove && (
                    <IconButton color="primary" edge="end" onClick={() => props.onRemove && props.onRemove(props.participant)}>
                        <ClearIcon/>
                    </IconButton>
                )}
            </ListItem>
        </Paper>
    )
}