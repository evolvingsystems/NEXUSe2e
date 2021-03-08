import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { HttpClient, HttpClientModule } from "@angular/common/http";
import { RouterModule, Routes } from "@angular/router";

import { AppComponent } from "./app.component";
import { LoginComponent } from "./login/login.component";
import { DashboardComponent } from "./dashboard/dashboard.component";
import { AuthGuardService } from "./data/auth-guard.service";
import { FormsModule } from "@angular/forms";
import { PageNotFoundComponent } from "./page-not-found/page-not-found.component";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { TranslateLoader, TranslateModule } from "@ngx-translate/core";
import { TranslateHttpLoader } from "@ngx-translate/http-loader";
import { MatInputModule } from "@angular/material/input";
import { MatButtonModule } from "@angular/material/button";
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from "@angular/material/form-field";
import { HeaderComponent } from "./header/header.component";
import { NavigationComponent } from "./navigation/navigation.component";
import { MatIconModule } from "@angular/material/icon";
import { SessionPanelComponent } from "./session-panel/session-panel.component";
import { ReportingComponent } from "./reporting/reporting.component";
import { TransactionReportingComponent } from "./transaction-reporting/transaction-reporting.component";
import { EngineLogComponent } from "./engine-log/engine-log.component";
import { MenuItemComponent } from "./menu-item/menu-item.component";
import { MessageListComponent } from "./message-list/message-list.component";
import { ConversationListComponent } from "./conversation-list/conversation-list.component";

const routes: Routes = [
  { path: "", redirectTo: "/dashboard", pathMatch: "full" },
  { path: "login", component: LoginComponent },
  {
    path: "dashboard",
    component: DashboardComponent,
    canActivate: [AuthGuardService],
    data: {
      title: "Dashboard",
    },
  },
  {
    path: "reporting",
    component: ReportingComponent,
    canActivate: [AuthGuardService],
    data: {
      title: "Reporting",
    },
    children: [
      {
        path: "transaction-reporting",
        component: TransactionReportingComponent,
        data: {
          title: "Transaction Reporting",
        },
        children: [
          {path: '', redirectTo: 'conversations', pathMatch: 'full'},
          {
            path: "conversations",
            component: ConversationListComponent,
            data: {
              title: "Conversations",
            },
          },
          {
            path: "messages",
            component: MessageListComponent,
            data: {
              title: "Messages",
            },
          },
        ],
      },
      {
        path: "engine-log",
        component: EngineLogComponent,
        data: {
          title: "Engine Log",
        },
      },
    ],
  },
  { path: "**", component: PageNotFoundComponent },
];

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, "./assets/i18n/");
}

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    PageNotFoundComponent,
    HeaderComponent,
    NavigationComponent,
    SessionPanelComponent,
    ReportingComponent,
    TransactionReportingComponent,
    EngineLogComponent,
    MenuItemComponent,
    MessageListComponent,
    ConversationListComponent,
  ],
  imports: [
    BrowserModule,
    TranslateModule.forRoot({
      defaultLanguage: "en",
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient],
      },
    }),
    HttpClientModule,
    RouterModule.forRoot(routes, { useHash: true }),
    FormsModule,
    NoopAnimationsModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
  ],
  providers: [
    {
      provide: MAT_FORM_FIELD_DEFAULT_OPTIONS,
      useValue: {
        appearance: "outline",
      },
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
