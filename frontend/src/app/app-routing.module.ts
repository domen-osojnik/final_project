import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DriveInformationComponent } from './tabs/drive-information/drive-information.component';
import { DashboardComponent } from './tabs/dashboard/dashboard.component';
import { DoesNotExistComponent } from './info-pages/does-not-exist/does-not-exist.component';
import { InfoComponent } from './tabs/info/info.component';
import { SignTrackerComponent } from './tabs/sign-tracker/sign-tracker.component';


const routes: Routes = [
  { path: 'driveInfo', component: DriveInformationComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'info', component: InfoComponent },
  { path: 'signTracker', component: SignTrackerComponent },
  { path: '',
    redirectTo: '/info',
    pathMatch: 'full' },
  { path: '**', component: DoesNotExistComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
