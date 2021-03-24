import { ComponentFixture, TestBed, waitForAsync } from "@angular/core/testing";
import { MenuItemComponent } from "./menu-item.component";
import { RouterTestingModule } from "@angular/router/testing";
import { FormsModule } from "@angular/forms";
import { TranslateModule } from "@ngx-translate/core";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { LoginComponent } from "../login/login.component";
import { DashboardComponent } from "../dashboard/dashboard.component";
import { ReportingComponent } from "../reporting/reporting.component";
import { TransactionReportingComponent } from "../transaction-reporting/transaction-reporting.component";
import { Router } from "@angular/router";

describe("MenuItemComponent", () => {
  let component: MenuItemComponent;
  let fixture: ComponentFixture<MenuItemComponent>;
  let router: Router;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          FormsModule,
          HttpClientTestingModule,
          TranslateModule.forRoot(),
        ],
        declarations: [MenuItemComponent],
        providers: [
          {
            provide: Router,
            useValue: {
              url: "/dashboard",
            },
          },
        ],
      });
      fixture = TestBed.createComponent(MenuItemComponent);
      component = fixture.componentInstance;
      component.route = { path: "login", component: LoginComponent };
      router = TestBed.inject(Router);
      fixture.detectChanges();
    })
  );

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should be hidden if route does not have a title", () => {
    component.route = {
      path: "login",
      component: LoginComponent,
    };
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector(".menu-item")).toBeFalsy();
  });

  it("should display if route has a title", () => {
    component.route = {
      path: "dashboard",
      component: DashboardComponent,
      data: {
        title: "Dashboard",
      },
    };
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector(".menu-item")).toBeTruthy();
  });

  it("should display toggle if route has children", () => {
    component.route = {
      path: "reporting",
      component: ReportingComponent,
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
        },
      ],
    };
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector(".toggle")).toBeTruthy();
  });

  it("should display children if it has children", () => {
    component.route = {
      path: "reporting",
      component: ReportingComponent,
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
        },
      ],
    };
    component.expanded = true;
    fixture.detectChanges();

    expect(
      fixture.nativeElement.querySelectorAll(".menu-item").length
    ).toBeGreaterThan(1);
  });

  it("should assemble path based on parent path", () => {
    component.fullPath = "/parent";
    component.route.path = "child";
    component.assemblePath();
    fixture.detectChanges();

    expect(component.fullPath).toBe("/parent/child");
  });

  it("should set menu link item as active if it is the current active route", () => {
    component.route = { path: "dashboard", component: DashboardComponent };
    fixture.detectChanges();
    expect(component.isActivatedRoute()).toBeTruthy();
  });
});
