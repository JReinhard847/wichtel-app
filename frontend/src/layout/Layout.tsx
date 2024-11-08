import { Outlet } from 'react-router-dom';
import { DashboardLayout } from '@toolpad/core/DashboardLayout';
import { PageContainer } from '@toolpad/core/PageContainer';

import SessionButton from "../components/SessionButton.tsx";

export default function Layout() {


    return (
        <DashboardLayout
        slots={{toolbarActions: SessionButton}}
        defaultSidebarCollapsed={true}>
            <PageContainer>
                <Outlet />
            </PageContainer>
        </DashboardLayout>
    );
}
